package com.gymcrm.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

/**
 * Creates, signs, and validates JWT tokens (HMAC-SHA-256).
 *
 * Each token carries:
 *   sub  — username
 *   jti  — UUID used by TokenBlacklistService (Phase 6) on logout
 *   iat  — issued-at timestamp
 *   exp  — expiry timestamp (now + jwt.expiration-ms)
 */
@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    private final SecretKey signingKey;
    private final long expirationMs;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expirationMs) {

        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException(
                    "jwt.secret must be at least 32 characters (256 bits) for HMAC-SHA-256");
        }
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        this.expirationMs = expirationMs;
    }

    /**
     * Generates a signed, time-limited JWT for the given username.
     */
    public String generateToken(String username) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(username)
                .id(UUID.randomUUID().toString())   // jti — unique per token
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(expirationMs)))
                .signWith(signingKey)
                .compact();
    }

    /**
     * Extracts the username (subject claim) from a valid token.
     * Throws JwtException if the token is invalid or expired.
     */
    public String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * Extracts the JWT ID (jti claim) — used by TokenBlacklistService on logout.
     */
    public String getJtiFromToken(String token) {
        return parseClaims(token).getId();
    }

    /**
     * Extracts the token's expiry instant — used to schedule blacklist cleanup.
     */
    public Instant getExpiryFromToken(String token) {
        return parseClaims(token).getExpiration().toInstant();
    }

    /**
     * Returns true if the token is valid (correct signature, not expired).
     * Logs a warning and returns false for any invalid token.
     */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException e) {
            logger.warn("JWT validation failed: {}", e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            logger.warn("JWT token is empty or malformed: {}", e.getMessage());
            return false;
        }
    }

    // -------------------------------------------------------------------------

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
