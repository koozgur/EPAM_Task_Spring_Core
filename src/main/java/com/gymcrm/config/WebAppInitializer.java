package com.gymcrm.config;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.Filter;

/**
 * Replaces web.xml. Discovered via Servlet 3.0 SPI; in embedded mode passed explicitly
 * to SpringServletContainerInitializer by GymCrmApplication.
 *
 * Context hierarchy:
 *   Root (parent) ← AppConfig — DAOs, services, persistence
 *   Servlet (child) ← WebMvcConfig — controllers, filters, MVC infra
 */
public class WebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    /** Parent context: persistence, services, utilities. */
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[] { AppConfig.class };
    }

    /** Child context: MVC infrastructure, controllers, filters. */
    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[] { WebMvcConfig.class };
    }

    /** Route all requests through DispatcherServlet. */
    @Override
    protected String[] getServletMappings() {
        return new String[] { "/" };
    }

    /**
     * Filters registered here once implemented:
     *   Phase 3 — AuthenticationFilter (Basic auth)
     *   Phase 4 — TransactionLoggingFilter (MDC), RestLoggingFilter (request/response log)
     */
    @Override
    protected Filter[] getServletFilters() {
        return new Filter[0];
    }
}
