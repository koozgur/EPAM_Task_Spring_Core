package com.gymcrm.workload.filter;

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
 * Reads X-Transaction-Id from the incoming request and puts it in MDC so all
 * downstream log statements share the same ID as the main service caller.
 * Falls back to a generated UUID for direct calls. Clears MDC after the request.
 */
public class TransactionIdPropagationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(TransactionIdPropagationFilter.class);

    static final String TRANSACTION_ID_MDC_KEY = "transactionId";
    private static final String TRANSACTION_ID_HEADER = "X-Transaction-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {

        String incoming = request.getHeader(TRANSACTION_ID_HEADER);
        String transactionId = (incoming != null && !incoming.isBlank())
                ? incoming
                : UUID.randomUUID().toString();

        MDC.put(TRANSACTION_ID_MDC_KEY, transactionId);
        response.setHeader(TRANSACTION_ID_HEADER, transactionId);

        log.debug("Request started: {} {} [propagated={}]",
                request.getMethod(), request.getRequestURI(), incoming != null);

        try {
            chain.doFilter(request, response);
        } finally {
            MDC.remove(TRANSACTION_ID_MDC_KEY);
        }
    }
}
