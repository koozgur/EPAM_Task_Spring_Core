package com.gymcrm.workload.component.steps;

import com.gymcrm.workload.component.support.TestContext;
import com.gymcrm.workload.dto.WorkloadRequest;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

public class WorkloadRestSteps {

    @Autowired
    private TestContext testContext;

    //POST workload event
    @When("a workload {word} event is posted for trainer {string} with {int} minutes on {string}")
    public void postWorkloadEvent(String actionType, String username, int duration, String date) {
        WorkloadRequest request = buildRequest(username, duration, date, actionType);

        Response response = given()
                .header("Authorization", "Bearer " + testContext.getJwtToken())
                .contentType(ContentType.JSON)
                .body(request)
            .when()
                .post("/api/workload");

        testContext.setResponse(response);
    }

    //GET workload summary

    @When("the workload summary is requested for {string}")
    public void getWorkloadSummary(String username) {
        Response response = given()
                .header("Authorization", "Bearer " + testContext.getJwtToken())
            .when()
                .get("/api/workload/" + username);

        testContext.setResponse(response);
    }

    @When("the workload summary is requested for {string} without authentication")
    public void getWorkloadSummaryWithoutAuth(String username) {
        Response response = given()
            .when()
                .get("/api/workload/" + username);

        testContext.setResponse(response);
    }

    //Summary assertions

    @Then("the summary should show {int} minutes for year {int} month {int}")
    public void verifySummaryDuration(int expectedMinutes, int year, int month) {
        int actual = extractDuration(testContext.getResponse(), year, month);
        assertThat(actual, is(expectedMinutes));
    }

    @Then("the summary years list should be empty")
    public void verifySummaryEmpty() {
        List<Object> years = testContext.getResponse().jsonPath().getList("years");
        assertThat(years, is(empty()));
    }

    //Helpers

    static WorkloadRequest buildRequest(String username, int duration, String date, String actionType) {
        WorkloadRequest request = new WorkloadRequest();
        request.setTrainerUsername(username);
        String[] parts = username.split("\\.");
        request.setFirstName(capitalize(parts[0]));
        request.setLastName(parts.length > 1 ? capitalize(parts[1]) : "Unknown");
        request.setIsActive(true);
        request.setTrainingDate(LocalDate.parse(date));
        request.setTrainingDuration(duration);
        request.setActionType(WorkloadRequest.ActionType.valueOf(actionType));
        return request;
    }

    @SuppressWarnings("unchecked")
    static int extractDuration(Response response, int year, int month) {
        List<Map<String, Object>> years = response.jsonPath().getList("years");
        return years.stream()
                .filter(y -> ((Number) y.get("year")).intValue() == year)
                .flatMap(y -> ((List<Map<String, Object>>) y.get("months")).stream())
                .filter(m -> ((Number) m.get("month")).intValue() == month)
                .map(m -> ((Number) m.get("trainingSummaryDuration")).intValue())
                .findFirst()
                .orElseThrow(() -> new AssertionError(
                        String.format("No entry for year %d month %d in summary", year, month)));
    }

    private static String capitalize(String s) {
        return s.isEmpty() ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
