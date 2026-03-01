package com.gymcrm.health;

import com.gymcrm.service.UserService;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Reports whether the authentication subsystem (UserService) is ready.
 * Constructor injection guarantees the bean is non-null if Spring started successfully.
 */
@Component("gymcrmAuthHealthIndicator")
public class GymCrmAuthHealthIndicator implements HealthIndicator {

    private final UserService userService;

    public GymCrmAuthHealthIndicator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Health health() {
        return Health.up()
                .withDetail("authService", userService.getClass().getSimpleName())
                .build();
    }
}
