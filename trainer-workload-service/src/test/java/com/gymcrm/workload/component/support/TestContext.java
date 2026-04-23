package com.gymcrm.workload.component.support;

import io.cucumber.spring.ScenarioScope;
import io.restassured.response.Response;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds shared state across Cucumber step definitions within a single scenario.
 * A new instance is created for each scenario via {@link ScenarioScope}.
 */
@Component
@ScenarioScope
public class TestContext {

    private Response response;
    private String jwtToken;
    private final Map<String, Object> scenarioData = new HashMap<>();

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public void put(String key, Object value) {
        scenarioData.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) scenarioData.get(key);
    }
}
