package com.gymcrm.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

/**
 * Returns a JSON 401 response when an unauthenticated request reaches a protected endpoint.
 * Keeps the error format consistent with {@link com.gymcrm.exception.GlobalExceptionHandler}.
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        String path = request.getRequestURI();
        String txId = MDC.get("transactionId");
        String message = authException.getMessage() != null
                ? authException.getMessage()
                : "Authentication required — provide a valid Bearer token";

        // Build a minimal JSON body that mirrors ErrorResponse record layout
        String body = String.format(
                "{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"%s\"," +
                "\"path\":\"%s\",\"transactionId\":\"%s\",\"timestamp\":\"%s\",\"fieldErrors\":null}",
                escape(message),
                escape(path),
                txId != null ? txId : "",
                Instant.now());

        response.getWriter().write(body);
    }

    /** Minimal JSON string escaping for the fields we control. */
    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }
}
