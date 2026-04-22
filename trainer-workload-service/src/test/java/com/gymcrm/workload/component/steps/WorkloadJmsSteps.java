package com.gymcrm.workload.component.steps;

import com.gymcrm.workload.component.support.TestContext;
import com.gymcrm.workload.dto.WorkloadRequest;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class WorkloadJmsSteps {

    @Autowired
    private TestContext testContext;

    @Autowired
    private JmsTemplate jmsTemplate;

    @When("a JMS workload {word} message is sent for trainer {string} with {int} minutes on {string}")
    public void sendJmsMessage(String actionType, String username, int duration, String date) {
        WorkloadRequest request = WorkloadRestSteps.buildRequest(username, duration, date, actionType);
        jmsTemplate.convertAndSend("trainer.workload.queue", request);
    }

    @Then("within {int} seconds the workload summary for {string} should show {int} minutes for year {int} month {int}")
    public void verifySummaryAsync(int timeout, String username, int expectedMinutes, int year, int month) {
        String token = testContext.getJwtToken();

        await().atMost(timeout, TimeUnit.SECONDS)
               .pollInterval(500, TimeUnit.MILLISECONDS)
               .untilAsserted(() -> {
                   Response response = given()
                           .header("Authorization", "Bearer " + token)
                       .when()
                           .get("/api/workload/" + username);

                   assertThat(response.statusCode(), is(200));
                   int actual = WorkloadRestSteps.extractDuration(response, year, month);
                   assertThat(actual, is(expectedMinutes));
               });
    }
}
