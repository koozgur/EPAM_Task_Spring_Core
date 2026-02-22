package com.gymcrm.dto.response;

public class TrainingTypeResponse {

    private Long id;
    private String trainingType;

    public TrainingTypeResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTrainingType() { return trainingType; }
    public void setTrainingType(String trainingType) { this.trainingType = trainingType; }
}
