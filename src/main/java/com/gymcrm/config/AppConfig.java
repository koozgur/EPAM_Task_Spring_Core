package com.gymcrm.config;

import org.springframework.context.annotation.Configuration;

/**
 * Boot-level configuration extension point.
 * <p>
 * Spring Boot now autoconfigures DataSource, JPA, transaction manager, and SQL initialization
 * from application properties and active profile settings.
 *
 * Keep this class only for explicit custom beans that are not provided by Boot defaults.
 */
@Configuration
public class AppConfig {
}

