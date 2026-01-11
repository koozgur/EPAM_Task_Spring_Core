package com.gymcrm.dao;

import com.gymcrm.model.Trainee;
import com.gymcrm.storage.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of TraineeDAO for managing Trainee entities.
 * Uses StorageService for data persistence.
 */
@Repository
public class TraineeDAOImpl implements TraineeDAO {
    
    private static final Logger logger = LoggerFactory.getLogger(TraineeDAOImpl.class);
    
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
    public Trainee create(Trainee trainee) {
        logger.debug("Creating trainee: {}", trainee);

        //ID fields will be generated internally
        trainee.setUserId(storageService.generateTraineeId());
        
        storageService.getTraineeStorage().put(trainee.getUserId(), trainee);
        logger.debug("Trainee created with ID: {}", trainee.getUserId());
        
        return trainee;
    }
    
    @Override
    public Trainee update(Trainee trainee) {
        logger.debug("Updating trainee with ID: {}", trainee.getUserId());
        
        if (trainee.getUserId() == null) {
            logger.error("Cannot update trainee without ID");
            throw new IllegalArgumentException("Trainee ID cannot be null for update operation");
        }
        
        if (!storageService.getTraineeStorage().containsKey(trainee.getUserId())) {
            logger.error("Trainee not found with ID: {}", trainee.getUserId());
            throw new IllegalArgumentException("Trainee not found with ID: " + trainee.getUserId());
        }
        
        storageService.getTraineeStorage().put(trainee.getUserId(), trainee);
        logger.debug("Trainee updated: {}", trainee.getUserId());
        
        return trainee;
    }
    
    @Override
    public void delete(Long id) {
        logger.debug("Deleting trainee with ID: {}", id);
        
        if (id == null) {
            logger.error("Cannot delete trainee with null ID");
            throw new IllegalArgumentException("Trainee ID cannot be null");
        }
        
        Trainee removed = storageService.getTraineeStorage().remove(id);
        
        if (removed == null) {
            logger.warn("Trainee not found for deletion with ID: {}", id);
        } else {
            logger.debug("Trainee deleted with ID: {}", id);
        }
    }
    
    @Override
    public Optional<Trainee> findById(Long id) {
        logger.debug("Finding trainee by ID: {}", id);
        
        if (id == null) {
            logger.debug("Cannot find trainee with null ID");
            return Optional.empty();
        }
        
        Trainee trainee = storageService.getTraineeStorage().get(id);
        return Optional.ofNullable(trainee);
    }
    
    @Override
    public List<Trainee> findAll() {
        logger.debug("Finding all trainees");
        
        List<Trainee> trainees = new ArrayList<>(storageService.getTraineeStorage().values());
        logger.debug("Found {} trainees", trainees.size());
        
        return trainees;
    }
    
    @Override
    public Optional<Trainee> findByUsername(String username) {
        logger.debug("Finding trainee by username: {}", username);
        
        if (username == null || username.trim().isEmpty()) {
            logger.debug("Cannot find trainee with null or empty username");
            return Optional.empty();
        }
        
        Optional<Trainee> trainee = storageService.getTraineeStorage().values().stream()
                .filter(t -> username.equals(t.getUsername()))
                .findFirst();
        
        if (trainee.isPresent()) {
            logger.debug("Found trainee with username: {}", username);
        } else {
            logger.debug("No trainee found with username: {}", username);
        }
        
        return trainee;
    }
}
