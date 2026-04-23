package com.gymcrm.integration.steps;

import com.gymcrm.integration.support.ServicePorts;
import com.gymcrm.integration.support.TestContext;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;

import static io.restassured.RestAssured.given;

/**
 * Steps that verify a JWT minted by the main service is accepted (or properly
 * rejected) by the workload service — the two services share a JWT secret.
 *
 * <p>The target endpoint is GET {@code /api/workload/{trainerUsername}}. The
 * subject of the JWT does not need to match the path parameter: the workload
 * service only asserts that the token is present and valid.
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
