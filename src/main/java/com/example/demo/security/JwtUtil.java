package com.example.demo.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    @Value("${app.jwt.secret:dev-very-secret-key-change-me}")
    private String secret;

    @Value("${app.jwt.exp-ms:3600000}")
    private long expMs;

    public String generateToken(Long userId, String email, List<String> roles) {
        Algorithm alg = Algorithm.HMAC256(secret);
        var builder = JWT.create()
                .withSubject(String.valueOf(userId))
                .withClaim("email", email)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + expMs));
        if (roles != null && !roles.isEmpty()) {
            builder.withArrayClaim("roles", roles.toArray(new String[0]));
        }
        return builder.sign(alg);
    }

    public CurrentUser validateAndGet(String token) {
        Algorithm alg = Algorithm.HMAC256(secret);
        JWTVerifier verifier = JWT.require(alg).build();
        DecodedJWT jwt = verifier.verify(token);
        Long userId = Long.valueOf(jwt.getSubject());
        String email = jwt.getClaim("email").asString();
        List<String> roles = jwt.getClaim("roles").asList(String.class);
        return new CurrentUser(userId, email, roles);
    }

    public boolean isExpired(String token) {
        DecodedJWT jwt = JWT.decode(token);
        return jwt.getExpiresAt().before(new Date());
    }
}
