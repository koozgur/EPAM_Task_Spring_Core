package com.gymcrm.workload.component;

import com.gymcrm.workload.component.support.HttpStatusExceptionUnwrappingFilter;
import io.cucumber.spring.CucumberContextConfiguration;
import io.restassured.RestAssured;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;

/**
 * Boots the Spring Boot test context for workload component tests.
 *
 * <p>Uses a shared Testcontainers MongoDB instance and in-memory ActiveMQ,
 * with runtime properties overridden via {@link DynamicPropertySource}.
 * Database state is cleared before each scenario for isolation.
 */
@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("component-test")
public class SpringComponentTestConfig {

    // Cucumber runs on its own JUnit Platform engine, not Jupiter, so the
    // @Testcontainers/@Container lifecycle does not fire. Start the container
    // manually in a static initializer; the JVM reuses it across all scenarios
    // and shuts it down on JVM exit via Testcontainers' Ryuk reaper.
    static final MongoDBContainer mongo = new MongoDBContainer("mongo:7.0");

    static {
        mongo.start();
        // REST-assured 5.4.0 only installs its failure handler for POST, so
        // non-POST 4xx/5xx responses can escape as HttpResponseException. This filter
        // converts that exception back into a Response for uniform assertions.
        RestAssured.filters(new HttpStatusExceptionUnwrappingFilter());
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongo::getReplicaSetUrl);
        registry.add("eureka.client.enabled", () -> "false");
        registry.add("spring.activemq.broker-url",
                () -> "vm://localhost?broker.persistent=false");
    }
}
