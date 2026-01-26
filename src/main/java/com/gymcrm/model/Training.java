package com.gymcrm.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;


@Entity
@Table(name = "trainings")
public class Training {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "trainee_id", nullable = false)
    private Trainee trainee;

    @ManyToOne
    @JoinColumn(name = "trainer_id", nullable = false)
    private Trainer trainer;

    @Column(name = "training_name", nullable = false, length = 100)
    private String trainingName;

    @ManyToOne
    @JoinColumn(name = "training_type_id", nullable = false)
    private TrainingType trainingType;

    @Column(name = "training_date", nullable = false)
    private LocalDate trainingDate;

    @Column(name = "training_duration", nullable = false)
    private Integer trainingDuration; // Duration in minutes

    public Training() {
    }

    public Training(Long id, Trainee trainee, Trainer trainer, String trainingName, 
                    TrainingType trainingType, LocalDate trainingDate, Integer trainingDuration) {
        this.id = id;
        this.trainee = trainee;
        this.trainer = trainer;
        this.trainingName = trainingName;
        this.trainingType = trainingType;
        this.trainingDate = trainingDate;
        this.trainingDuration = trainingDuration;
    }
    
    /**
     * Constructor without ID (for creating new trainings)
     */
    public Training(Trainee trainee, Trainer trainer, String trainingName, 
                    TrainingType trainingType, LocalDate trainingDate, Integer trainingDuration) {
        this.trainee = trainee;
        this.trainer = trainer;
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
    }
    
    public Trainee getTrainee() {
        return trainee;
    }
    
    public void setTrainee(Trainee trainee) {
        this.trainee = trainee;
    }
    
    public Trainer getTrainer() {
        return trainer;
    }
    
    public void setTrainer(Trainer trainer) {
        this.trainer = trainer;
    }
    
    public String getTrainingName() {
        return trainingName;
    }
    
    public void setTrainingName(String trainingName) {
        this.trainingName = trainingName;
    }
    
    public TrainingType getTrainingType() {
        return trainingType;
    }
    
    public void setTrainingType(TrainingType trainingType) {
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
                ", trainee=" + (trainee != null && trainee.getUser() != null ? trainee.getUser().getUsername() : null) +
                ", trainer=" + (trainer != null && trainer.getUser() != null ? trainer.getUser().getUsername() : null) +
                ", trainingName='" + trainingName + '\'' +
                ", trainingType=" + (trainingType != null ? trainingType.getTrainingTypeName() : null) +
                ", trainingDate=" + trainingDate +
                ", trainingDuration=" + trainingDuration +
                '}';
    }
}
