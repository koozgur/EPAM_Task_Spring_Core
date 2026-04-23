package com.gymcrm.integration.steps;

import com.gymcrm.integration.support.ServicePorts;
import com.gymcrm.integration.support.TestContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.awaitility.Awaitility;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.Month;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * End-to-end steps covering training lifecycle events and their async propagation
 * to the workload service through JMS.
 */
public class TrainingWorkloadFlowSteps {

    @Autowired
    private TestContext testContext;

    @Autowired
    private ServicePorts ports;

    @When("the user adds a training for {int} minutes on {string} via the main service")
    public void addTraining(int duration, String date) {
        Response response = postTraining(
                testContext.getTraineeUsername(),
                testContext.getTrainerUsername(),
                "Integration Training",
                date, duration);
        testContext.setResponse(response);
        assertThat("training creation should succeed", response.statusCode(), is(200));
    }

    @Given("a training for {int} minutes on {string} exists in the main service")
    public void trainingExists(int duration, String date) {
        Response response = postTraining(
                testContext.getTraineeUsername(),
                testContext.getTrainerUsername(),
                "Integration Training",
                date, duration);
        assertThat("pre-seed training should be accepted", response.statusCode(), is(200));
    }

    @When("the trainee is deleted in the main service")
    public void deleteTrainee() {
        Response response = given()
                .baseUri("http://localhost").port(ports.main())
                .header("Authorization", "Bearer " + testContext.getJwtToken())
                .when().delete("/trainees/" + testContext.getTraineeUsername());
        testContext.setResponse(response);
        assertThat("trainee deletion should succeed", response.statusCode(), is(200));
    }

    @Then("within {int} seconds the workload service shows {int} minutes for that trainer in {word} {int}")
    public void verifyWorkload(int timeoutSeconds, int expectedMinutes, String monthName, int year) {
        int month = Month.valueOf(monthName.toUpperCase(Locale.ROOT)).getValue();
        String trainerUsername = testContext.getTrainerUsername();
        String jwt = testContext.getJwtToken();

        // JMS consumption is asynchronous — poll the workload summary until the
        // aggregated minutes for the target (year, month) equal the expectation.
        Awaitility.await()
                .atMost(Duration.ofSeconds(timeoutSeconds))
                .pollInterval(Duration.ofMillis(500))
                .untilAsserted(() -> {
                    Response response = given()
                            .baseUri("http://localhost").port(ports.workload())
                            .header("Authorization", "Bearer " + jwt)
                            .when().get("/api/workload/" + trainerUsername);
                    assertThat("workload summary should return 200",
                            response.statusCode(), is(200));
                    assertThat(
                            "trainer " + trainerUsername + " should have " + expectedMinutes
                                    + " min in " + monthName + " " + year,
                            totalMinutes(response, year, month),
                            is(expectedMinutes));
                });
    }

    private Response postTraining(String traineeUsername, String trainerUsername,
                                   String name, String date, int duration) {
        Map<String, Object> body = new HashMap<>();
        body.put("traineeUsername", traineeUsername);
        body.put("trainerUsername", trainerUsername);
        body.put("name", name);
        body.put("date", date);
        body.put("duration", duration);

        return given()
                .baseUri("http://localhost").port(ports.main())
                .header("Authorization", "Bearer " + testContext.getJwtToken())
                .contentType(ContentType.JSON)
                .body(body)
                .when().post("/trainings");
    }

    /**
     * Sums the {@code trainingSummaryDuration} for the target year/month from the
     * summary response. Returns 0 if the year or month is absent — the workload
     * service returns an empty {@code years} list when no data exists, and after
     * a DELETE that decrements to zero, that month entry is pruned.
     */
    @SuppressWarnings("unchecked")
    private int totalMinutes(Response response, int year, int month) {
        List<Map<String, Object>> years = response.jsonPath().getList("years");
        if (years == null) return 0;
        return years.stream()
                .filter(y -> ((Number) y.get("year")).intValue() == year)
                .flatMap(y -> ((List<Map<String, Object>>) y.get("months")).stream())
                .filter(m -> ((Number) m.get("month")).intValue() == month)
                .mapToInt(m -> ((Number) m.get("trainingSummaryDuration")).intValue())
                .sum();
    }
}
