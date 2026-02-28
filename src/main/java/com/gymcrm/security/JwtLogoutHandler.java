package com.gymcrm.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

/**
 * {@link LogoutHandler} implementation that invalidates a JWT on logout.
 *
 * <p>On {@code /logout}, extracts and validates the Bearer token from the
 * request. If valid, its JTI is added to {@link TokenBlacklistService} so
 * subsequent requests with the same token are rejected. Missing or invalid
 * tokens are ignored, preserving idempotent logout behavior.
 */
@Component
public class JwtLogoutHandler implements LogoutHandler {

    private static final Logger logger = LoggerFactory.getLogger(JwtLogoutHandler.class);

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenBlacklistService tokenBlacklistService;

    public JwtLogoutHandler(JwtTokenProvider jwtTokenProvider,
                            TokenBlacklistService tokenBlacklistService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Override
    public void logout(HttpServletRequest request,
                       HttpServletResponse response,
                       Authentication authentication) {

        String token = JwtAuthenticationFilter.extractBearerToken(request);
        if (token == null) {
            logger.debug("Logout request contains no Bearer token — nothing to blacklist");
            return;
        }

        if (!jwtTokenProvider.validateToken(token)) {
            logger.debug("Logout request contains an invalid/expired token — nothing to blacklist");
            return;
        }

        String jti = jwtTokenProvider.getJtiFromToken(token);
        tokenBlacklistService.blacklist(jti, jwtTokenProvider.getExpiryFromToken(token));
        logger.debug("Logout: blacklisted JWT jti={}", jti);
    }
}
