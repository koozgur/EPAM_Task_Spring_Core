package com.gymcrm.component;

import com.gymcrm.GymCrmApplication;
import com.gymcrm.component.support.HttpStatusExceptionUnwrappingFilter;
import io.cucumber.spring.CucumberContextConfiguration;
import io.restassured.RestAssured;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Boots the Spring Boot test context for Cucumber component tests.
 *
 * <p>Uses a shared Testcontainers PostgreSQL instance and in-memory ActiveMQ,
 * with runtime properties overridden via {@link DynamicPropertySource}.
 * Database state is reset before each scenario for isolation (see DatabaseCleaner).
 */
@CucumberContextConfiguration
@SpringBootTest(
    classes = GymCrmApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("component-test")
public class SpringComponentTestConfig {

    // Cucumber runs on its own JUnit Platform engine, not Jupiter, so the
    // @Testcontainers/@Container lifecycle does not fire. Start the container
    // manually in a static initializer; the JVM reuses it across all scenarios
    // and shuts it down on JVM exit via Testcontainers' Ryuk reaper.
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("gymcrm_test")
            .withUsername("test")
            .withPassword("test");

    static {
        postgres.start();
        // REST-assured 5.4.0 only installs its failure handler for POST, so
        // non-POST 4xx/5xx responses can escape as HttpResponseException. This filter
        // converts that exception back into a Response for uniform assertions.
        // RestAssured.filters(new HttpStatusExceptionUnwrappingFilter());
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("eureka.client.enabled", () -> "false");
        registry.add("spring.activemq.broker-url",
                () -> "vm://localhost?broker.persistent=false");
    }
}
