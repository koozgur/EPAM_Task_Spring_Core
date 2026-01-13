package com.gymcrm.dao;

import com.gymcrm.model.Training;
import com.gymcrm.storage.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of TrainingDAO for managing Training entities.
 * Uses injected storage map bean for data persistence and StorageService for ID generation.
 */
@Repository
public class TrainingDAOImpl implements TrainingDAO {
    
    private static final Logger logger = LoggerFactory.getLogger(TrainingDAOImpl.class);
    
    private Map<Long, Training> trainingStorage;
    private StorageService storageService;
    
    /**
     * Setter-based injection for training storage map using @Resource
     * 
     * @param trainingStorage the training storage map bean
     */
    @Resource
    public void setTrainingStorage(Map<Long, Training> trainingStorage) {
        this.trainingStorage = trainingStorage;
    }
    
    /**
     * Setter-based injection for StorageService (for ID generation)
     * 
     * @param storageService the storage service component
     */
    @Autowired
    public void setStorageService(StorageService storageService) {
        this.storageService = storageService;
    }
    
    @Override
    public Training create(Training training) {
        //ID fields will be generated internally
        training.setId(storageService.generateTrainingId());
        
        trainingStorage.put(training.getId(), training);
        logger.debug("Persisted Training entity id={}", training.getId());
        
        return training;
    }
    
    @Override
    public Optional<Training> findById(Long id) {
        //Service defines what is a valid request, input control against business rules
        return Optional.ofNullable(trainingStorage.get(id));
    }
    
    @Override
    public List<Training> findAll() {
        return new ArrayList<>(trainingStorage.values());
    }
    
    @Override
    public List<Training> findByTraineeId(Long traineeId) {
        //Service defines what is a valid request, input control against business rules
        return trainingStorage.values().stream()
                .filter(t -> traineeId.equals(t.getTraineeId()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Training> findByTrainerId(Long trainerId) {
        //Service defines what is a valid request, input control against business rules
        return trainingStorage.values().stream()
                .filter(t -> trainerId.equals(t.getTrainerId()))
                .collect(Collectors.toList());
    }
}
