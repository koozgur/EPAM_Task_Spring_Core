package com.gymcrm.dto.response;

public class TrainerSummaryResponse {

    private String username;
    private String firstName;
    private String lastName;
    private TrainingTypeResponse specialization;

    public TrainerSummaryResponse() {}

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public TrainingTypeResponse getSpecialization() { return specialization; }
    public void setSpecialization(TrainingTypeResponse specialization) { this.specialization = specialization; }
}
