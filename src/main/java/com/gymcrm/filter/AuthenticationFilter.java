package com.gymcrm.filter;

import com.gymcrm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Set;

/**
 * Validates Basic auth on every non-public request.
 * Delegates credential check to UserService.
 */
@Component
public class AuthenticationFilter extends OncePerRequestFilter {

    // O(1) exact-match — avoids endsWith partial-match risk (e.g. "/admin/trainees/register")
    private static final Set<String> PUBLIC_PATHS = Set.of(
            "/trainees/register", "/trainers/register", "/training-types", "/login"
    );

    private final UserService userService;

    // Constructor injection — servlet container instantiates filters before Spring field injection runs.
    @Autowired
    public AuthenticationFilter(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {

        if (PUBLIC_PATHS.contains(request.getRequestURI())) {
            chain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Basic ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing credentials");
            return;
        }

        // Guard: blank token after scheme — e.g. "Basic   "
        String token = header.substring(6).trim();
        if (token.isEmpty()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Malformed Authorization header");
            return;
        }

        // Guard: invalid Base64 — decode() throws IllegalArgumentException on bad input
        String decoded;
        try {
            decoded = new String(Base64.getDecoder().decode(token), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Malformed Authorization header");
            return;
        }

        // Guard: no colon — indexOf handles passwords containing ':' correctly (RFC 7617)
        int colon = decoded.indexOf(':');
        if (colon < 0) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Malformed Authorization header");
            return;
        }

        String username = decoded.substring(0, colon);
        String password = decoded.substring(colon + 1);

        // Guard: blank username or empty password
        if (username.isBlank() || password.isEmpty()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing credentials");
            return;
        }

        if (!userService.authenticate(username, password)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid credentials");
            return;
        }

        chain.doFilter(request, response);
    }
}
