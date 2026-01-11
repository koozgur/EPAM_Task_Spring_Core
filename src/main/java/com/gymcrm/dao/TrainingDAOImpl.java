package com.gymcrm.dao;

import com.gymcrm.model.Training;
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
import java.util.stream.Collectors;

/**
 * Implementation of TrainingDAO for managing Training entities.
 * Uses injected storage map bean for data persistence and StorageInitializer for ID generation.
 */
@Repository
public class TrainingDAOImpl implements TrainingDAO {
    
    private static final Logger logger = LoggerFactory.getLogger(TrainingDAOImpl.class);
    
    private Map<Long, Training> trainingStorage;
    private StorageService storageService;
    
    /**
     * Setter-based injection for training storage map
     * 
     * @param trainingStorage the training storage map bean
     */
    @Autowired
    @Qualifier("trainingStorage")
    public void setTrainingStorage(Map<Long, Training> trainingStorage) {
        this.trainingStorage = trainingStorage;
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
    public Training create(Training training) {
        logger.debug("Creating training: {}", training);

        //ID fields will be generated internally
        training.setId(storageService.generateTrainingId());
        
        trainingStorage.put(training.getId(), training);
        logger.debug("Training created with ID: {}", training.getId());
        
        return training;
    }
    
    @Override
    public Optional<Training> findById(Long id) {
        logger.debug("Finding training by ID: {}", id);
        
        if (id == null) {
            logger.debug("Cannot find training with null ID");
            return Optional.empty();
        }
        
        Training training = trainingStorage.get(id);
        return Optional.ofNullable(training);
    }
    
    @Override
    public List<Training> findAll() {
        logger.debug("Finding all trainings");
        
        List<Training> trainings = new ArrayList<>(trainingStorage.values());
        logger.debug("Found {} trainings", trainings.size());
        
        return trainings;
    }
    
    @Override
    public List<Training> findByTraineeId(Long traineeId) {
        logger.debug("Finding trainings by trainee ID: {}", traineeId);
        
        if (traineeId == null) {
            logger.debug("Cannot find trainings with null trainee ID");
            return new ArrayList<>();
        }
        
        List<Training> trainings = trainingStorage.values().stream()
                .filter(t -> traineeId.equals(t.getTraineeId()))
                .collect(Collectors.toList());
        
        logger.debug("Found {} trainings for trainee ID: {}", trainings.size(), traineeId);
        
        return trainings;
    }
    
    @Override
    public List<Training> findByTrainerId(Long trainerId) {
        logger.debug("Finding trainings by trainer ID: {}", trainerId);
        
        if (trainerId == null) {
            logger.debug("Cannot find trainings with null trainer ID");
            return new ArrayList<>();
        }

        
        List<Training> trainings = trainingStorage.values().stream()
                .filter(t -> trainerId.equals(t.getTrainerId()))
                .collect(Collectors.toList());
        
        logger.debug("Found {} trainings for trainer ID: {}", trainings.size(), trainerId);
        
        return trainings;
    }
}
