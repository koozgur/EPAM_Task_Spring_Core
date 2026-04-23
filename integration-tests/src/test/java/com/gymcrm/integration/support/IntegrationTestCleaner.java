package com.gymcrm.integration.support;

import com.gymcrm.integration.SpringIntegrationTestConfig;
import com.gymcrm.security.LoginAttemptService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * Resets shared state between Cucumber scenarios.
 *
 * <p>Performs:
 * <ul>
 *   <li>JPA cleanup (FK-safe order, keeps {@code training_types})</li>
 *   <li>Workload MongoDB reset</li>
 *   <li>Login attempt counter reset</li>
 * </ul>
 *
 * <p>The workload {@link MongoTemplate} is retrieved from the workload context
 * since it is not part of the main Spring context.
 */
@Component
public class IntegrationTestCleaner {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private LoginAttemptService loginAttemptService;

    @Transactional
    public void clean() {
        cleanMainDatabase();
        cleanWorkloadMongo();
        clearLoginAttempts();
    }

    private void cleanMainDatabase() {
        entityManager.createNativeQuery("DELETE FROM trainings").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM trainee_trainer").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM trainees").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM trainers").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM users").executeUpdate();
        entityManager.flush();
    }

    private void cleanWorkloadMongo() {
        MongoTemplate mongoTemplate = SpringIntegrationTestConfig.workloadContext()
                .getBean(MongoTemplate.class);
        mongoTemplate.getDb().drop();
    }

    // Clear in-memory login attempt counters via reflection (no public reset API).
    private void clearLoginAttempts() {
        try {
            Field field = LoginAttemptService.class.getDeclaredField("attempts");
            field.setAccessible(true);
            ((Map<?, ?>) field.get(loginAttemptService)).clear();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Unable to reset LoginAttemptService state", e);
        }
    }
}
