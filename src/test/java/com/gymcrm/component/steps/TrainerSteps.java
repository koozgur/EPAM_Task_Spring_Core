package com.gymcrm.component.steps;

import com.gymcrm.component.support.TestContext;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static io.restassured.RestAssured.given;

/**
 * Cucumber step definitions for POST /trainers/register scenarios.
 *
 * <p>Shared Given/Then steps (pre-registered trainer, generic response assertions) live in {@link CommonSteps}; this class covers the trainer registration When action.
 */
public class TrainerSteps {

    @Autowired
    private TestContext testContext;

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
}
