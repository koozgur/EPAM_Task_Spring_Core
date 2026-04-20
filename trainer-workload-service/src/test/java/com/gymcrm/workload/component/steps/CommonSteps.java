package com.gymcrm.workload.component.steps;

import com.gymcrm.workload.component.support.JwtTestHelper;
import com.gymcrm.workload.component.support.TestContext;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.restassured.RestAssured;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.mongodb.core.MongoTemplate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Shared Cucumber step definitions for workload service component tests.
 *
 * <p>Includes: scenario lifecycle hooks, JWT setup, and response assertions.
 */
public class CommonSteps {

    @LocalServerPort
    private int port;

    @Autowired
    private TestContext testContext;

    @Autowired
    private JwtTestHelper jwtTestHelper;

    @Autowired
    private MongoTemplate mongoTemplate;

    //Lifecycle

    @Before
    public void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        mongoTemplate.getDb().drop();
    }

    //Authentication setup

    @Given("a valid JWT token")
    public void validJwtToken() {
        testContext.setJwtToken(jwtTestHelper.generateValidToken("test.user"));
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
}
