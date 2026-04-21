package com.gymcrm.component.steps;

import com.gymcrm.component.support.TestContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Cucumber step definitions for training endpoints (POST /trainings).
 */
public class TrainingSteps {

    @Autowired
    private TestContext testContext;

    @When("the user adds a training with name {string}, date {string}, and duration {int}")
    public void addTraining(String name, String date, int duration) {
        String traineeUsername = testContext.getCurrentUsername();
        String trainerUsername = testContext.get("trainerUsername");
        addTrainingRequest(traineeUsername, trainerUsername, name, date, duration);
    }

    @When("the user adds a training with trainee {string} and the registered trainer")
    public void addTrainingWithCustomTrainee(String traineeUsername) {
        String trainerUsername = testContext.get("trainerUsername");
        addTrainingRequest(traineeUsername, trainerUsername, "Test Training", "2026-03-15", 60);
    }

    @Given("a training {string} on {string} for {int} minutes exists")
    public void createTraining(String name, String date, int duration) {
        String traineeUsername = testContext.getCurrentUsername();
        String trainerUsername = testContext.get("trainerUsername");
        Map<String, Object> body = new HashMap<>();
        body.put("traineeUsername", traineeUsername);
        body.put("trainerUsername", trainerUsername);
        body.put("name", name);
        body.put("date", date);
        body.put("duration", duration);

        Response response = given()
                .header("Authorization", "Bearer " + testContext.getJwtToken())
                .contentType(ContentType.JSON)
                .body(body)
                .when().post("/trainings");
        assertThat("Training creation should succeed", response.statusCode(), is(200));
    }

    private void addTrainingRequest(String traineeUsername, String trainerUsername,
                                    String name, String date, int duration) {
        Map<String, Object> body = new HashMap<>();
        body.put("traineeUsername", traineeUsername);
        body.put("trainerUsername", trainerUsername);
        body.put("name", name);
        body.put("date", date);
        body.put("duration", duration);

        Response response = given()
                .header("Authorization", "Bearer " + testContext.getJwtToken())
                .contentType(ContentType.JSON)
                .body(body)
                .when().post("/trainings");
        testContext.setResponse(response);
    }
}
