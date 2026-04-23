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
 * Boots the Spring context for end-to-end Cucumber integration tests.
 *
 * <p>Starts three things in a single static initializer so they are live before
 * any Spring context loads:
 * <ul>
 *   <li>PostgreSQL Testcontainer — main service JPA store</li>
 *   <li>MongoDB Testcontainer — workload service document store</li>
 *   <li>{@link TrainerWorkloadApplication} via {@link SpringApplicationBuilder} on a random port</li>
 * </ul>
 *
 * <p>The main service is booted here as a standard {@code @SpringBootTest}.
 * Both services share a single in-JVM ActiveMQ broker via {@code vm://localhost}:
 * the first {@code ConnectionFactory} to connect auto-starts the broker, and
 * subsequent connections (from either service) reuse it. No ActiveMQ Testcontainer
 * is needed because both services run in the same JVM — the plan originally
 * called for one, but vm:// is strictly simpler and equally representative of
 * the JMS contract.
 *
 * <p>{@link MainServiceIntegrationApplication} is used instead of
 * {@code GymCrmApplication} to keep the workload-service classes (also on the
 * test classpath) out of the main context's component scan.
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
        // Shut the workload context down when the JVM exits. Testcontainers' Ryuk
        // reaper handles the containers; the workload Spring context needs an
        // explicit hook because nothing else knows it exists.
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (workloadContext != null && workloadContext.isActive()) {
                workloadContext.close();
            }
        }, "workload-context-shutdown"));
    }

    private static void startWorkloadService() {
        workloadContext = new SpringApplicationBuilder(TrainerWorkloadApplication.class)
                // Args have higher precedence than any property file, so workload's
                // own application.yml + main's application.properties can't leak in.
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
                        // Main's test classpath pulls in spring-boot-starter-data-jpa +
                        // postgresql-driver; in the same JVM, the workload context
                        // would try to bootstrap a JDBC DataSource it doesn't need.
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
