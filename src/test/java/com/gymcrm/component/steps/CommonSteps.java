package com.gymcrm.component.steps;

import com.gymcrm.component.support.DatabaseCleaner;
import com.gymcrm.component.support.TestContext;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Shared Cucumber step definitions used across multiple feature files.
 *
 * <p>Includes: scenario lifecycle hooks, registration/auth setup steps,
 * and common response assertions.
 */
public class CommonSteps {

    @LocalServerPort
    private int port;

    @Autowired
    private TestContext testContext;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    //Lifecycle

    @Before
    public void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        databaseCleaner.clean();
    }

    //Registration (Given setup)

    @Given("a registered trainee with first name {string} and last name {string}")
    public void registerTrainee(String firstName, String lastName) {
        Response response = given()
                .contentType(ContentType.JSON)
                .body(Map.of("firstName", firstName, "lastName", lastName))
                .post("/trainees/register");

        assertThat(response.statusCode(), is(201));
        testContext.setCurrentUsername(response.jsonPath().getString("username"));
        testContext.setCurrentPassword(response.jsonPath().getString("password"));
        testContext.setJwtToken(response.jsonPath().getString("token"));
    }

    @Given("a registered trainee with first name {string}, last name {string}, DOB {string}, address {string}")
    public void registerTraineeAllFields(String firstName, String lastName,
                                         String dob, String address) {
        Response response = given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "firstName", firstName,
                        "lastName", lastName,
                        "dateOfBirth", dob,
                        "address", address))
                .post("/trainees/register");

        assertThat(response.statusCode(), is(201));
        testContext.setCurrentUsername(response.jsonPath().getString("username"));
        testContext.setCurrentPassword(response.jsonPath().getString("password"));
        testContext.setJwtToken(response.jsonPath().getString("token"));
    }

    @Given("a registered trainer with first name {string} and last name {string} and specialization id {int}")
    public void registerTrainer(String firstName, String lastName, int specId) {
        Response response = given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "firstName", firstName,
                        "lastName", lastName,
                        "specializationId", specId))
                .post("/trainers/register");

        assertThat(response.statusCode(), is(201));
        testContext.put("trainerUsername", response.jsonPath().getString("username"));
        testContext.put("trainerPassword", response.jsonPath().getString("password"));
        testContext.put("trainerToken", response.jsonPath().getString("token"));
    }

    @Given("a registered trainee and the user is authenticated")
    public void registerTraineeAndAuthenticate() {
        registerTrainee("John", "Doe");
        // Token already stored from registration
    }

    @Given("the user is authenticated")
    public void userIsAuthenticated() {
        // Registration already provides a JWT token in TestContext.
        // If somehow no token exists, login explicitly.
        if (testContext.getJwtToken() == null) {
            Response response = given()
                    .contentType(ContentType.JSON)
                    .body(Map.of(
                            "username", testContext.getCurrentUsername(),
                            "password", testContext.getCurrentPassword()))
                    .post("/login");

            assertThat(response.statusCode(), is(200));
            testContext.setJwtToken(response.jsonPath().getString("token"));
        }
    }

    @Given("the trainer is authenticated")
    public void trainerIsAuthenticated() {
        String token = testContext.get("trainerToken");
        if (token == null) {
            Response response = given()
                    .contentType(ContentType.JSON)
                    .body(Map.of(
                            "username", (String) testContext.get("trainerUsername"),
                            "password", (String) testContext.get("trainerPassword")))
                    .post("/login");
            assertThat(response.statusCode(), is(200));
            testContext.put("trainerToken", response.jsonPath().getString("token"));
        }
    }

    @Given("the trainer is assigned to the trainee")
    public void assignTrainerToTrainee() {
        String trainerUsername = testContext.get("trainerUsername");
        Response response = given()
                .header("Authorization", "Bearer " + testContext.getJwtToken())
                .contentType(ContentType.JSON)
                .body(Map.of("trainerUsernames", List.of(trainerUsername)))
                .when().put("/trainees/" + testContext.getCurrentUsername() + "/trainers");
        assertThat(response.statusCode(), is(200));
    }

    //Response assertions

    @Then("the response status should be {int}")
    public void verifyResponseStatus(int expectedStatus) {
        assertThat(testContext.getResponse().statusCode(), is(expectedStatus));
    }

    @Then("the response should contain a non-empty {string} field")
    public void verifyNonEmptyField(String fieldName) {
        String value = testContext.getResponse().jsonPath().getString(fieldName);
        assertThat(value, is(notNullValue()));
        assertThat(value, is(not(emptyString())));
    }

    @Then("the response should contain field error for {string}")
    public void verifyFieldError(String fieldName) {
        Response response = testContext.getResponse();
        assertThat(response.jsonPath().getString("fieldErrors." + fieldName),
                is(notNullValue()));
    }

    @Then("the response should contain the username")
    public void verifyResponseContainsUsername() {
        String username = testContext.getResponse().jsonPath().getString("username");
        assertThat(username, is(equalTo(testContext.getCurrentUsername())));
    }

    @Then("the response should contain a generated username and password and token")
    public void verifyRegistrationFields() {
        Response response = testContext.getResponse();
        assertThat(response.jsonPath().getString("username"), not(emptyOrNullString()));
        assertThat(response.jsonPath().getString("password"), not(emptyOrNullString()));
        assertThat(response.jsonPath().getString("token"), not(emptyOrNullString()));
    }

    @Then("the response should contain a generated username starting with {string}")
    public void verifyUsernameStartsWith(String prefix) {
        String username = testContext.getResponse().jsonPath().getString("username");
        assertThat(username, startsWith(prefix));
    }

    @Then("the response should contain field {string} with value {string}")
    public void verifyFieldValue(String fieldName, String expectedValue) {
        String value = testContext.getResponse().jsonPath().getString(fieldName);
        assertThat(value, is(equalTo(expectedValue)));
    }

    @Then("the response should be a non-empty list")
    public void verifyNonEmptyList() {
        List<?> list = testContext.getResponse().jsonPath().getList("$");
        assertThat(list, is(not(empty())));
    }

    @Then("the response should be an empty list")
    public void verifyEmptyList() {
        List<?> list = testContext.getResponse().jsonPath().getList("$");
        assertThat(list, is(empty()));
    }
}
