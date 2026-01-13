package com.gymcrm.dao;

import com.gymcrm.model.Trainee;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for Trainee entity.
 * Provides CRUD operations for managing trainees in storage.
 */
public interface TraineeDAO {
    
    /**
     * Create a new trainee
     * 
     * @param trainee the trainee to create
     * @return the created trainee with generated ID
     */
    Trainee create(Trainee trainee);
    
    /**
     * Update an existing trainee
     * 
     * @param trainee the trainee to update
     * @return the updated trainee
     */
    Trainee update(Trainee trainee);
    
    /**
     * Delete a trainee by ID
     * 
     * @param id the trainee ID
     */
    void delete(Long id);
    
    /**
     * Find a trainee by ID
     * 
     * @param id the trainee ID
     * @return Optional containing the trainee if found
     */
    Optional<Trainee> findById(Long id);
    
    /**
     * Find all trainees
     * 
     * @return list of all trainees
     */
    List<Trainee> findAll();
    
    /**
     * Find a trainee by username
     * 
     * @param username the username to search for
     * @return Optional containing the trainee if found
     */
    Optional<Trainee> findByUsername(String username);
}
