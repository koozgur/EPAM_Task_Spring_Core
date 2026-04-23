package com.gymcrm.integration.steps;

import com.gymcrm.integration.support.ServicePorts;
import com.gymcrm.integration.support.TestContext;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;

import static io.restassured.RestAssured.given;

/**
 * Cucumber steps for verifying JWT-based authentication between services.
 *
 * <p>Targets the workload endpoint ({@code /api/workload/{trainerUsername}})
 * using valid, invalid, and missing tokens. The workload service validates only
 * token presence and integrity; the JWT subject is not required to match the
 * path parameter.
 */
public class CrossServiceAuthSteps {

    @Autowired
    private TestContext testContext;

    @Autowired
    private ServicePorts ports;

    @When("the user calls the workload service summary endpoint with that JWT")
    public void callWithJwt() {
        String token = testContext.getJwtToken();
        testContext.setResponse(invokeSummary(token));
    }

    @When("the user calls the workload service summary endpoint with an invalid JWT")
    public void callWithInvalidJwt() {
        testContext.setResponse(invokeSummary("not.a.valid.token"));
    }

    @When("the user calls the workload service summary endpoint without a JWT")
    public void callWithoutJwt() {
        testContext.setResponse(invokeSummary(null));
    }

    private Response invokeSummary(String token) {
        String username = testContext.getTraineeUsername() != null
                ? testContext.getTraineeUsername()
                : "any.user";
        var request = given()
                .baseUri("http://localhost").port(ports.workload());
        if (token != null) {
            request = request.header("Authorization", "Bearer " + token);
        }
        return request.when().get("/api/workload/" + username);
    }
}
