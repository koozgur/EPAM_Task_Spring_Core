package com.gymcrm.dao;

import com.gymcrm.model.Training;
import com.gymcrm.storage.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of TrainingDAO for managing Training entities.
 * Uses StorageService for data persistence.
 */
@Repository
public class TrainingDAOImpl implements TrainingDAO {
    
    private static final Logger logger = LoggerFactory.getLogger(TrainingDAOImpl.class);
    
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
    public Training create(Training training) {
        logger.debug("Creating training: {}", training);
        
        if (training.getId() == null) {
            training.setId(storageService.generateTrainingId());
        }
        
        storageService.getTrainingStorage().put(training.getId(), training);
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
        
        Training training = storageService.getTrainingStorage().get(id);
        return Optional.ofNullable(training);
    }
    
    @Override
    public List<Training> findAll() {
        logger.debug("Finding all trainings");
        
        List<Training> trainings = new ArrayList<>(storageService.getTrainingStorage().values());
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
        
        List<Training> trainings = storageService.getTrainingStorage().values().stream()
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

        
        List<Training> trainings = storageService.getTrainingStorage().values().stream()
                .filter(t -> trainerId.equals(t.getTrainerId()))
                .collect(Collectors.toList());
        
        logger.debug("Found {} trainings for trainer ID: {}", trainings.size(), trainerId);
        
        return trainings;
    }
}
