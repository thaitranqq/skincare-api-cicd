package com.example.demo.identity.service;

import com.example.demo.security.JwtUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {

    private final JwtUtil jwtUtil;
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;
    private final PasswordEncoder passwordEncoder;

    // In-memory stores for OTPs and refresh tokens (simple demo implementation)
    private final Map<String, String> otpStore = new ConcurrentHashMap<>(); // key -> code
    private final Map<String, Instant> otpExpiry = new ConcurrentHashMap<>(); // key -> expiry
    private final Map<String, Long> refreshStore = new ConcurrentHashMap<>(); // refreshToken -> userId

    public AuthService(JwtUtil jwtUtil, JdbcTemplate jdbcTemplate, ObjectMapper objectMapper, PasswordEncoder passwordEncoder) {
        this.jwtUtil = jwtUtil;
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public void signup(Map<String, Object> body) {
        Objects.requireNonNull(body);
        String email = body.get("email") != null ? body.get("email").toString().trim() : null;
        String password = body.get("password") != null ? body.get("password").toString() : null;
        String phone = body.get("phone") != null ? body.get("phone").toString() : null;

        if (email == null && phone == null) throw new RuntimeException("email or phone required");
        if (password == null || password.length() < 6) throw new RuntimeException("password required (min 6 chars)");

        // check existing
        List<Map<String, Object>> exists = jdbcTemplate.queryForList("SELECT id FROM users WHERE email = ? LIMIT 1", email);
        if (!exists.isEmpty()) throw new RuntimeException("email already registered");

        String hashed = passwordEncoder.encode(password);

        // insert user and get generated id
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO users (email, phone, password_hash, status) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, email);
            ps.setString(2, phone);
            ps.setString(3, hashed);
            ps.setString(4, "ACTIVE");
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        Long userId = (key != null) ? key.longValue() : null;

        // assign default USER role
        if (userId != null) {
            Long roleId = null;
            try {
                roleId = jdbcTemplate.queryForObject("SELECT id FROM roles WHERE code = 'USER' LIMIT 1", Long.class);
            } catch (Exception ignored) {}
            if (roleId == null) {
                // create USER role
                KeyHolder rk = new GeneratedKeyHolder();
                jdbcTemplate.update(conn -> {
                    PreparedStatement ps = conn.prepareStatement("INSERT INTO roles (code) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, "USER");
                    return ps;
                }, rk);
                roleId = rk.getKey().longValue();
            }
            jdbcTemplate.update("INSERT INTO user_roles (user_id, role_id) VALUES (?, ?)", userId, roleId);
        }

        // In a real app: send verification email/SMS here
    }

    public Map<String, Object> signin(Map<String, Object> body) {
        Objects.requireNonNull(body);
        // Expecting body contains email and password
        String email = body.get("email") != null ? body.get("email").toString() : null;
        String password = body.get("password") != null ? body.get("password").toString() : null;
        if (email == null || password == null) throw new RuntimeException("email and password required");

        // find user by email
        List<Map<String, Object>> users = jdbcTemplate.queryForList(
                "SELECT id, email, phone, status, password_hash FROM users WHERE email = ? LIMIT 1",
                email
        );
        if (users.isEmpty()) throw new RuntimeException("Invalid credentials");
        Map<String, Object> userRow = users.get(0);
        Long userId = ((Number) userRow.get("id")).longValue();

        String pwHash = (String) userRow.get("password_hash");
        System.out.println(passwordEncoder.matches(password,pwHash));
        System.out.println(pwHash);
        System.out.println(new BCryptPasswordEncoder().encode("123456"));
        if (pwHash == null || !passwordEncoder.matches(password, pwHash)) {
            throw new RuntimeException("Invalid credentials");
        }

        // load roles (use non-deprecated jdbcTemplate.query signature)
        List<String> roles = jdbcTemplate.query(
                "SELECT r.code FROM roles r JOIN user_roles ur ON r.id = ur.role_id WHERE ur.user_id = ?",
                (rs, rowNum) -> rs.getString("code"),
                userId
        );
        if (roles.isEmpty()) roles = List.of("USER");

        String access = jwtUtil.generateToken(userId, email, roles);
        String refresh = UUID.randomUUID().toString();
        refreshStore.put(refresh, userId);

        Map<String, Object> tokens = new HashMap<>();
        tokens.put("accessToken", access);
        tokens.put("refreshToken", refresh);
        return tokens;
    }

    public void requestOtp(Map<String, Object> body) {
        Objects.requireNonNull(body);
        String dest = body.get("phone") != null ? body.get("phone").toString() : (body.get("email") != null ? body.get("email").toString() : null);
        if (dest == null) throw new RuntimeException("phone or email required");
        String code = String.valueOf((int) (Math.random() * 900000) + 100000); // 6-digit
        otpStore.put(dest, code);
        otpExpiry.put(dest, Instant.now().plusSeconds(300)); // 5 minutes
        // In production: send via SMS/Email. Here we persist a lightweight event for debugging.
        jdbcTemplate.update("INSERT INTO events (user_id, type, payload_json) VALUES (?, ?, ?)", null, "OTP_REQUEST", "{\"dest\":\"" + dest + "\",\"code\":\"" + code + "\"}");
    }

    public void verifyOtp(Map<String, Object> body) {
        Objects.requireNonNull(body);
        String dest = body.get("phone") != null ? body.get("phone").toString() : (body.get("email") != null ? body.get("email").toString() : null);
        String code = body.get("code") != null ? body.get("code").toString() : null;
        if (dest == null || code == null) throw new RuntimeException("phone/email and code required");
        String stored = otpStore.get(dest);
        Instant exp = otpExpiry.get(dest);
        if (stored == null || exp == null || Instant.now().isAfter(exp) || !stored.equals(code)) {
            throw new RuntimeException("Invalid or expired code");
        }
        // consume
        otpStore.remove(dest);
        otpExpiry.remove(dest);
        // Optionally: create user if not exists (for passwordless flows). Not implemented here.
    }

    public Map<String, Object> oauthLogin(String provider, Map<String, Object> body) {
        Objects.requireNonNull(provider);
        Objects.requireNonNull(body);
        // TODO: validate id_token with provider and upsert user
        // For now try to find a linked user by provider_uid if provided, else fallback to local user
        String providerUid = body.get("provider_uid") != null ? body.get("provider_uid").toString() : null;
        if (providerUid != null) {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                    "SELECT u.id, u.email FROM users u JOIN user_identities ui ON ui.user_id = u.id WHERE ui.provider = ? AND ui.provider_uid = ? LIMIT 1",
                    provider, providerUid
            );
            if (!rows.isEmpty()) {
                Map<String, Object> r = rows.get(0);
                Long userId = ((Number) r.get("id")).longValue();
                String email = (String) r.get("email");
                List<String> roles = jdbcTemplate.query(
                        "SELECT r.code FROM roles r JOIN user_roles ur ON r.id = ur.role_id WHERE ur.user_id = ?",
                        (rs, rowNum) -> rs.getString("code"),
                        userId
                );
                String access = jwtUtil.generateToken(userId, email, roles.isEmpty() ? List.of("USER") : roles);
                return Map.of("accessToken", access, "refreshToken", "demo-oauth-refresh");
            }
        }

        // fallback: create or return demo token for id 1
        Long userId = 1L;
        String email = "oauth@demo.local";
        List<String> roles = List.of("USER");
        String access = jwtUtil.generateToken(userId, email, roles);
        Map<String, Object> tokens = new HashMap<>();
        tokens.put("accessToken", access);
        tokens.put("refreshToken", "demo-oauth-refresh");
        return tokens;
    }

    public Map<String, String> refresh(String refreshToken) {
        Objects.requireNonNull(refreshToken);
        Long userId = refreshStore.get(refreshToken);
        if (userId == null) throw new RuntimeException("Invalid refresh token");
        // fetch basic user for email
        String email = jdbcTemplate.queryForObject("SELECT email FROM users WHERE id = ?", String.class, userId);
        List<String> roles = jdbcTemplate.query(
                "SELECT r.code FROM roles r JOIN user_roles ur ON r.id = ur.role_id WHERE ur.user_id = ?",
                (rs, rowNum) -> rs.getString("code"),
                userId
        );
        String access = jwtUtil.generateToken(userId, email, roles.isEmpty() ? List.of("USER") : roles);
        // Optionally rotate refresh token
        String newRefresh = UUID.randomUUID().toString();
        refreshStore.remove(refreshToken);
        refreshStore.put(newRefresh, userId);
        return Map.of("accessToken", access, "refreshToken", newRefresh);
    }

    public Map<String, Object> getMe(Long userId) {
        Objects.requireNonNull(userId);
        Map<String, Object> result = new HashMap<>();
        // user basic
        List<Map<String, Object>> users = jdbcTemplate.queryForList(
                "SELECT id, email, phone, status, created_at FROM users WHERE id = ? LIMIT 1",
                userId
        );
        if (users.isEmpty()) throw new RuntimeException("user not found");
        Map<String, Object> u = users.get(0);
        result.put("id", ((Number) u.get("id")).longValue());
        result.put("email", u.get("email"));
        result.put("phone", u.get("phone"));
        result.put("status", u.get("status"));
        result.put("createdAt", u.get("created_at"));

        // profile: parse JSON columns into proper objects
        List<Map<String, Object>> profiles = jdbcTemplate.queryForList(
                "SELECT skin_type, concerns, allergies, pregnant, conditions, lifestyle_json, goals FROM profiles WHERE user_id = ?",
                userId
        );
        if (!profiles.isEmpty()) {
            Map<String, Object> p = profiles.get(0);
            Map<String, Object> profileOut = new HashMap<>();
            profileOut.put("skinType", p.get("skin_type"));

            try {
                Object concernsObj = p.get("concerns");
                if (concernsObj != null) {
                    List<String> concerns = objectMapper.readValue(concernsObj.toString(), new TypeReference<>() {});
                    profileOut.put("concerns", concerns);
                }
            } catch (Exception ignored) {}

            try {
                Object allergiesObj = p.get("allergies");
                if (allergiesObj != null) {
                    List<String> allergies = objectMapper.readValue(allergiesObj.toString(), new TypeReference<>() {});
                    profileOut.put("allergies", allergies);
                }
            } catch (Exception ignored) {}

            profileOut.put("pregnant", p.get("pregnant"));

            try {
                Object conditionsObj = p.get("conditions");
                if (conditionsObj != null) {
                    List<String> conditions = objectMapper.readValue(conditionsObj.toString(), new TypeReference<>() {});
                    profileOut.put("conditions", conditions);
                }
            } catch (Exception ignored) {}

            try {
                Object lifestyleObj = p.get("lifestyle_json");
                if (lifestyleObj != null) {
                    Map<String, Object> lifestyle = objectMapper.readValue(lifestyleObj.toString(), new TypeReference<>() {});
                    profileOut.put("lifestyle", lifestyle);
                }
            } catch (Exception ignored) {}

            try {
                Object goalsObj = p.get("goals");
                if (goalsObj != null) {
                    List<String> goals = objectMapper.readValue(goalsObj.toString(), new TypeReference<>() {});
                    profileOut.put("goals", goals);
                }
            } catch (Exception ignored) {}

            result.put("profile", profileOut);
        }

        // roles
        List<String> roles = jdbcTemplate.query(
                "SELECT r.code FROM roles r JOIN user_roles ur ON r.id = ur.role_id WHERE ur.user_id = ?",
                (rs, rowNum) -> rs.getString("code"),
                userId
        );
        result.put("roles", roles);

        return result;
    }

    public void updateMe(Long userId, Map<String, Object> body) {
        Objects.requireNonNull(userId);
        Objects.requireNonNull(body);
        if (body.containsKey("email")) {
            jdbcTemplate.update("UPDATE users SET email = ? WHERE id = ?", body.get("email"), userId);
        }
        if (body.containsKey("phone")) {
            jdbcTemplate.update("UPDATE users SET phone = ? WHERE id = ?", body.get("phone"), userId);
        }
        // update profile table fields if present
        if (body.containsKey("profile")) {
            Object profileObj = body.get("profile");
            Map<String, Object> profileMap = new HashMap<>();
            if (profileObj instanceof Map<?, ?>) {
                Map<?, ?> m = (Map<?, ?>) profileObj;
                for (Map.Entry<?, ?> e : m.entrySet()) {
                    profileMap.put(String.valueOf(e.getKey()), e.getValue());
                }
            } else {
                try {
                    profileMap = objectMapper.readValue(profileObj.toString(), new TypeReference<>() {});
                } catch (Exception e) {
                    // fallback: store raw string into lifestyle_json
                    profileMap.put("lifestyle_json", profileObj.toString());
                }
            }

            // prepare JSON strings for JSON columns
            String concernsJson = null, allergiesJson = null, conditionsJson = null, lifestyleJson = null, goalsJson = null;
            try { if (profileMap.containsKey("concerns")) concernsJson = objectMapper.writeValueAsString(profileMap.get("concerns")); } catch (Exception ignored) {}
            try { if (profileMap.containsKey("allergies")) allergiesJson = objectMapper.writeValueAsString(profileMap.get("allergies")); } catch (Exception ignored) {}
            try { if (profileMap.containsKey("conditions")) conditionsJson = objectMapper.writeValueAsString(profileMap.get("conditions")); } catch (Exception ignored) {}
            try { if (profileMap.containsKey("lifestyle_json")) lifestyleJson = objectMapper.writeValueAsString(profileMap.get("lifestyle_json")); else if (profileMap.containsKey("lifestyle")) lifestyleJson = objectMapper.writeValueAsString(profileMap.get("lifestyle")); } catch (Exception ignored) {}
            try { if (profileMap.containsKey("goals")) goalsJson = objectMapper.writeValueAsString(profileMap.get("goals")); } catch (Exception ignored) {}

            String skinType = profileMap.containsKey("skinType") ? profileMap.get("skinType").toString() : (profileMap.containsKey("skin_type") ? profileMap.get("skin_type").toString() : null);
            Boolean pregnant = null;
            if (profileMap.containsKey("pregnant")) {
                Object p = profileMap.get("pregnant");
                if (p instanceof Boolean) pregnant = (Boolean) p; else pregnant = Boolean.valueOf(p.toString());
            }

            // Upsert into profiles
            jdbcTemplate.update(
                    "INSERT INTO profiles (user_id, skin_type, concerns, allergies, pregnant, conditions, lifestyle_json, goals) VALUES (?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE skin_type=VALUES(skin_type), concerns=VALUES(concerns), allergies=VALUES(allergies), pregnant=VALUES(pregnant), conditions=VALUES(conditions), lifestyle_json=VALUES(lifestyle_json), goals=VALUES(goals)",
                    userId,
                    skinType,
                    concernsJson,
                    allergiesJson,
                    pregnant,
                    conditionsJson,
                    lifestyleJson,
                    goalsJson
            );
        }
    }
}
