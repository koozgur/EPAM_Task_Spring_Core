package com.gymcrm.dao;

import com.gymcrm.model.Trainer;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for Trainer entity.
 * Provides CRUD operations for managing trainers in storage.
 */
public interface TrainerDAO {
    
    /**
     * Create a new trainer
     * 
     * @param trainer the trainer to create
     * @return the created trainer with generated ID
     */
    Trainer create(Trainer trainer);
    
    /**
     * Update an existing trainer
     * 
     * @param trainer the trainer to update
     * @return the updated trainer
     */
    Trainer update(Trainer trainer);
    
    /**
     * Find a trainer by ID
     * 
     * @param id the trainer ID
     * @return Optional containing the trainer if found
     */
    Optional<Trainer> findById(Long id);
    
    /**
     * Find all trainers
     * 
     * @return list of all trainers
     */
    List<Trainer> findAll();
    
    /**
     * Find a trainer by username
     * 
     * @param username the username to search for
     * @return Optional containing the trainer if found
     */
    Optional<Trainer> findByUsername(String username);
}
