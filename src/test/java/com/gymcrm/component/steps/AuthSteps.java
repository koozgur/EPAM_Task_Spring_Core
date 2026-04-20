package com.gymcrm.component.steps;

import com.gymcrm.component.support.TestContext;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static io.restassured.RestAssured.given;

/**
 * Cucumber step definitions for authentication endpoints
 * ({@code POST /login}, {@code PUT /change-password}) and brute-force
 * protection scenarios.
 *
 * <p>Given/Then steps (registration, response assertions) live in {@link CommonSteps};
 * this class defines the When actions that invoke the auth endpoints and store
 * the response in {@link TestContext} for downstream assertions.
 */
public class AuthSteps {

    @Autowired
    private TestContext testContext;

    // ── Login ────────────────────────────────────────────────────────────────

    @When("the user logs in with the registered credentials")
    @When("the user attempts login with the correct credentials")
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

    @When("the user fails login {int} times with wrong password")
    public void failLoginNTimes(int times) {
        for (int i = 0; i < times; i++) {
            login(testContext.getCurrentUsername(), "wrongPassword");
        }
    }

    private void login(String username, String password) {
        Response response = given()
                .contentType(ContentType.JSON)
                .body(Map.of("username", username, "password", password))
                .post("/login");
        testContext.setResponse(response);
    }

    // ── Change password ──────────────────────────────────────────────────────

    @When("the user changes password to {string}")
    public void changePassword(String newPassword) {
        Response response = putChangePassword(
                testContext.getJwtToken(),
                testContext.getCurrentPassword(),
                newPassword);
        testContext.setResponse(response);
        if (response.statusCode() == 200) {
            // Keep TestContext aligned so downstream login steps reuse the new password.
            testContext.setCurrentPassword(newPassword);
        }
    }

    @When("the user changes password with wrong old password to {string}")
    public void changePasswordWrongOld(String newPassword) {
        Response response = putChangePassword(
                testContext.getJwtToken(),
                "wrongOldPassword",
                newPassword);
        testContext.setResponse(response);
    }

    @When("the user attempts to change password without a token")
    public void changePasswordUnauth() {
        Response response = putChangePassword(
                null,
                testContext.getCurrentPassword(),
                "newPassword123");
        testContext.setResponse(response);
    }

    private Response putChangePassword(String token, String oldPassword, String newPassword) {
        var request = given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "username", testContext.getCurrentUsername(),
                        "oldPassword", oldPassword,
                        "newPassword", newPassword));
        if (token != null) {
            request = request.header("Authorization", "Bearer " + token);
        }
        // .when() routes the request through REST-assured's modern execution
        // path; the direct .put() shortcut triggers the legacy HTTPBuilder
        // failure handler on 4xx+JSON-body responses in REST-assured 5.4.0.
        return request.when().put("/change-password");
    }
}
