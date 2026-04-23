package com.gymcrm.component;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoReactiveDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoReactiveAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Test-only entry point for component tests.
 *
 * <p>The workload-service JAR is on the main test classpath so that integration
 * tests can boot {@code TrainerWorkloadApplication} in-process. That places
 * {@code com.gymcrm.workload.*} beans under {@code GymCrmApplication}'s default
 * {@code com.gymcrm} scan root, which collides with the main service's own
 * {@code jmsConfig} / {@code jmsTemplate} / {@code jacksonJmsMessageConverter}.
 *
 * <p>Component tests switch to this class via {@code @SpringBootTest(classes = ...)}
 * to restrict the scan to an explicit allowlist of main-service sub-packages,
 * avoiding {@code com.gymcrm.workload.*} (and also {@code com.gymcrm.integration.*},
 * whose @Component helpers live on the same test-classes root).
 *
 * <p>{@code @EntityScan} and {@code @EnableJpaRepositories} re-expose the
 * {@code model} / {@code dao} packages because the restricted component scan
 * no longer auto-drives entity / repository discovery.
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
                "com.gymcrm.component"
        })
@EntityScan("com.gymcrm.model")
@EnableJpaRepositories("com.gymcrm.dao")
public class MainServiceComponentApplication {
}
