package com.gymcrm.integration.steps;

import com.gymcrm.integration.support.IntegrationTestCleaner;
import com.gymcrm.integration.support.ServicePorts;
import com.gymcrm.integration.support.TestContext;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Shared Cucumber step definitions used across integration scenarios:
 * scenario lifecycle hooks, registration/auth setup, and response assertions.
 *
 * <p>Unlike component tests, there's no global {@code RestAssured.port}: each
 * step targets either the main or the workload service explicitly by URL,
 * because two services listen on different ports in the same JVM.
 */
public class CommonSteps {

    @Autowired
    private TestContext testContext;

    @Autowired
    private IntegrationTestCleaner cleaner;

    @Autowired
    private ServicePorts ports;

    @Before
    public void setUp() {
        // Disable global base URI/port — every request uses an absolute URL,
        // since the main and workload services are on different random ports.
        RestAssured.reset();
        cleaner.clean();
    }

    @Given("both microservices are running")
    public void bothServicesAreRunning() {
        // Booted by SpringIntegrationTestConfig's static init; pure readability step.
        assertThat("main service port should be assigned", ports.main() > 0, is(true));
        assertThat("workload service port should be assigned", ports.workload() > 0, is(true));
    }

    @Given("a trainee and trainer are registered in the main service")
    public void registerTraineeAndTrainer() {
        registerTrainee("John", "Doe");
        registerTrainer("Mike", "Johnson", 1);
        assignTrainerToTrainee();
    }

    // POST /trainings rejects with 409 if the trainer is not yet on the
    // trainee's assigned list. Integration scenarios assume both exist, so
    // this is wired into the Given setup rather than exposed as its own step.
    private void assignTrainerToTrainee() {
        Response response = given()
                .baseUri("http://localhost").port(ports.main())
                .header("Authorization", "Bearer " + testContext.getJwtToken())
                .contentType(ContentType.JSON)
                .body(Map.of("trainerUsernames", List.of(testContext.getTrainerUsername())))
                .when().put("/trainees/" + testContext.getTraineeUsername() + "/trainers");
        assertThat("trainer assignment should succeed", response.statusCode(), is(200));
    }

    @Given("a trainee is registered in the main service and receives a JWT")
    public void registerTraineeReceivesJwt() {
        registerTrainee("John", "Doe");
    }

    @Given("the user is authenticated in the main service")
    public void userIsAuthenticated() {
        // Registration responses carry the JWT, so authentication is already satisfied.
        assertThat("JWT should be present after registration",
                testContext.getJwtToken(), is(org.hamcrest.Matchers.notNullValue()));
    }

    private void registerTrainee(String firstName, String lastName) {
        Response response = given()
                .baseUri("http://localhost").port(ports.main())
                .contentType(ContentType.JSON)
                .body(Map.of("firstName", firstName, "lastName", lastName))
                .post("/trainees/register");

        assertThat(response.statusCode(), is(201));
        testContext.setTraineeUsername(response.jsonPath().getString("username"));
        testContext.setTraineePassword(response.jsonPath().getString("password"));
        testContext.setJwtToken(response.jsonPath().getString("token"));
    }

    private void registerTrainer(String firstName, String lastName, int specId) {
        Response response = given()
                .baseUri("http://localhost").port(ports.main())
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "firstName", firstName,
                        "lastName", lastName,
                        "specializationId", specId))
                .post("/trainers/register");

        assertThat(response.statusCode(), is(201));
        testContext.setTrainerUsername(response.jsonPath().getString("username"));
        testContext.setTrainerPassword(response.jsonPath().getString("password"));
    }

    @Then("the response status should be {int}")
    public void verifyResponseStatus(int expectedStatus) {
        assertThat(testContext.getResponse().statusCode(), is(expectedStatus));
    }
}
