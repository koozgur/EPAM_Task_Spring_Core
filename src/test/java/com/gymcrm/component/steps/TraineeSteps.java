package com.gymcrm.component.steps;

import com.gymcrm.component.support.TestContext;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;

/**
 * Cucumber step definitions for POST /trainees/register scenarios.
 *
 * <p>Shared Given/Then steps (pre-registered trainee, generic response assertions including generated username/password/token) live in {@link CommonSteps}. This class covers trainee registration When actions and the trainee-specific duplicate-suffix assertion.
 */
public class TraineeSteps {

    @Autowired
    private TestContext testContext;

    @When("a/another trainee registers with first name {string} and last name {string}")
    public void registerTrainee(String firstName, String lastName) {
        register(Map.of("firstName", firstName, "lastName", lastName));
    }

    @When("a trainee registers with first name {string}, last name {string}, DOB {string}, address {string}")
    public void registerTraineeAllFields(String firstName, String lastName,
                                         String dob, String address) {
        register(Map.of(
                "firstName", firstName,
                "lastName", lastName,
                "dateOfBirth", dob,
                "address", address));
    }

    private void register(Map<String, Object> body) {
        Response response = given()
                .contentType(ContentType.JSON)
                .body(body)
                .post("/trainees/register");
        testContext.setResponse(response);
    }

    @Then("the second username should end with {string}")
    public void verifySecondUsernameEndsWith(String suffix) {
        String username = testContext.getResponse().jsonPath().getString("username");
        assertThat(username, endsWith(suffix));
    }
}
