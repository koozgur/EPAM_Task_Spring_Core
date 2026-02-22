package com.gymcrm.dto.response;

import java.time.Instant;
import java.util.Map;

/**
 * Immutable error envelope returned for all non-2xx responses.
 *
 * status — HTTP status code
 * error — standard HTTP reason phrase
 * message — human-readable summary
 * path — request URI
 * transactionId — MDC correlation id (nullable)
 * timestamp — UTC timestamp of error generation
 * fieldErrors — optional validation field errors (null if not applicable)
 */
public record ErrorResponse(
        int status,
        String error,
        String message,
        String path,
        String transactionId,
        Instant timestamp,
        Map<String, String> fieldErrors
) {}