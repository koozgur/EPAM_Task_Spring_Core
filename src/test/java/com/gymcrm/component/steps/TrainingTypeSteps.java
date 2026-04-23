package com.gymcrm.component.steps;

import com.gymcrm.component.support.TestContext;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;

import static io.restassured.RestAssured.given;

/**
 * Cucumber step definitions for GET /training-types endpoint.
 */
public class TrainingTypeSteps {

    @Autowired
    private TestContext testContext;

    @When("the user requests all training types")
    public void getAllTrainingTypes() {
        Response response = given()
                .header("Authorization", "Bearer " + testContext.getJwtToken())
                .when().get("/training-types");
        testContext.setResponse(response);
    }
}
