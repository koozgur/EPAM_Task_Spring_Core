package com.gymcrm.service;

import com.gymcrm.model.Trainee;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing Trainee entities.
 */
public interface TraineeService {
    
    /**
     * Create a new trainee profile.
     * Generates username and password for the trainee.
     * 
     * @param trainee the trainee to create
     * @return the created trainee with generated ID, username, and password
     */
    Trainee createTrainee(Trainee trainee);
    
    /**
     * Update an existing trainee profile.
     * 
     * @param trainee the trainee to update
     * @return the updated trainee
     */
    Trainee updateTrainee(Trainee trainee);
    
    /**
     * Delete a trainee profile by ID.
     * 
     * @param id the trainee ID
     */
    void deleteTrainee(Long id);
    
    /**
     * Get a trainee profile by ID.
     * 
     * @param id the trainee ID
     * @return Optional containing the trainee if found
     */
    Optional<Trainee> getTrainee(Long id);
    
    /**
     * Get all trainee profiles.
     * 
     * @return list of all trainees
     */
    List<Trainee> getAllTrainees();
}
