package com.gymcrm.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

//TODO: response item for 13th task spec
public class TrainerTrainingResponse {

    private String trainingName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate trainingDate;

    private TrainingTypeResponse trainingType;
    private Integer trainingDuration;
    private String traineeName;

    public TrainerTrainingResponse() {}

    public String getTrainingName() { return trainingName; }
    public void setTrainingName(String trainingName) { this.trainingName = trainingName; }

    public LocalDate getTrainingDate() { return trainingDate; }
    public void setTrainingDate(LocalDate trainingDate) { this.trainingDate = trainingDate; }

    public TrainingTypeResponse getTrainingType() { return trainingType; }
    public void setTrainingType(TrainingTypeResponse trainingType) { this.trainingType = trainingType; }

    public Integer getTrainingDuration() { return trainingDuration; }
    public void setTrainingDuration(Integer trainingDuration) { this.trainingDuration = trainingDuration; }

    public String getTraineeName() { return traineeName; }
    public void setTraineeName(String traineeName) { this.traineeName = traineeName; }
}
