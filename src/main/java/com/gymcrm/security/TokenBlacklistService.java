package com.gymcrm.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * In-memory JWT blacklist used for logout token invalidation.
 *
 * When a user logs out, the token's {@code jti} (JWT ID) is stored here until the original
 * expiry time. {@link JwtAuthenticationFilter} checks this list on every request and rejects
 * blacklisted tokens even if they are still cryptographically valid.
 *
 * <p><b>State:</b> in-memory only — cleared on application restart.
 */
@Service
public class TokenBlacklistService {

    private static final Logger logger = LoggerFactory.getLogger(TokenBlacklistService.class);

    /** Maps JWT ID (jti) → token expiry. Entries are removed lazily or by scheduled cleanup. */
    private final ConcurrentMap<String, Instant> blacklist = new ConcurrentHashMap<>();

    /**
     * Adds a token's {@code jti} to the blacklist until {@code expiry}.
     */
    public void blacklist(String jti, Instant expiry) {
        blacklist.put(jti, expiry);
        logger.debug("Blacklisted JWT jti={} until {}", jti, expiry);
    }

    public boolean isBlacklisted(String jti) {
        Instant expiry = blacklist.get(jti);
        if (expiry == null) return false; //if expired
        if (Instant.now().isAfter(expiry)) {
            blacklist.remove(jti);  // lazy removal
            return false;
        }
        return true;
    }

    /**
     * Removes all expired entries from the blacklist.
     * Runs every 10 minutes to prevent unbounded memory growth.
     * Entries that are still within their expiry window remain — they are still needed to
     * reject in-flight requests that carry a supposedly-valid but logged-out token.
     */
    @Scheduled(fixedRate = 600_000)
    public void cleanupExpired() {
        int before = blacklist.size();
        blacklist.entrySet().removeIf(e -> Instant.now().isAfter(e.getValue()));
        int removed = before - blacklist.size();
        if (removed > 0) {
            logger.debug("Token blacklist cleanup: removed {} expired entries, {} remaining",
                    removed, blacklist.size());
        }
    }
}
