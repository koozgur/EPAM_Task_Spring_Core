package com.gymcrm.config;

import com.gymcrm.filter.RestLoggingFilter;
import com.gymcrm.filter.TransactionLoggingFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Registers servlet filters that run alongside the Spring Security filter chain.
 */
@Configuration
public class FilterConfig {

    @Bean
    public TransactionLoggingFilter transactionLoggingFilter() {
        return new TransactionLoggingFilter();
    }

    @Bean
    public RestLoggingFilter restLoggingFilter() {
        return new RestLoggingFilter();
    }

    @Bean
    public FilterRegistrationBean<TransactionLoggingFilter> transactionLoggingFilterRegistration(
            TransactionLoggingFilter filter) {
        FilterRegistrationBean<TransactionLoggingFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(filter);
        registration.addUrlPatterns("/*");
        registration.setOrder(1);
        return registration;
    }

    @Bean
    public FilterRegistrationBean<RestLoggingFilter> restLoggingFilterRegistration(RestLoggingFilter filter) {
        FilterRegistrationBean<RestLoggingFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(filter);
        registration.addUrlPatterns("/*");
        registration.setOrder(2);
        return registration;
    }
}
