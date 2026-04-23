package com.gymcrm.component.support;

import com.gymcrm.security.LoginAttemptService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * Resets all scenario-scoped state between Cucumber scenarios:
 * truncates user-created tables (preserving {@code training_types} seeded by
 * {@code data.sql}) and clears the in-memory brute-force counter state held
 * by {@link LoginAttemptService}.
 *
 * <p>Deletion order respects foreign key constraints.
 */
@Component
public class DatabaseCleaner {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private LoginAttemptService loginAttemptService;

    @Transactional
    public void clean() {
        entityManager.createNativeQuery("DELETE FROM trainings").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM trainee_trainer").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM trainees").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM trainers").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM users").executeUpdate();
        entityManager.flush();
        clearLoginAttempts();
    }

    // LoginAttemptService stores counters in a private in-memory map with no
    // public reset hook. Reflection lets the test harness clear it without
    // widening the production API surface.
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
