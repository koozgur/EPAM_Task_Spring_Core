package com.gymcrm.workload.dto;

import java.util.List;

/**
 * Response for GET /api/workload/{trainerUsername}.
 * Nested structure: trainer → years → months → total training minutes.
 */
public class WorkloadSummaryResponse {

    private String trainerUsername;
    private String firstName;
    private String lastName;
    private Boolean trainerStatus;
    private List<YearSummary> years;

    public record YearSummary(Integer year, List<MonthSummary> months) {}

    public record MonthSummary(Integer month, Integer trainingSummaryDuration) {}

    public String getTrainerUsername() { return trainerUsername; }
    public void setTrainerUsername(String trainerUsername) { this.trainerUsername = trainerUsername; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public Boolean getTrainerStatus() { return trainerStatus; }
    public void setTrainerStatus(Boolean trainerStatus) { this.trainerStatus = trainerStatus; }

    public List<YearSummary> getYears() { return years; }
    public void setYears(List<YearSummary> years) { this.years = years; }
}
