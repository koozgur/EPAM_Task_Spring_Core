package com.gymcrm.workload.security;

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

/**
 * Validates incoming JWT tokens using the shared HMAC-SHA-256 signing key.
 *
 * This service only validates, it never issues tokens.
 * The signing key must match the one used by the main gym-crm service (${jwt.secret}).
 */
@Component
public class JwtTokenValidator {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenValidator.class);

    private final SecretKey signingKey;

    public JwtTokenValidator(@Value("${jwt.secret}") String secret) {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException(
                    "jwt.secret must be at least 32 characters (256 bits) for HMAC-SHA-256");
        }
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Returns true if the token has a valid signature and has not expired.
     * Logs a warning and returns false for any invalid token.
     */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException e) {
            log.warn("JWT validation failed: {}", e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            log.warn("JWT token is empty or malformed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Extracts the subject claim (caller identity) from a valid token.
     * Used for logging only — the workload service does not perform per-user authorization.
     */
    public String getSubject(String token) {
        return parseClaims(token).getSubject();
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
