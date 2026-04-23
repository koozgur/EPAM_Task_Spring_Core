package com.gymcrm.integration.support;

import com.gymcrm.integration.SpringIntegrationTestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Exposes both services' listening ports to step definitions.
 *
 * <p>The main service's {@code local.server.port} is only published after the
 * web server starts at the end of context refresh, so {@code @Value} / {@code @LocalServerPort}
 * on a singleton field fails (resolved too early). Reading from {@link Environment}
 * at call time defers the lookup until the first step asks for the port, by which
 * time the server is listening.
 *
 * <p>The workload port is captured during its {@link
 * org.springframework.boot.builder.SpringApplicationBuilder#run} call in
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
