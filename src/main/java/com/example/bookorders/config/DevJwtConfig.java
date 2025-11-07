package com.example.bookorders.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Development Jwt decoder that accepts a fixed token value for local testing.
 *
 * Usage: run the app with the `dev` profile and send the header
 *   Authorization: Bearer dev-token
 *
 * This is intentionally simple and insecure; do NOT enable in production.
 */
@Configuration
@Profile("dev")
public class DevJwtConfig {

    private static final String DEV_TOKEN = "dev-token";

    @Bean
    public JwtDecoder jwtDecoder() {
        return token -> {
            if (DEV_TOKEN.equals(token)) {
                Instant now = Instant.now();
                Instant exp = now.plus(1, ChronoUnit.HOURS);

                Map<String, Object> headers = new HashMap<>();
                headers.put("alg", "none");

                Map<String, Object> claims = new HashMap<>();
                claims.put("sub", "dev-user");
                claims.put("scope", "read write");
                claims.put("iss", "dev");
                claims.put("aud", List.of("book-orders-service"));
                claims.put("iat", now.getEpochSecond());
                claims.put("exp", exp.getEpochSecond());

                return new Jwt(token, now, exp, headers, claims);
            }
            throw new JwtException("Unrecognized dev token");
        };
    }
}
