package com.gymcrm.dao;

import com.gymcrm.model.Training;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for Training entity.
 * Provides CRUD operations for managing training sessions in storage.
 */
public interface TrainingDAO {
    
    /**
     * Create a new training
     * 
     * @param training the training to create
     * @return the created training with generated ID
     */
    Training create(Training training);
    
    /**
     * Find a training by ID
     * 
     * @param id the training ID
     * @return Optional containing the training if found
     */
    Optional<Training> findById(Long id);
    
    /**
     * Find all trainings
     * 
     * @return list of all trainings
     */
    List<Training> findAll();
    
    /**
     * Find all trainings for a specific trainee
     * 
     * @param traineeId the trainee ID
     * @return list of trainings for the trainee
     */
    List<Training> findByTraineeId(Long traineeId);
    
    /**
     * Find all trainings for a specific trainer
     * 
     * @param trainerId the trainer ID
     * @return list of trainings for the trainer
     */
    List<Training> findByTrainerId(Long trainerId);
}
