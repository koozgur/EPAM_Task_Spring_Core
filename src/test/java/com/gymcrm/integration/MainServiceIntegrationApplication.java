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
 * Test-only entry point used by integration tests to boot the main service.
 *
 * <p>The workload-service JAR sits on the integration-test classpath so tests can
 * start {@code TrainerWorkloadApplication} in-process. Two hazards follow:
 *
 * <ol>
 *   <li>Workload JMS beans ({@code jmsConfig}, {@code jmsTemplate},
 *       {@code jacksonJmsMessageConverter}) would collide with the main service's.</li>
 *   <li>Component-test @Component classes under {@code com.gymcrm.component.*}
 *       share the test-classes root and would collide on bean names
 *       ({@code testContext}, {@code databaseCleaner}).</li>
 * </ol>
 *
 * <p>The scan is restricted to an explicit allowlist of sub-packages. The root
 * {@code com.gymcrm} is deliberately excluded: it contains
 * {@link com.gymcrm.GymCrmApplication}, whose own {@code @SpringBootApplication}
 * would transitively re-scan everything (including workload and component) with
 * no filter.
 *
 * <p>{@code @EntityScan} and {@code @EnableJpaRepositories} re-expose the
 * {@code model} and {@code dao} packages to Hibernate and Spring Data — with
 * {@code scanBasePackages} restricted, the default auto-detection no longer
 * finds them. MongoDB auto-configuration is disabled so the main context does
 * not try to connect to Mongo merely because it's on the classpath.
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
