package com.gymcrm.config;

import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.EnumSet;

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
     * Register filters via DelegatingFilterProxy so Spring resolves @Component beans
     * (with injected dependencies) from the servlet context at first request.
     * Order: transactionLoggingFilter → authenticationFilter → restLoggingFilter (Phase 8)
     */
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        super.onStartup(servletContext);

        //TODO: register transactionLoggingFilter here BEFORE authenticationFilter

        FilterRegistration.Dynamic authFilter = servletContext.addFilter(
                "authenticationFilter",
                new DelegatingFilterProxy("authenticationFilter")
        );
        authFilter.addMappingForUrlPatterns(
                EnumSet.of(DispatcherType.REQUEST), false, "/*"
        );

        //TODO: register restLoggingFilter here AFTER authenticationFilter
    }
}
