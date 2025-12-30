package com.gymcrm.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Training entity representing a training session.
 * Contains training details and references to trainee and trainer.
 */
public class Training {
    
    private Long id;
    private Long traineeId;
    private Long trainerId;
    private String trainingName;
    private String trainingType;
    private LocalDate trainingDate;
    private Integer trainingDuration; // Duration in minutes

    public Training() {
    }

    public Training(Long id, Long traineeId, Long trainerId, String trainingName, 
                    String trainingType, LocalDate trainingDate, Integer trainingDuration) {
        this.id = id;
        this.traineeId = traineeId;
        this.trainerId = trainerId;
        this.trainingName = trainingName;
        this.trainingType = trainingType;
        this.trainingDate = trainingDate;
        this.trainingDuration = trainingDuration;
    }
    
    /**
     * Constructor without ID (for creating new trainings)
     */
    public Training(Long traineeId, Long trainerId, String trainingName, 
                    String trainingType, LocalDate trainingDate, Integer trainingDuration) {
        this.traineeId = traineeId;
        this.trainerId = trainerId;
        this.trainingName = trainingName;
        this.trainingType = trainingType;
        this.trainingDate = trainingDate;
        this.trainingDuration = trainingDuration;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    } //TODO: check the security requirement
    
    public Long getTraineeId() {
        return traineeId;
    }
    
    public void setTraineeId(Long traineeId) {
        this.traineeId = traineeId;
    }
    
    public Long getTrainerId() {
        return trainerId;
    }
    
    public void setTrainerId(Long trainerId) {
        this.trainerId = trainerId;
    }
    
    public String getTrainingName() {
        return trainingName;
    }
    
    public void setTrainingName(String trainingName) {
        this.trainingName = trainingName;
    }
    
    public String getTrainingType() {
        return trainingType;
    }
    
    public void setTrainingType(String trainingType) {
        this.trainingType = trainingType;
    }
    
    public LocalDate getTrainingDate() {
        return trainingDate;
    }
    
    public void setTrainingDate(LocalDate trainingDate) {
        this.trainingDate = trainingDate;
    }
    
    public Integer getTrainingDuration() {
        return trainingDuration;
    }
    
    public void setTrainingDuration(Integer trainingDuration) {
        this.trainingDuration = trainingDuration;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Training training = (Training) o;
        return Objects.equals(id, training.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Training{" +
                "id=" + id +
                ", traineeId=" + traineeId +
                ", trainerId=" + trainerId +
                ", trainingName='" + trainingName + '\'' +
                ", trainingType='" + trainingType + '\'' +
                ", trainingDate=" + trainingDate +
                ", trainingDuration=" + trainingDuration +
                '}';
    }
}
