package com.gymcrm.integration;

import com.gymcrm.workload.TrainerWorkloadApplication;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Spring context configuration for end-to-end Cucumber tests.
 *
 * <p>Starts required dependencies before context initialization:
 * <ul>
 *   <li>PostgreSQL (JPA store)</li>
 *   <li>MongoDB (workload service store)</li>
 *   <li>{@link TrainerWorkloadApplication} on a random port</li>
 * </ul>
 *
 * <p>The main service runs via {@code @SpringBootTest}, while both services
 * share an in-JVM ActiveMQ broker ({@code vm://localhost}), avoiding the need
 * for a dedicated container.
 *
 * <p>{@link MainServiceIntegrationApplication} is used to restrict component
 * scanning and prevent workload-service classes from leaking into the main context.
 */
@CucumberContextConfiguration
@SpringBootTest(
        classes = MainServiceIntegrationApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("integration-test")
public class SpringIntegrationTestConfig {

    // Cucumber runs on the JUnit Platform (not Jupiter) so @Testcontainers'
    // lifecycle hooks never fire. Start containers and the workload app manually.
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("gymcrm_int_test")
            .withUsername("test")
            .withPassword("test");

    static final MongoDBContainer mongo = new MongoDBContainer("mongo:7.0");

    static final String SHARED_JWT_SECRET = "integration-test-jwt-secret-key-min-32-chars-long";
    static final String BROKER_URL = "vm://localhost?broker.persistent=false&create=true";

    private static ConfigurableApplicationContext workloadContext;
    private static int workloadPort;

    public static int workloadPort() {
        return workloadPort;
    }

    public static ConfigurableApplicationContext workloadContext() {
        return workloadContext;
    }

    static {
        postgres.start();
        mongo.start();
        startWorkloadService();
        // Ensure workload Spring context shuts down on JVM exit. 
        // Testcontainers handles container lifecycle separately.
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (workloadContext != null && workloadContext.isActive()) {
                workloadContext.close();
            }
        }, "workload-context-shutdown"));
    }

    private static void startWorkloadService() {
        workloadContext = new SpringApplicationBuilder(TrainerWorkloadApplication.class)
                // Command-line args override config files, isolating workload configuration.
                .run(
                        "--server.port=0",
                        "--spring.application.name=trainer-workload-service",
                        "--spring.data.mongodb.uri=" + mongo.getReplicaSetUrl(),
                        "--spring.activemq.broker-url=" + BROKER_URL,
                        "--jwt.secret=" + SHARED_JWT_SECRET,
                        "--eureka.client.enabled=false",
                        "--eureka.client.register-with-eureka=false",
                        "--eureka.client.fetch-registry=false",
                        "--workload.jms.queue-name=trainer.workload.queue",
                        "--workload.jms.dlq-name=DLQ.trainer.workload.queue",
                        "--workload.jms.concurrency=1-1",
                        "--logging.level.com.gymcrm.workload=WARN",
                        "--logging.level.org.apache.activemq=WARN",
                        // Prevent unintended JDBC/JPA auto-configuration in the workload context due to shared test classpath dependencies.
                        "--spring.autoconfigure.exclude="
                                + "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,"
                                + "org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration,"
                                + "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,"
                                + "org.springframework.boot.autoconfigure.sql.init.SqlInitializationAutoConfiguration"
                );
        workloadPort = workloadContext.getEnvironment()
                .getProperty("local.server.port", Integer.class, 0);
    }

    @DynamicPropertySource
    static void mainServiceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.activemq.broker-url", () -> BROKER_URL);
        registry.add("jwt.secret", () -> SHARED_JWT_SECRET);
    }
}
