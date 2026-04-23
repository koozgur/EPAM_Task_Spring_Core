package com.gymcrm.component.steps;

import com.gymcrm.component.support.TestContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;

/**
 * Cucumber step definitions for trainee endpoints.
 */
public class TraineeSteps {

    @Autowired
    private TestContext testContext;

    //Registration

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

    //Profile

    @When("the user requests the trainee profile")
    public void getTraineeProfile() {
        Response response = given()
                .header("Authorization", "Bearer " + testContext.getJwtToken())
                .when().get("/trainees/" + testContext.getCurrentUsername());
        testContext.setResponse(response);
    }

    @When("the user requests profile for username {string}")
    public void getTraineeProfileByUsername(String username) {
        Response response = given()
                .header("Authorization", "Bearer " + testContext.getJwtToken())
                .when().get("/trainees/" + username);
        testContext.setResponse(response);
    }

    //Update

    @When("the user updates the trainee profile with first name {string}, last name {string}, DOB {string}, address {string}, active {word}")
    public void updateTraineeProfile(String firstName, String lastName,
                                     String dob, String address, String active) {
        Map<String, Object> body = new HashMap<>();
        body.put("firstName", firstName);
        body.put("lastName", lastName);
        body.put("dateOfBirth", dob);
        body.put("address", address);
        body.put("isActive", Boolean.parseBoolean(active));

        Response response = given()
                .header("Authorization", "Bearer " + testContext.getJwtToken())
                .contentType(ContentType.JSON)
                .body(body)
                .when().put("/trainees/" + testContext.getCurrentUsername());
        testContext.setResponse(response);
    }

    @When("the user updates profile for username {string} with first name {string}, last name {string}, active {word}")
    public void updateTraineeProfileByUsername(String username, String firstName,
                                               String lastName, String active) {
        Map<String, Object> body = new HashMap<>();
        body.put("firstName", firstName);
        body.put("lastName", lastName);
        body.put("isActive", Boolean.parseBoolean(active));

        Response response = given()
                .header("Authorization", "Bearer " + testContext.getJwtToken())
                .contentType(ContentType.JSON)
                .body(body)
                .when().put("/trainees/" + username);
        testContext.setResponse(response);
    }

    //Delete

    @When("the user deletes the trainee")
    public void deleteTrainee() {
        testContext.put("deletedUsername", testContext.getCurrentUsername());
        Response response = given()
                .header("Authorization", "Bearer " + testContext.getJwtToken())
                .when().delete("/trainees/" + testContext.getCurrentUsername());
        testContext.setResponse(response);
    }

    @When("the user deletes trainee with username {string}")
    public void deleteTraineeByUsername(String username) {
        Response response = given()
                .header("Authorization", "Bearer " + testContext.getJwtToken())
                .when().delete("/trainees/" + username);
        testContext.setResponse(response);
    }

    @When("a new user verifies the deleted trainee profile")
    public void verifyDeletedTraineeProfile() {
        String deletedUsername = testContext.get("deletedUsername");
        // Register a new trainee to get a fresh valid token
        Response regResponse = given()
                .contentType(ContentType.JSON)
                .body(Map.of("firstName", "Verify", "lastName", "User"))
                .post("/trainees/register");
        assertThat(regResponse.statusCode(), is(201));
        String freshToken = regResponse.jsonPath().getString("token");

        Response response = given()
                .header("Authorization", "Bearer " + freshToken)
                .when().get("/trainees/" + deletedUsername);
        testContext.setResponse(response);
    }

    //Activation

    @When("the user deactivates the trainee")
    public void deactivateTrainee() {
        Response response = given()
                .header("Authorization", "Bearer " + testContext.getJwtToken())
                .contentType(ContentType.JSON)
                .body(Map.of("isActive", false))
                .when().patch("/trainees/" + testContext.getCurrentUsername() + "/activation");
        testContext.setResponse(response);
    }

    @When("the user activates the trainee")
    public void activateTrainee() {
        Response response = given()
                .header("Authorization", "Bearer " + testContext.getJwtToken())
                .contentType(ContentType.JSON)
                .body(Map.of("isActive", true))
                .when().patch("/trainees/" + testContext.getCurrentUsername() + "/activation");
        testContext.setResponse(response);
    }

    @Given("the trainee has been deactivated")
    public void traineeIsDeactivated() {
        Response response = given()
                .header("Authorization", "Bearer " + testContext.getJwtToken())
                .contentType(ContentType.JSON)
                .body(Map.of("isActive", false))
                .when().patch("/trainees/" + testContext.getCurrentUsername() + "/activation");
        assertThat(response.statusCode(), is(200));
    }

    //Trainer management

    @When("the user requests available trainers for the trainee")
    public void getAvailableTrainers() {
        Response response = given()
                .header("Authorization", "Bearer " + testContext.getJwtToken())
                .when().get("/trainees/" + testContext.getCurrentUsername() + "/available-trainers");
        testContext.setResponse(response);
    }

    @When("the user updates the trainee trainer list with usernames {string}")
    public void updateTrainerList(String usernames) {
        List<String> trainerUsernames = List.of(usernames.split(","));
        Response response = given()
                .header("Authorization", "Bearer " + testContext.getJwtToken())
                .contentType(ContentType.JSON)
                .body(Map.of("trainerUsernames", trainerUsernames))
                .when().put("/trainees/" + testContext.getCurrentUsername() + "/trainers");
        testContext.setResponse(response);
    }

    //Trainings

    @When("the user requests trainee trainings")
    public void getTraineeTrainings() {
        Response response = given()
                .header("Authorization", "Bearer " + testContext.getJwtToken())
                .when().get("/trainees/" + testContext.getCurrentUsername() + "/trainings");
        testContext.setResponse(response);
    }

    @When("the user requests trainee trainings from {string} to {string}")
    public void getTraineeTrainingsFiltered(String from, String to) {
        Response response = given()
                .header("Authorization", "Bearer " + testContext.getJwtToken())
                .queryParam("periodFrom", from)
                .queryParam("periodTo", to)
                .when().get("/trainees/" + testContext.getCurrentUsername() + "/trainings");
        testContext.setResponse(response);
    }
}
