package com.gymcrm.service;

import com.gymcrm.model.Trainer;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing Trainer entities.
 */
public interface TrainerService {
    
    /**
     * Create a new trainer profile.
     * Generates username and password for the trainer.
     * 
     * @param trainer the trainer to create
     * @return the created trainer with generated ID, username, and password
     */
    Trainer createTrainer(Trainer trainer);
    
    /**
     * Update an existing trainer profile.
     * 
     * @param trainer the trainer to update
     * @return the updated trainer
     */
    Trainer updateTrainer(Trainer trainer);
    
    /**
     * Get a trainer profile by ID.
     * 
     * @param id the trainer ID
     * @return Optional containing the trainer if found
     */
    Optional<Trainer> getTrainer(Long id);
    
    /**
     * Get all trainer profiles.
     * 
     * @return list of all trainers
     */
    List<Trainer> getAllTrainers();
}
