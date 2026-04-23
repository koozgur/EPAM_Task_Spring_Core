package com.gymcrm.integration;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoReactiveDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoReactiveAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Test-only Spring Boot entry point for integration tests.
 *
 * <p>Runs the main service in-process while avoiding bean conflicts with:
 * <ul>
 *   <li>Workload JMS configuration</li>
 *   <li>Component-test classes sharing the same classpath</li>
 * </ul>
 *
 * <p>Component scanning is restricted to an explicit allowlist to prevent accidental inclusion of {@code GymCrmApplication}, which would re-scan the entire classpath.
 *
 * <p>{@code @EntityScan} and {@code @EnableJpaRepositories} explicitly include
 * model and DAO packages, since restricted scanning disables default detection.
 *
 * <p>MongoDB auto-configuration is excluded to avoid unintended connections.
 */
@SpringBootApplication(
        exclude = {
                MongoAutoConfiguration.class,
                MongoDataAutoConfiguration.class,
                MongoReactiveAutoConfiguration.class,
                MongoReactiveDataAutoConfiguration.class,
                MongoRepositoriesAutoConfiguration.class
        },
        scanBasePackages = {
                "com.gymcrm.config",
                "com.gymcrm.controller",
                "com.gymcrm.dao",
                "com.gymcrm.exception",
                "com.gymcrm.facade",
                "com.gymcrm.filter",
                "com.gymcrm.health",
                "com.gymcrm.mapper",
                "com.gymcrm.metrics",
                "com.gymcrm.security",
                "com.gymcrm.service",
                "com.gymcrm.util",
                "com.gymcrm.integration"
        })
@EntityScan("com.gymcrm.model")
@EnableJpaRepositories("com.gymcrm.dao")
public class MainServiceIntegrationApplication {
}
