package com.gymcrm.workload.component;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Cucumber-Spring glue: boots the workload service against a disposable
 * Testcontainers MongoDB instance and an in-memory ActiveMQ broker.
 *
 * <p>The container is static and shared across all scenarios.
 * Scenario isolation is handled by dropping the MongoDB collection
 * in a {@code @Before} hook.
 */
@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("component-test")
@Testcontainers
public class SpringComponentTestConfig {

    @Container
    static MongoDBContainer mongo = new MongoDBContainer("mongo:7.0");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongo::getReplicaSetUrl);
        registry.add("eureka.client.enabled", () -> "false");
        registry.add("spring.activemq.broker-url",
                () -> "vm://localhost?broker.persistent=false");
    }
}
