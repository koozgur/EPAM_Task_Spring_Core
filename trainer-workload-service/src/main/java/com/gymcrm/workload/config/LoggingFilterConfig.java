package com.gymcrm.workload.config;

import com.gymcrm.workload.filter.TransactionIdPropagationFilter;
import com.gymcrm.workload.filter.WorkloadRestLoggingFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Registers the two logging filters as servlet-container-level filters.
 * <p>
 * Order:
 *   1 — TransactionIdPropagationFilter  (reads/generates transactionId → MDC)
 *   2 — WorkloadRestLoggingFilter       (logs request/response with MDC txId already set)
 * <p>
 * These run outside the Spring Security filter chain so every request —
 * including those rejected by security — is covered by both log levels.
 */
@Configuration
public class LoggingFilterConfig {

    @Bean
    public TransactionIdPropagationFilter transactionIdPropagationFilter() {
        return new TransactionIdPropagationFilter();
    }

    @Bean
    public WorkloadRestLoggingFilter workloadRestLoggingFilter() {
        return new WorkloadRestLoggingFilter();
    }

    @Bean
    public FilterRegistrationBean<TransactionIdPropagationFilter> transactionIdFilterRegistration(
            TransactionIdPropagationFilter filter) {
        FilterRegistrationBean<TransactionIdPropagationFilter> reg = new FilterRegistrationBean<>();
        reg.setFilter(filter);
        reg.addUrlPatterns("/*");
        reg.setOrder(1);
        return reg;
    }

    @Bean
    public FilterRegistrationBean<WorkloadRestLoggingFilter> workloadRestLoggingFilterRegistration(
            WorkloadRestLoggingFilter filter) {
        FilterRegistrationBean<WorkloadRestLoggingFilter> reg = new FilterRegistrationBean<>();
        reg.setFilter(filter);
        reg.addUrlPatterns("/*");
        reg.setOrder(2);
        return reg;
    }
}
