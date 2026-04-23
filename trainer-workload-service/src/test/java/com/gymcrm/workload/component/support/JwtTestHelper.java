package com.gymcrm.workload.component.support;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

/**
 * Generates JWT tokens for workload service component tests (originally, workload service is a consumer).
 *
 * <p>The workload service has no registration endpoint, so tests cannot
 * obtain tokens via HTTP. This helper creates tokens signed with the
 * same secret the service validates against.
 */
@Component
public class JwtTestHelper {

    private final SecretKey signingKey;

    public JwtTestHelper(@Value("${jwt.secret}") String secret) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateValidToken(String username) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(username)
                .id(UUID.randomUUID().toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(3600)))
                .signWith(signingKey)
                .compact();
    }

    public String generateExpiredToken(String username) {
        Instant past = Instant.now().minusSeconds(7200);
        return Jwts.builder()
                .subject(username)
                .id(UUID.randomUUID().toString())
                .issuedAt(Date.from(past))
                .expiration(Date.from(past.plusSeconds(3600)))
                .signWith(signingKey)
                .compact();
    }
}
