package com.gymcrm.service;

import com.gymcrm.model.Training;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing Training entities.
 */
public interface TrainingService {
    
    /**
     * Create a new training.
     * 
     * @param training the training to create
     * @return the created training with generated ID
     */
    Training createTraining(Training training);
    
    /**
     * Get a training by ID.
     * 
     * @param id the training ID
     * @return Optional containing the training if found
     */
    Optional<Training> getTraining(Long id);
    
    /**
     * Get all trainings.
     * 
     * @return list of all trainings
     */
    List<Training> getAllTrainings();
    
    /**
     * Get trainings by trainee ID.
     * 
     * @param traineeId the trainee ID
     * @return list of trainings for the trainee
     */
    List<Training> getTrainingsByTrainee(Long traineeId);
    
    /**
     * Get trainings by trainer ID.
     * 
     * @param trainerId the trainer ID
     * @return list of trainings for the trainer
     */
    List<Training> getTrainingsByTrainer(Long trainerId);
}
