package com.gymcrm.dto.response;

import java.util.List;

public class UpdateTrainerResponse {

    private String username;
    private String firstName;
    private String lastName;
    private TrainingTypeResponse specialization;
    private Boolean isActive;
    private List<TraineeSummaryResponse> trainees;

    public UpdateTrainerResponse() {}

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public TrainingTypeResponse getSpecialization() { return specialization; }
    public void setSpecialization(TrainingTypeResponse specialization) { this.specialization = specialization; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public List<TraineeSummaryResponse> getTrainees() { return trainees; }
    public void setTrainees(List<TraineeSummaryResponse> trainees) { this.trainees = trainees; }
}
