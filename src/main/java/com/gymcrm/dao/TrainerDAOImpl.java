package com.gymcrm.dao;

import com.gymcrm.model.Trainer;
import com.gymcrm.storage.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of TrainerDAO for managing Trainer entities.
 * Uses StorageService for data persistence.
 */
@Repository
public class TrainerDAOImpl implements TrainerDAO {
    
    private static final Logger logger = LoggerFactory.getLogger(TrainerDAOImpl.class);
    
    private StorageService storageService;
    
    /**
     * Setter-based injection for StorageService
     * 
     * @param storageService the storage service
     */
    @Autowired
    public void setStorageService(StorageService storageService) {
        this.storageService = storageService;
    }
    
    @Override
    public Trainer create(Trainer trainer) {
        logger.debug("Creating trainer: {}", trainer);
        
        if (trainer.getId() == null) {
            trainer.setId(storageService.generateTrainerId());
        }
        
        storageService.getTrainerStorage().put(trainer.getId(), trainer);
        logger.debug("Trainer created with ID: {}", trainer.getId());
        
        return trainer;
    }
    
    @Override
    public Trainer update(Trainer trainer) {
        logger.debug("Updating trainer with ID: {}", trainer.getId());
        
        if (trainer.getId() == null) {
            logger.error("Cannot update trainer without ID");
            throw new IllegalArgumentException("Trainer ID cannot be null for update operation");
        }
        
        if (!storageService.getTrainerStorage().containsKey(trainer.getId())) {
            logger.error("Trainer not found with ID: {}", trainer.getId());
            throw new IllegalArgumentException("Trainer not found with ID: " + trainer.getId());
        }
        
        storageService.getTrainerStorage().put(trainer.getId(), trainer);
        logger.debug("Trainer updated: {}", trainer.getId());
        
        return trainer;
    }
    
    @Override
    public Optional<Trainer> findById(Long id) {
        logger.debug("Finding trainer by ID: {}", id);
        
        if (id == null) {
            logger.debug("Cannot find trainer with null ID");
            return Optional.empty();
        }
        
        Trainer trainer = storageService.getTrainerStorage().get(id);
        return Optional.ofNullable(trainer);
    }
    
    @Override
    public List<Trainer> findAll() {
        logger.debug("Finding all trainers");
        
        List<Trainer> trainers = new ArrayList<>(storageService.getTrainerStorage().values());
        logger.debug("Found {} trainers", trainers.size());
        
        return trainers;
    }
    
    @Override
    public Optional<Trainer> findByUsername(String username) {
        logger.debug("Finding trainer by username: {}", username);
        
        if (username == null || username.trim().isEmpty()) {
            logger.debug("Cannot find trainer with null or empty username");
            return Optional.empty();
        }
        
        Optional<Trainer> trainer = storageService.getTrainerStorage().values().stream()
                .filter(t -> username.equals(t.getUsername()))
                .findFirst();
        
        if (trainer.isPresent()) {
            logger.debug("Found trainer with username: {}", username);
        } else {
            logger.debug("No trainer found with username: {}", username);
        }
        
        return trainer;
    }
}
