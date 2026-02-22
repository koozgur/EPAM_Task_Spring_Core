package com.gymcrm.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Logs REST requests and responses including method, URI, status,
 * execution time, and redacted body content.
 * <p>
 * Wraps authentication to ensure 401 responses are also logged.
 */
@Component("restLoggingFilter")
public class RestLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RestLoggingFilter.class);

    /** Max bytes logged per body to prevent log flooding. */
    private static final int BODY_LIMIT = 500;

        private static final Pattern JSON_SENSITIVE_FIELD_PATTERN = Pattern.compile(
                "(?i)(\"(?:password|oldPassword|newPassword|token|accessToken|refreshToken|authorization)\\\"\\s*:\\s*\\\")([^\\\"]*)(\\\")"
        );

        private static final Pattern FORM_SENSITIVE_FIELD_PATTERN = Pattern.compile(
            "(?i)(^|[&;\\s])(password|oldPassword|newPassword|token|access_token|refresh_token|authorization)=([^&;\\s]*)"
        );

    /**
     * Wraps request/response to cache bodies, logs request start,
     * executes the filter chain, then logs status, duration, and
     * redacted payload snippets before flushing the response body.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {

        // Wrap request/response to cache bodies for logging; copyBodyToResponse() required to flush.
        ContentCachingRequestWrapper  cachedReq  = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper cachedResp = new ContentCachingResponseWrapper(response);

        long   startMs = System.currentTimeMillis();
        String method  = cachedReq.getMethod();
        String uri     = cachedReq.getRequestURI();

        log.info("--> {} {}", method, uri);

        try {
            chain.doFilter(cachedReq, cachedResp);
        } finally {
            long elapsedMs = System.currentTimeMillis() - startMs;
            int  status    = cachedResp.getStatus();

            String reqBody  = bodySnippet(cachedReq.getContentAsByteArray(), cachedReq.getContentType());
            String respBody = bodySnippet(cachedResp.getContentAsByteArray(), cachedResp.getContentType());

            if (!reqBody.isEmpty()) {
                log.info("--> {} {} | request body: {}", method, uri, reqBody);
            }

            if (status >= 500) {
                log.error("<-- {} {} | status={} | {}ms | response: {}", method, uri, status, elapsedMs, respBody);
            } else if (status >= 400) {
                log.warn("<-- {} {} | status={} | {}ms | response: {}", method, uri, status, elapsedMs, respBody);
            } else {
                log.info("<-- {} {} | status={} | {}ms", method, uri, status, elapsedMs);
            }

            // Required — without this the client receives an empty body.
            cachedResp.copyBodyToResponse();
        }
    }

    /** Returns body up to BODY_LIMIT, redacted and truncated if needed. */
    private String bodySnippet(byte[] body, String contentType) {
        if (body == null || body.length == 0) {
            return "";
        }

        if (!isLoggableBodyContentType(contentType)) {
            return "[omitted non-text payload]";
        }

        int len     = Math.min(body.length, BODY_LIMIT);
        String text = new String(body, 0, len, StandardCharsets.UTF_8);
        text = redactSensitiveFields(text);
        return body.length > BODY_LIMIT ? text + "...[truncated]" : text;
    }

    /**
     * Determines whether the content type is safe and meaningful to log.
     * Skips multipart and binary payloads.
     */
    private boolean isLoggableBodyContentType(String contentType) {
        if (contentType == null) {
            return false;
        }

        String normalized = contentType.toLowerCase(Locale.ROOT);
        if (normalized.startsWith("multipart/") || normalized.contains("octet-stream")) {
            return false;
        }

        return normalized.startsWith("text/")
                || normalized.contains("json")
                || normalized.contains("xml")
                || normalized.contains("x-www-form-urlencoded")
                || normalized.contains("yaml")
                || normalized.contains("javascript");
    }

    /**
     * Masks sensitive fields (e.g., passwords, tokens) in JSON and
     * form-encoded payloads before logging.
     */
    private String redactSensitiveFields(String bodyText) {
        String redactedJson = JSON_SENSITIVE_FIELD_PATTERN.matcher(bodyText)
                .replaceAll("$1[REDACTED]$3");

        return FORM_SENSITIVE_FIELD_PATTERN.matcher(redactedJson)
                .replaceAll("$1$2=[REDACTED]");
    }
}
