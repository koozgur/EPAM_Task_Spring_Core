package com.gymcrm.workload.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * Operation-level logging filter
 */
public class WorkloadRestLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(WorkloadRestLoggingFilter.class);

    private static final int BODY_LIMIT = 500;

    private static final Pattern JSON_SENSITIVE_FIELD_PATTERN = Pattern.compile(
            "(?i)(\"(?:token|accessToken|authorization)\"\\s*:\\s*\")([^\"]*)(\")"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {

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

            String reqBody  = bodySnippet(cachedReq.getContentAsByteArray(),  cachedReq.getContentType());
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

            // Required — ContentCachingResponseWrapper buffers the body;
            // without this the client receives an empty response.
            cachedResp.copyBodyToResponse();
        }
    }

    // -------------------------------------------------------------------------

    private String bodySnippet(byte[] body, String contentType) {
        if (body == null || body.length == 0) {
            return "";
        }
        if (!isLoggableContentType(contentType)) {
            return "[omitted non-text payload]";
        }
        int    len  = Math.min(body.length, BODY_LIMIT);
        String text = new String(body, 0, len, StandardCharsets.UTF_8);
        text = JSON_SENSITIVE_FIELD_PATTERN.matcher(text).replaceAll("$1[REDACTED]$3");
        return body.length > BODY_LIMIT ? text + "...[truncated]" : text;
    }

    private boolean isLoggableContentType(String contentType) {
        if (contentType == null) {
            return false;
        }
        String norm = contentType.toLowerCase(Locale.ROOT);
        if (norm.startsWith("multipart/") || norm.contains("octet-stream")) {
            return false;
        }
        return norm.startsWith("text/")
                || norm.contains("json")
                || norm.contains("xml")
                || norm.contains("x-www-form-urlencoded");
    }
}
