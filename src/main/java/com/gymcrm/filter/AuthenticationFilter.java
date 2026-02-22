package com.gymcrm.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gymcrm.dto.response.ErrorResponse;
import com.gymcrm.service.UserService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Set;

/**
 * Validates Basic auth on every non-public request.
 * Delegates credential check to UserService.
 */
public class AuthenticationFilter extends OncePerRequestFilter {

    private static final Set<String> PUBLIC_EXACT_PATHS = Set.of(
            "/trainees/register", "/trainers/register", "/v3/api-docs", "/v3/api-docs.yaml", "/actuator/health", "/actuator/info", "/actuator/prometheus"
    );

    private static final Set<String> PUBLIC_PREFIX_PATHS = Set.of(
            "/swagger-ui", "/v3/api-docs"
    );

    private final UserService userService;
    private final ObjectMapper objectMapper;
    private final Counter authRequestsCounter;
    private final Counter authFailuresCounter;

    // Constructor injection — servlet container instantiates filters before Spring field injection runs.
    public AuthenticationFilter(UserService userService, MeterRegistry meterRegistry) {
        this.userService = userService;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.authRequestsCounter = meterRegistry.counter("gymcrm.auth.requests.total");
        this.authFailuresCounter = meterRegistry.counter("gymcrm.auth.failures.total");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {

        if (isPublicPath(request.getRequestURI())) {
            chain.doFilter(request, response);
            return;
        }

        authRequestsCounter.increment();

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Basic ")) {
            authFailuresCounter.increment();
            sendJsonError(response, request, "Missing credentials");
            return;
        }

        // Guard: blank token after scheme — e.g. "Basic   "
        String token = header.substring(6).trim();
        if (token.isEmpty()) {
            sendJsonError(response, request, "Malformed Authorization header");
            return;
        }

        // Guard: invalid Base64 — decode() throws IllegalArgumentException on bad input
        String decoded;
        try {
            decoded = new String(Base64.getDecoder().decode(token), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            sendJsonError(response, request, "Malformed Authorization header");
            return;
        }

        // Guard: no colon — indexOf handles passwords containing ':' correctly (RFC 7617)
        int colon = decoded.indexOf(':');
        if (colon < 0) {
            sendJsonError(response, request, "Malformed Authorization header");
            return;
        }

        String username = decoded.substring(0, colon);
        String password = decoded.substring(colon + 1);

        // Guard: blank username or empty password
        if (username.isBlank() || password.isEmpty()) {
            sendJsonError(response, request, "Missing credentials");
            return;
        }

        if (!userService.authenticate(username, password)) {
            authFailuresCounter.increment();
            sendJsonError(response, request, "Invalid credentials");
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean isPublicPath(String uri) {
        if (PUBLIC_EXACT_PATHS.contains(uri)) {
            return true;
        }
        return PUBLIC_PREFIX_PATHS.stream().anyMatch(uri::startsWith);
    }

    private void sendJsonError(HttpServletResponse response,
                               HttpServletRequest request,
                               String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        ErrorResponse errorResponse = new ErrorResponse(
                HttpServletResponse.SC_UNAUTHORIZED,
                "Unauthorized",
                message,
                request.getRequestURI(),
                MDC.get("transactionId"),
                Instant.now(),
                null
        );

        String jsonBody = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(jsonBody);
        response.getWriter().flush();
    }
}
