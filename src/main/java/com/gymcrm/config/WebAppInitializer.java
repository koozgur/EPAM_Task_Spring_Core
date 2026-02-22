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
        return new Class<?>[] { WebMvcConfig.class, SwaggerConfig.class };
    }

    /** Route all requests through DispatcherServlet. */
    @Override
    protected String[] getServletMappings() {
        return new String[] { "/" };
    }

    /**
     * Filter chain order, log all requests including auth failures:
     *   1. transactionLoggingFilter — sets MDC transactionId first
     *   2. restLoggingFilter        — wraps auth; captures 401s that auth short-circuits
     *   3. authenticationFilter     — innermost; sends 401 without calling chain on failure
     */
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        super.onStartup(servletContext);

        FilterRegistration.Dynamic txFilter = servletContext.addFilter(
                "transactionLoggingFilter",
                new DelegatingFilterProxy("transactionLoggingFilter")
        );
        txFilter.addMappingForUrlPatterns(
                EnumSet.of(DispatcherType.REQUEST), false, "/*"
        );

        // Must be before authFilter — auth short-circuits on 401 without calling chain.doFilter().
        FilterRegistration.Dynamic restFilter = servletContext.addFilter(
                "restLoggingFilter",
                new DelegatingFilterProxy("restLoggingFilter")
        );
        restFilter.addMappingForUrlPatterns(
                EnumSet.of(DispatcherType.REQUEST), false, "/*"
        );

        FilterRegistration.Dynamic authFilter = servletContext.addFilter(
                "authenticationFilter",
                new DelegatingFilterProxy("authenticationFilter")
        );
        authFilter.addMappingForUrlPatterns(
                EnumSet.of(DispatcherType.REQUEST), false, "/*"
        );
    }
}
