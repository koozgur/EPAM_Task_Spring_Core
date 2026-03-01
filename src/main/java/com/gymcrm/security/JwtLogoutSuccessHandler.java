package com.gymcrm.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

/**
 * {@link LogoutSuccessHandler} for REST APIs that returns {@code 200 OK}
 * without redirecting.
 *
 * <p>Replaces the default redirect-based behavior to align with stateless
 * JWT authentication. Invoked after {@link JwtLogoutHandler}.
 */
@Component
public class JwtLogoutSuccessHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request,
                                HttpServletResponse response,
                                Authentication authentication) {
        // Return 200 OK with no body.
        // No redirect: REST clients must not be forced to follow an HTML redirect.
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
