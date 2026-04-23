package com.gymcrm.integration.support;

import io.cucumber.spring.ScenarioScope;
import io.restassured.response.Response;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Scenario-scoped state shared across Cucumber steps.
 *
 * <p>Stores per-scenario data (e.g., credentials, JWT, responses) without
 * leaking state between scenarios.
 * Explicit bean name avoids collision with another TestContext bean on the test classpath.
 */
@Component("integrationTestContext")
@ScenarioScope
public class TestContext {

    private Response response;
    private String jwtToken;
    private String traineeUsername;
    private String traineePassword;
    private String trainerUsername;
    private String trainerPassword;
    private final Map<String, Object> scenarioData = new HashMap<>();

    public Response getResponse() { return response; }
    public void setResponse(Response response) { this.response = response; }

    public String getJwtToken() { return jwtToken; }
    public void setJwtToken(String jwtToken) { this.jwtToken = jwtToken; }

    public String getTraineeUsername() { return traineeUsername; }
    public void setTraineeUsername(String traineeUsername) { this.traineeUsername = traineeUsername; }

    public String getTraineePassword() { return traineePassword; }
    public void setTraineePassword(String traineePassword) { this.traineePassword = traineePassword; }

    public String getTrainerUsername() { return trainerUsername; }
    public void setTrainerUsername(String trainerUsername) { this.trainerUsername = trainerUsername; }

    public String getTrainerPassword() { return trainerPassword; }
    public void setTrainerPassword(String trainerPassword) { this.trainerPassword = trainerPassword; }

    public void put(String key, Object value) { scenarioData.put(key, value); }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) { return (T) scenarioData.get(key); }
}
