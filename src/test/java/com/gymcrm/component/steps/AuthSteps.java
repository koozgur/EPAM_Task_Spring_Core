package com.gymcrm.component.steps;

import com.gymcrm.component.support.TestContext;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static io.restassured.RestAssured.given;

/**
 * Cucumber step definitions for POST /login scenarios.
 *
 * <p>Given/Then steps (registration, response assertions) live in {@link CommonSteps};
 * this class defines the When actions that invoke the login endpoint and store the response in {@link TestContext} for downstream assertions.
 */
public class AuthSteps {

    @Autowired
    private TestContext testContext;

    @When("the user logs in with the registered credentials")
    public void loginWithRegisteredCredentials() {
        login(testContext.getCurrentUsername(), testContext.getCurrentPassword());
    }

    @When("the user logs in with password {string}")
    public void loginWithPassword(String password) {
        login(testContext.getCurrentUsername(), password);
    }

    @When("the user logs in with username {string} and password {string}")
    public void loginWithUsernameAndPassword(String username, String password) {
        login(username, password);
    }

    private void login(String username, String password) {
        Response response = given()
                .contentType(ContentType.JSON)
                .body(Map.of("username", username, "password", password))
                .post("/login");
        testContext.setResponse(response);
    }
}
