package com.gymcrm.config;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Servlet (child) context — MVC infrastructure, controllers, mappers, facade.
 * Filters are intentionally excluded: they belong to the root context because
 * DelegatingFilterProxy resolves them before the child context starts.
 * @EnableWebMvc lives here so HandlerMapping is initialised after controllers are registered.
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {
        "com.gymcrm.controller",
        "com.gymcrm.mapper",
        "com.gymcrm.facade"
})
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * Mutates the existing Jackson converter (preserves all other defaults).
     * JavaTimeModule + WRITE_DATES_AS_TIMESTAMPS=false → LocalDate serialises as "2026-02-19".
     */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.stream()
                .filter(c -> c instanceof MappingJackson2HttpMessageConverter)
                .map(c -> (MappingJackson2HttpMessageConverter) c)
                .findFirst()
                .ifPresent(c -> {
                    c.getObjectMapper().registerModule(new JavaTimeModule());
                    c.getObjectMapper().disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                });
    }

    /** Fall back to JSON when no Accept header is present. */
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.defaultContentType(MediaType.APPLICATION_JSON);
    }
}
