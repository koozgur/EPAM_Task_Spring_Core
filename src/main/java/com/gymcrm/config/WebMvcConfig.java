package com.gymcrm.config;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * MVC customization layer on top of Spring Boot auto-configuration.
 *
 * Keep only custom behavior that differs from Boot defaults.
 */
@Configuration
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
