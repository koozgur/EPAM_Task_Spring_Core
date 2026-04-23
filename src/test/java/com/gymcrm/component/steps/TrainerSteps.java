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
 * Cucumber step definitions for trainer endpoints.
 */
public class TrainerSteps {

    @Autowired
    private TestContext testContext;

    //Registration

    @When("a trainer registers with first name {string} and last name {string} and specialization id {int}")
    public void registerTrainer(String firstName, String lastName, int specializationId) {
        Response response = given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "firstName", firstName,
                        "lastName", lastName,
                        "specializationId", specializationId))
                .post("/trainers/register");
        testContext.setResponse(response);
    }

    //Profile

    @When("the trainer requests their profile")
    public void getTrainerProfile() {
        String trainerUsername = testContext.get("trainerUsername");
        String trainerToken = testContext.get("trainerToken");
        Response response = given()
                .header("Authorization", "Bearer " + trainerToken)
                .when().get("/trainers/" + trainerUsername);
        testContext.setResponse(response);
    }

    @When("the trainer requests profile for username {string}")
    public void getTrainerProfileByUsername(String username) {
        String trainerToken = testContext.get("trainerToken");
        Response response = given()
                .header("Authorization", "Bearer " + trainerToken)
                .when().get("/trainers/" + username);
        testContext.setResponse(response);
    }

    //Update

    @When("the trainer updates their profile with first name {string}, last name {string}, active {word}")
    public void updateTrainerProfile(String firstName, String lastName, String active) {
        String trainerUsername = testContext.get("trainerUsername");
        String trainerToken = testContext.get("trainerToken");
        Map<String, Object> body = new HashMap<>();
        body.put("firstName", firstName);
        body.put("lastName", lastName);
        body.put("isActive", Boolean.parseBoolean(active));

        Response response = given()
                .header("Authorization", "Bearer " + trainerToken)
                .contentType(ContentType.JSON)
                .body(body)
                .when().put("/trainers/" + trainerUsername);
        testContext.setResponse(response);
    }

    //Activation

    @When("the trainer deactivates their account")
    public void deactivateTrainer() {
        String trainerUsername = testContext.get("trainerUsername");
        String trainerToken = testContext.get("trainerToken");
        Response response = given()
                .header("Authorization", "Bearer " + trainerToken)
                .contentType(ContentType.JSON)
                .body(Map.of("isActive", false))
                .when().patch("/trainers/" + trainerUsername + "/activation");
        testContext.setResponse(response);
    }

    @Given("the trainer has been deactivated")
    public void trainerIsDeactivated() {
        String trainerUsername = testContext.get("trainerUsername");
        String trainerToken = testContext.get("trainerToken");
        Response response = given()
                .header("Authorization", "Bearer " + trainerToken)
                .contentType(ContentType.JSON)
                .body(Map.of("isActive", false))
                .when().patch("/trainers/" + trainerUsername + "/activation");
        assertThat(response.statusCode(), is(200));
    }

    //Trainings

    @When("the trainer requests their trainings from {string} to {string}")
    public void getTrainerTrainingsFiltered(String from, String to) {
        String trainerUsername = testContext.get("trainerUsername");
        String trainerToken = testContext.get("trainerToken");
        Response response = given()
                .header("Authorization", "Bearer " + trainerToken)
                .queryParam("periodFrom", from)
                .queryParam("periodTo", to)
                .when().get("/trainers/" + trainerUsername + "/trainings");
        testContext.setResponse(response);
    }
}
