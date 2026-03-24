package com.gymcrm.feign;

import com.gymcrm.security.JwtTokenProvider;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

/**
 * Injects auth and tracing headers into every outbound Feign request:
 * Bearer JWT (subject "gym-crm-service") and X-Transaction-Id from MDC.
 */
@Component
public class FeignAuthInterceptor implements RequestInterceptor {

    private static final String TRANSACTION_ID_HEADER = "X-Transaction-Id";
    private static final String SERVICE_SUBJECT = "gym-crm-service";

    private final JwtTokenProvider jwtTokenProvider;

    public FeignAuthInterceptor(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void apply(RequestTemplate template) {
        String token = jwtTokenProvider.generateToken(SERVICE_SUBJECT);
        template.header("Authorization", "Bearer " + token);

        String transactionId = MDC.get("transactionId");
        if (transactionId != null) {
            template.header(TRANSACTION_ID_HEADER, transactionId);
        }
    }
}
