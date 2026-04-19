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
 * Boots the Spring Boot test context for workload component tests.
 *
 * Uses a shared Testcontainers MongoDB instance and in-memory ActiveMQ.
 * Database state is cleared before each scenario for isolation.
 * Runtime properties are injected via DynamicPropertySource.
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
