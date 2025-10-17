package com.example.demo.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final JdbcTemplate jdbcTemplate;

    @Value("${app.oauth2.redirect-uri:http://localhost:3000/oauth/redirect}")
    private String redirectUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        DefaultOAuth2User oauthUser = (DefaultOAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oauthUser.getAttributes();
        String email = (String) attributes.get("email");
        String providerId = (String) attributes.get("sub");

        // 1. Find or create user
        long userId = findOrCreateUser(email, providerId);

        // 2. Load roles
        List<String> roles = jdbcTemplate.query(
                "SELECT r.code FROM roles r JOIN user_roles ur ON r.id = ur.role_id WHERE ur.user_id = ?",
                (rs, rowNum) -> rs.getString("code"),
                userId
        );
        if (roles.isEmpty()) {
            roles = List.of("USER");
        }

        // 3. Generate JWT
        String token = jwtUtil.generateToken(userId, email, roles);

        // 4. Redirect
        String targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("token", token)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private long findOrCreateUser(String email, String providerId) {
        // Check if user exists by email
        List<Map<String, Object>> users = jdbcTemplate.queryForList("SELECT id FROM users WHERE email = ? LIMIT 1", email);

        long userId;
        if (!users.isEmpty()) {
            userId = ((Number) users.get(0).get("id")).longValue();
        } else {
            // Create new user
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO users (email, status) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, email);
                ps.setString(2, "ACTIVE");
                return ps;
            }, keyHolder);
            Number key = keyHolder.getKey();
            Objects.requireNonNull(key, "Failed to create user from OAuth2 login.");
            userId = key.longValue();

            // Assign default USER role
            long roleId = getOrCreateUserRoleId();
            jdbcTemplate.update("INSERT INTO user_roles (user_id, role_id) VALUES (?, ?)", userId, roleId);
        }

        // Link OAuth2 identity
        jdbcTemplate.update(
            "INSERT INTO user_identities (user_id, provider, provider_uid) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE provider_uid = VALUES(provider_uid)",
            userId, "google", providerId
        );

        return userId;
    }

    private long getOrCreateUserRoleId() {
        try {
            Long roleId = jdbcTemplate.queryForObject("SELECT id FROM roles WHERE code = 'USER' LIMIT 1", Long.class);
            return roleId != null ? roleId : createRoleAndGetId();
        } catch (Exception ignored) {
            return createRoleAndGetId();
        }
    }

    private long createRoleAndGetId() {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(conn -> conn.prepareStatement("INSERT INTO roles (code) VALUES ('USER')", Statement.RETURN_GENERATED_KEYS), keyHolder);
        Number key = keyHolder.getKey();
        Objects.requireNonNull(key, "Failed to create USER role.");
        return key.longValue();
    }
}
