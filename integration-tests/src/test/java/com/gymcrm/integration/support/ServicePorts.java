package com.gymcrm.integration.support;

import com.gymcrm.integration.SpringIntegrationTestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Provides access to the runtime ports of both services.
 *
 * <p>The main service port is resolved lazily from {@link Environment}, as
 * {@code local.server.port} becomes available only after server startup.
 *
 * <p>The workload service port is initialized during startup in
 * {@link SpringIntegrationTestConfig} and exposed via a static accessor.
 */
@Component
public class ServicePorts {

    @Autowired
    private Environment environment;

    public int main() {
        return environment.getRequiredProperty("local.server.port", Integer.class);
    }

    public int workload() {
        return SpringIntegrationTestConfig.workloadPort();
    }
}
