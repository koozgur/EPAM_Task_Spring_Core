package com.gymcrm.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Tracks failed login attempts per username and enforces a temporary lockout
 * after too many consecutive failures.
 *
 * <p><b>State:</b> in-memory only — resets on application restart.
 */
@Service
public class LoginAttemptService {

    private static final Logger logger = LoggerFactory.getLogger(LoginAttemptService.class);

    private final int maxAttempts;
    private final Duration lockoutDuration;
    private final ConcurrentMap<String, AttemptInfo> attempts = new ConcurrentHashMap<>();

    public LoginAttemptService(
            @Value("${security.login.max-attempts:3}") int maxAttempts,
            @Value("${security.login.lockout-duration-ms:300000}") long lockoutDurationMs) {
        this.maxAttempts = maxAttempts;
        this.lockoutDuration = Duration.ofMillis(lockoutDurationMs);
    }

    /**
     * Returns {@code true} if the username is currently in the lockout window.
     * Expired lockouts are cleaned up lazily here.
     */
    public boolean isBlocked(String username) {
        AttemptInfo info = attempts.get(username);
        if (info == null || info.lockedUntil() == null) return false;
        if (Instant.now().isAfter(info.lockedUntil())) {
            attempts.remove(username);  // lockout expired — clean up
            return false;
        }
        return true;
    }

    /**
     * Records a failed login attempt. Triggers a lockout when the failure count
     * reaches {@code security.login.max-attempts}.
     */
    public void loginFailed(String username) {
        AttemptInfo current = attempts.getOrDefault(username, new AttemptInfo(0, null));
        int newCount = current.count() + 1;

        if (newCount >= maxAttempts) {
            Instant lockedUntil = Instant.now().plus(lockoutDuration);
            attempts.put(username, new AttemptInfo(newCount, lockedUntil));
            logger.warn("Account '{}' locked until {} after {} failed attempts",
                    username, lockedUntil, newCount);
        } else {
            attempts.put(username, new AttemptInfo(newCount, null));
            logger.debug("Failed login attempt {}/{} for '{}'", newCount, maxAttempts, username);
        }
    }

    /**
     * Clears the failure counter for a username on successful authentication.
     */
    public void loginSucceeded(String username) {
        attempts.remove(username);
    }

    // -------------------------------------------------------------------------

    /**
     * Immutable snapshot of attempt state for one username.
     *
     * @param count      number of consecutive failed attempts
     * @param lockedUntil non-null when the account is in a brute-force lockout
     */
    private record AttemptInfo(int count, Instant lockedUntil) {}
}
