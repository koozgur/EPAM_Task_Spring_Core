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
 * Resets cross-scenario state between Cucumber integration scenarios:
 * <ul>
 *   <li>Truncates main-service JPA tables (FK-safe order, preserving {@code training_types})</li>
 *   <li>Drops the workload-service {@code trainer_workload} collection</li>
 *   <li>Clears the in-memory brute-force counter</li>
 * </ul>
 *
 * <p>The workload {@link MongoTemplate} is fetched from the workload child context
 * (started in {@link SpringIntegrationTestConfig}) rather than autowired, since
 * that bean lives in a different Spring context.
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

    // LoginAttemptService keeps counters in a private in-memory map with no
    // public reset hook; reflection clears it without widening the prod API.
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
