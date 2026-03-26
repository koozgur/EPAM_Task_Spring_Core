package com.gymcrm.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * Outermost filter.
 * Reads X-Transaction-Id from the incoming request for end-to-end log correlation;
 * generates a fresh UUID when the header is absent.
 * Stores the ID in MDC and echoes it on the response header.
 * Ensures MDC cleanup after request completion.
 */
public class TransactionLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(TransactionLoggingFilter.class);

    /** MDC key — must match the %X{transactionId} token in logback.xml */
    static final String TRANSACTION_ID_KEY = "transactionId";

    /** Response header surfaced to API callers for correlation with server logs */
    private static final String TRANSACTION_ID_HEADER = "X-Transaction-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {

        String incoming = request.getHeader(TRANSACTION_ID_HEADER);
        String transactionId = (incoming != null && !incoming.isBlank())
                ? incoming
                : UUID.randomUUID().toString();

        // MDC must be set before the header — downstream writes commit the response stream.
        MDC.put(TRANSACTION_ID_KEY, transactionId);
        response.setHeader(TRANSACTION_ID_HEADER, transactionId);

        log.debug("Request started: {} {}", request.getMethod(), request.getRequestURI());

        try {
            chain.doFilter(request, response);
        } finally {
            MDC.remove(TRANSACTION_ID_KEY); // prevent stale transactionId leaking into the next request
        }
    }
}
