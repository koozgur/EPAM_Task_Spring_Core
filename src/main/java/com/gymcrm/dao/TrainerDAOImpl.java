package com.gymcrm.dao;

import com.gymcrm.model.Trainer;
import com.gymcrm.storage.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementation of TrainerDAO for managing Trainer entities.
 * Uses injected storage map bean for data persistence and StorageInitializer for ID generation.
 */
@Repository
public class TrainerDAOImpl implements TrainerDAO {
    
    private static final Logger logger = LoggerFactory.getLogger(TrainerDAOImpl.class);
    
    private Map<Long, Trainer> trainerStorage;
    private StorageService storageService;
    
    /**
     * Setter-based injection for trainer storage map
     * 
     * @param trainerStorage the trainer storage map bean
     */
    @Autowired
    @Qualifier("trainerStorage")
    public void setTrainerStorage(Map<Long, Trainer> trainerStorage) {
        this.trainerStorage = trainerStorage;
    }
    
    /**
     * Setter-based injection for StorageInitializer (for ID generation)
     * 
     * @param storageService the storage initializer component
     */
    @Autowired
    public void setStorageInitializer(StorageService storageService) {
        this.storageService = storageService;
    }
    
    @Override
    public Trainer create(Trainer trainer) {
        logger.debug("Creating trainer: {}", trainer);

        //ID fields will be generated internally
        trainer.setUserId(storageService.generateTrainerId());
        
        trainerStorage.put(trainer.getUserId(), trainer);
        logger.debug("Trainer created with ID: {}", trainer.getUserId());
        
        return trainer;
    }
    
    @Override
    public Trainer update(Trainer trainer) {
        logger.debug("Updating trainer with ID: {}", trainer.getUserId());
        
        if (trainer.getUserId() == null) {
            logger.error("Cannot update trainer without ID");
            throw new IllegalArgumentException("Trainer ID cannot be null for update operation");
        }
        
        if (!trainerStorage.containsKey(trainer.getUserId())) {
            logger.error("Trainer not found with ID: {}", trainer.getUserId());
            throw new IllegalArgumentException("Trainer not found with ID: " + trainer.getUserId());
        }
        
        trainerStorage.put(trainer.getUserId(), trainer);
        logger.debug("Trainer updated: {}", trainer.getUserId());
        
        return trainer;
    }
    
    @Override
    public Optional<Trainer> findById(Long id) {
        logger.debug("Finding trainer by ID: {}", id);
        
        if (id == null) {
            logger.debug("Cannot find trainer with null ID");
            return Optional.empty();
        }
        
        Trainer trainer = trainerStorage.get(id);
        return Optional.ofNullable(trainer);
    }
    
    @Override
    public List<Trainer> findAll() {
        logger.debug("Finding all trainers");
        
        List<Trainer> trainers = new ArrayList<>(trainerStorage.values());
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
        
        Optional<Trainer> trainer = trainerStorage.values().stream()
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
