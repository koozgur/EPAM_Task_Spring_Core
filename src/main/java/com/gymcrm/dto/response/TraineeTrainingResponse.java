package com.gymcrm.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

//TODO: list item for 12th task spec
public class TraineeTrainingResponse {

    private String trainingName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate trainingDate;

    private TrainingTypeResponse trainingType;
    private Integer trainingDuration;
    private String trainerName;

    public TraineeTrainingResponse() {}

    public String getTrainingName() { return trainingName; }
    public void setTrainingName(String trainingName) { this.trainingName = trainingName; }

    public LocalDate getTrainingDate() { return trainingDate; }
    public void setTrainingDate(LocalDate trainingDate) { this.trainingDate = trainingDate; }

    public TrainingTypeResponse getTrainingType() { return trainingType; }
    public void setTrainingType(TrainingTypeResponse trainingType) { this.trainingType = trainingType; }

    public Integer getTrainingDuration() { return trainingDuration; }
    public void setTrainingDuration(Integer trainingDuration) { this.trainingDuration = trainingDuration; }

    public String getTrainerName() { return trainerName; }
    public void setTrainerName(String trainerName) { this.trainerName = trainerName; }
}
