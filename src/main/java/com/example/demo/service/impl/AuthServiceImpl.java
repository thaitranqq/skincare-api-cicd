package com.example.demo.service.impl;

import com.example.demo.event.EventService;
import com.example.demo.event.EventType;
import com.example.demo.security.JwtUtil;
import com.example.demo.service.AuthService;
import com.example.demo.repository.FeedbackRepository; // Import FeedbackRepository
import com.example.demo.model.Feedback; // Import Feedback model
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    private final JwtUtil jwtUtil;
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final EventService eventService;
    private final FeedbackRepository feedbackRepository; // Inject FeedbackRepository

    @Value("${app.base-url:https://exedemo-dwgqcxhvdsd2eqgp.koreacentral-01.azurewebsites.net}")
    private String baseUrl;

    // In-memory stores for OTPs and refresh tokens (simple demo implementation)
    private final Map<String, String> otpStore = new ConcurrentHashMap<>(); // key -> code
    private final Map<String, Instant> otpExpiry = new ConcurrentHashMap<>(); // key -> expiry
    private final Map<String, Long> refreshStore = new ConcurrentHashMap<>(); // refreshToken -> userId

    public AuthServiceImpl(JwtUtil jwtUtil, JdbcTemplate jdbcTemplate, ObjectMapper objectMapper, PasswordEncoder passwordEncoder, JavaMailSender mailSender, EventService eventService, FeedbackRepository feedbackRepository) {
        this.jwtUtil = jwtUtil;
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
        this.eventService = eventService;
        this.feedbackRepository = feedbackRepository; // Initialize FeedbackRepository
    }

    @Override
    @Transactional
    public void signup(Map<String, Object> body) {
        Objects.requireNonNull(body);
        String email = body.get("email") != null ? body.get("email").toString().trim() : null;
        String password = body.get("password") != null ? body.get("password").toString() : null;
        String phone = body.get("phone") != null ? body.get("phone").toString() : null;

        if (email == null) throw new RuntimeException("Email is required for signup.");
        if (password == null || password.length() < 6) throw new RuntimeException("Password required (min 6 chars).");

        // Check if user already exists and is active
        try {
            Map<String, Object> existingUser = jdbcTemplate.queryForMap("SELECT status FROM users WHERE email = ? LIMIT 1", email);
            String status = (String) existingUser.get("status");
            if ("ACTIVE".equals(status)) {
                throw new RuntimeException("Email is already registered and active.");
            } else if ("PENDING".equals(status)) {
                // Optional: Resend verification email if user tries to sign up again
                // For now, just inform them.
                throw new RuntimeException("Email is already registered. Please check your email to verify your account.");
            }
        } catch (EmptyResultDataAccessException e) {
            // User does not exist, which is good. Continue.
        }

        String hashed = passwordEncoder.encode(password);

        // Insert user with PENDING status
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO users (email, phone, password_hash, status) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, email);
            ps.setString(2, phone);
            ps.setString(3, hashed);
            ps.setString(4, "PENDING"); // Set status to PENDING
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new RuntimeException("Failed to create user.");
        }
        Long userId = key.longValue();

        eventService.recordEvent(userId, EventType.REGISTER);

        // Assign default USER role
        Long roleId = getOrCreateUserRoleId();
        jdbcTemplate.update("INSERT INTO user_roles (user_id, role_id) VALUES (?, ?)", userId, roleId);

        // Create verification token and send email
        sendVerificationEmail(userId, email);
    }

    private void sendVerificationEmail(Long userId, String email) {
        String token = UUID.randomUUID().toString();
        Instant expiryDate = Instant.now().plusSeconds(86400); // 24 hours

        jdbcTemplate.update("INSERT INTO verification_tokens (user_id, token, expiry_date) VALUES (?, ?, ?)",
                userId, token, Timestamp.from(expiryDate));

        eventService.recordEvent(userId, EventType.EMAIL_VERIFICATION_SENT, Map.of("email", email));

        String verificationLink = baseUrl + "/api/v1/auth/verify-email?token=" + token;

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject("LADANV Account Verification");
        mailMessage.setText("To verify your account, please click the link below:\n" + verificationLink);
        mailSender.send(mailMessage);
    }

    @Override
    @Transactional
    public void verifyEmail(String token) {
        Map<String, Object> tokenData;
        try {
            tokenData = jdbcTemplate.queryForMap("SELECT user_id, expiry_date FROM verification_tokens WHERE token = ?", token);
        } catch (EmptyResultDataAccessException e) {
            throw new RuntimeException("Invalid verification token.");
        }

        Long userId = ((Number) tokenData.get("user_id")).longValue();
        Timestamp expiry = (Timestamp) tokenData.get("expiry_date");

        if (expiry.toInstant().isBefore(Instant.now())) {
            jdbcTemplate.update("DELETE FROM verification_tokens WHERE token = ?", token);
            throw new RuntimeException("Verification token has expired.");
        }

        // Activate user
        jdbcTemplate.update("UPDATE users SET status = 'ACTIVE' WHERE id = ?", userId);

        eventService.recordEvent(userId, EventType.EMAIL_VERIFIED);

        // Delete the used token
        jdbcTemplate.update("DELETE FROM verification_tokens WHERE token = ?", token);
    }


    @Override
    public Map<String, Object> signin(Map<String, Object> body) {
        Objects.requireNonNull(body);
        String email = body.get("email") != null ? body.get("email").toString() : null;
        String password = body.get("password") != null ? body.get("password").toString() : null;
        if (email == null || password == null) throw new RuntimeException("Email and password required.");

        Map<String, Object> userRow;
        try {
            userRow = jdbcTemplate.queryForMap("SELECT id, email, phone, status, password_hash FROM users WHERE email = ? LIMIT 1", email);
        } catch (EmptyResultDataAccessException e) {
            throw new RuntimeException("Invalid credentials.");
        }

        String status = (String) userRow.get("status");
        if ("PENDING".equals(status)) {
            throw new RuntimeException("Account is pending verification. Please check your email.");
        }
        if (!"ACTIVE".equals(status)) {
            throw new RuntimeException("Account is not active.");
        }

        String pwHash = (String) userRow.get("password_hash");
        if (pwHash == null || !passwordEncoder.matches(password, pwHash)) {
            throw new RuntimeException("Invalid credentials.");
        }

        Long userId = ((Number) userRow.get("id")).longValue();

        eventService.recordEvent(userId, EventType.LOGIN);

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

    private Long getOrCreateUserRoleId() {
        try {
            return jdbcTemplate.queryForObject("SELECT id FROM roles WHERE code = 'USER' LIMIT 1", Long.class);
        } catch (EmptyResultDataAccessException e) {
            KeyHolder rk = new GeneratedKeyHolder();
            jdbcTemplate.update(conn -> {
                PreparedStatement ps = conn.prepareStatement("INSERT INTO roles (code) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, "USER");
                return ps;
            }, rk);
            Number key = rk.getKey();
            return (key != null) ? key.longValue() : null;
        }
    }

    @Override
    public void updateMe(Long userId, Map<String, Object> body) {
        Objects.requireNonNull(userId);
        Objects.requireNonNull(body);

        // Validate if the user exists
        try {
            jdbcTemplate.queryForObject("SELECT id FROM users WHERE id = ?", Long.class, userId);
        } catch (EmptyResultDataAccessException e) {
            throw new RuntimeException("User not found with id: " + userId);
        }

        if (body.containsKey("email")) {
            jdbcTemplate.update("UPDATE users SET email = ? WHERE id = ?", body.get("email"), userId);
        }
        if (body.containsKey("phone")) {
            jdbcTemplate.update("UPDATE users SET phone = ? WHERE id = ?", body.get("phone"), userId);
        }
        if (body.containsKey("profile")) {
            Object profileObj = body.get("profile");
            
            final Map<String, Object> profileMap = new HashMap<>();
            if (profileObj instanceof Map) {
                try {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> castedMap = (Map<String, Object>) profileObj;
                    profileMap.putAll(castedMap);
                } catch (ClassCastException e) {
                    // Log or handle the error if the map is not of the expected type
                }
            } else {
                try {
                    Map<String, Object> tempMap = objectMapper.readValue(profileObj.toString(), new TypeReference<>() {});
                    profileMap.putAll(tempMap);
                } catch (Exception e) {
                    profileMap.put("lifestyle_json", profileObj.toString());
                }
            }

            String concernsJson = null, allergiesJson = null, conditionsJson = null, lifestyleJson = null, goalsJson = null;
            try { if (profileMap.containsKey("concerns")) concernsJson = objectMapper.writeValueAsString(profileMap.get("concerns")); } catch (Exception ignored) {}
            try { if (profileMap.containsKey("allergies")) allergiesJson = objectMapper.writeValueAsString(profileMap.get("allergies")); } catch (Exception ignored) {}
            try { if (profileMap.containsKey("conditions")) conditionsJson = objectMapper.writeValueAsString(profileMap.get("conditions")); } catch (Exception ignored) {}
            try { if (profileMap.containsKey("lifestyle")) lifestyleJson = objectMapper.writeValueAsString(profileMap.get("lifestyle")); } catch (Exception ignored) {}
            try { if (profileMap.containsKey("goals")) goalsJson = objectMapper.writeValueAsString(profileMap.get("goals")); } catch (Exception ignored) {}

            String skinType = profileMap.get("skinType") != null ? profileMap.get("skinType").toString() : null;
            Boolean pregnant = profileMap.get("pregnant") != null ? Boolean.valueOf(profileMap.get("pregnant").toString()) : null;

            jdbcTemplate.update(
                    "INSERT INTO profiles (user_id, skin_type, concerns, allergies, pregnant, conditions, lifestyle_json, goals) VALUES (?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE skin_type=VALUES(skin_type), concerns=VALUES(concerns), allergies=VALUES(allergies), pregnant=VALUES(pregnant), conditions=VALUES(conditions), lifestyle_json=VALUES(lifestyle_json), goals=VALUES(goals)",
                    userId, skinType, concernsJson, allergiesJson, pregnant, conditionsJson, lifestyleJson, goalsJson
            );
        }
    }

    public Map<String, Object> oauthLogin(String provider, Map<String, Object> body) {
        Objects.requireNonNull(provider);
        Objects.requireNonNull(body);
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
        String email = jdbcTemplate.queryForObject("SELECT email FROM users WHERE id = ?", String.class, userId);
        List<String> roles = jdbcTemplate.query(
                "SELECT r.code FROM roles r JOIN user_roles ur ON r.id = ur.role_id WHERE ur.user_id = ?",
                (rs, rowNum) -> rs.getString("code"),
                userId
        );
        String access = jwtUtil.generateToken(userId, email, roles.isEmpty() ? List.of("USER") : roles);
        String newRefresh = UUID.randomUUID().toString();
        refreshStore.remove(refreshToken);
        refreshStore.put(newRefresh, userId);
        return Map.of("accessToken", access, "refreshToken", newRefresh);
    }

    public Map<String, Object> getMe(Long userId) {
        Objects.requireNonNull(userId);
        Map<String, Object> result = new HashMap<>();
        try {
            Map<String, Object> u = jdbcTemplate.queryForMap("SELECT id, email, phone, status, created_at FROM users WHERE id = ? LIMIT 1", userId);
            result.put("id", ((Number) u.get("id")).longValue());
            result.put("email", u.get("email"));
            result.put("phone", u.get("phone"));
            result.put("status", u.get("status"));
            result.put("createdAt", u.get("created_at"));
        } catch (EmptyResultDataAccessException e) {
            throw new RuntimeException("User not found");
        }

        // Fetch roles
        List<String> roles = jdbcTemplate.query(
                "SELECT r.code FROM roles r JOIN user_roles ur ON r.id = ur.role_id WHERE ur.user_id = ?",
                (rs, rowNum) -> rs.getString("code"),
                userId
        );
        result.put("roles", roles);

        // Fetch full profile
        try {
            Map<String, Object> p = jdbcTemplate.queryForMap("SELECT skin_type, concerns, allergies, pregnant, conditions, lifestyle_json, goals FROM profiles WHERE user_id = ?", userId);
            result.put("profile", parseJsonFields(p));
        } catch (EmptyResultDataAccessException e) {
            result.put("profile", null);
        }

        // Fetch profile preferences
        try {
            Map<String, Object> prefs = jdbcTemplate.queryForMap("SELECT prefer_functions, avoid_ingredients, budget_min, budget_max FROM profile_prefs WHERE user_id = ?", userId);
            result.put("preferences", parseJsonFields(prefs));
        } catch (EmptyResultDataAccessException e) {
            result.put("preferences", null);
        }

        // Fetch user feedback using JpaRepository
        List<Feedback> feedbackEntities = feedbackRepository.findByUserIdOrderByCreatedAtDesc(userId);
        List<Map<String, Object>> feedback = feedbackEntities.stream().map(f -> {
            Map<String, Object> feedbackMap = new HashMap<>();
            feedbackMap.put("id", f.getId());
            feedbackMap.put("product_id", f.getProduct() != null ? f.getProduct().getId() : null); // Assuming Product has an ID
            feedbackMap.put("rating", f.getRating());
            feedbackMap.put("comment", f.getComment());
            feedbackMap.put("created_at", f.getCreatedAt());
            return feedbackMap;
        }).collect(Collectors.toList());
        result.put("feedback", feedback);

        // Fetch user events
        List<Map<String, Object>> events = jdbcTemplate.queryForList(
                "SELECT type, payload_json, ts FROM events WHERE user_id = ? ORDER BY ts DESC",
                userId
        );
        result.put("events", events.stream().map(this::parseJsonFields).collect(Collectors.toList()));

        // Fetch alerts
        List<Map<String, Object>> alerts = jdbcTemplate.queryForList(
                "SELECT type, payload_json, status, created_at FROM alerts WHERE user_id = ? ORDER BY created_at DESC",
                userId
        );
        result.put("alerts", alerts.stream().map(this::parseJsonFields).collect(Collectors.toList()));

        // Fetch routines
        List<Map<String, Object>> routines = jdbcTemplate.queryForList("SELECT id, title FROM routines WHERE user_id = ?", userId);
        for (Map<String, Object> routine : routines) {
            Long routineId = ((Number) routine.get("id")).longValue();
            List<Map<String, Object>> items = jdbcTemplate.queryForList("SELECT product_id, step, time_of_day FROM routine_items WHERE routine_id = ? ORDER BY step", routineId);
            routine.put("items", items);
        }
        result.put("routines", routines);

        // Fetch journal entries
        List<Map<String, Object>> journalEntries = jdbcTemplate.queryForList("SELECT id, date, text_note FROM journal_entries WHERE user_id = ? ORDER BY date DESC", userId);
        for (Map<String, Object> entry : journalEntries) {
            Long entryId = ((Number) entry.get("id")).longValue();
            List<Map<String, Object>> photos = jdbcTemplate.queryForList("SELECT id, file_key, ai_features_json FROM journal_photos WHERE entry_id = ?", entryId);
            entry.put("photos", photos.stream().map(this::parseJsonFields).collect(Collectors.toList()));
        }
        result.put("journal", journalEntries);

        // Fetch recommendations
        List<Map<String, Object>> recs = jdbcTemplate.queryForList("SELECT product_id, score, reason_json, created_at FROM recs WHERE user_id = ? ORDER BY created_at DESC", userId);
        result.put("recommendations", recs.stream().map(this::parseJsonFields).collect(Collectors.toList()));

        // Fetch schedules
        List<Map<String, Object>> schedules = jdbcTemplate.queryForList("SELECT product_id, cron_expr, channel FROM schedules WHERE user_id = ?", userId);
        result.put("schedules", schedules);

        // Fetch user identities (for OAuth)
        List<Map<String, Object>> identities = jdbcTemplate.queryForList("SELECT provider, provider_uid, created_at FROM user_identities WHERE user_id = ?", userId);
        result.put("identities", identities);

        return result;
    }

    private Map<String, Object> parseJsonFields(Map<String, Object> item) {
        Map<String, Object> newItem = new HashMap<>(item);
        item.forEach((key, value) -> {
            if (key.endsWith("_json") && value instanceof String) {
                try {
                    newItem.put(key.replace("_json", ""), objectMapper.readValue((String) value, new TypeReference<>() {}));
                    newItem.remove(key);
                } catch (JsonProcessingException e) {
                    // Keep raw value if parsing fails
                }
            }
        });
        return newItem;
    }

    public void requestOtp(Map<String, Object> body) {
        Objects.requireNonNull(body);
        String dest = body.get("phone") != null ? body.get("phone").toString() : (body.get("email") != null ? body.get("email").toString() : null);
        if (dest == null) throw new RuntimeException("phone or email required");
        String code = String.valueOf((int) (Math.random() * 900000) + 100000); // 6-digit
        otpStore.put(dest, code);
        otpExpiry.put(dest, Instant.now().plusSeconds(300)); // 5 minutes
        eventService.recordEvent(null, EventType.OTP_REQUEST, Map.of("dest", dest, "code", code));
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
        otpStore.remove(dest);
        otpExpiry.remove(dest);
    }
}