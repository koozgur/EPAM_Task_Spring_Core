package com.gymcrm.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

/**
 * Outbound DTO sent by the main service to the trainer-workload-service
 * every time a training is added or deleted.
 *
 * @JsonFormat on trainingDate is required so Jackson serializes LocalDate as
 * "yyyy-MM-dd" string rather than a numeric array when Feign builds the request body.
 */
public class TrainerWorkloadRequest {

    private String trainerUsername;
    private String firstName;
    private String lastName;
    private Boolean isActive;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate trainingDate;

    private Integer trainingDuration;
    private ActionType actionType;

    public enum ActionType {
        ADD, DELETE
    }

    public String getTrainerUsername() { return trainerUsername; }
    public void setTrainerUsername(String trainerUsername) { this.trainerUsername = trainerUsername; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDate getTrainingDate() { return trainingDate; }
    public void setTrainingDate(LocalDate trainingDate) { this.trainingDate = trainingDate; }

    public Integer getTrainingDuration() { return trainingDuration; }
    public void setTrainingDuration(Integer trainingDuration) { this.trainingDuration = trainingDuration; }

    public ActionType getActionType() { return actionType; }
    public void setActionType(ActionType actionType) { this.actionType = actionType; }
}
