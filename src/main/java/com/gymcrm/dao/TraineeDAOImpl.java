package com.gymcrm.dao;

import com.gymcrm.model.Trainee;
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

/**
 * Implementation of TraineeDAO for managing Trainee entities.
 * Uses injected storage map bean for data persistence and StorageService for ID generation.
 */
@Repository
public class TraineeDAOImpl implements TraineeDAO {

    //DAO logs technical persistence facts, not business events.
    private static final Logger logger = LoggerFactory.getLogger(TraineeDAOImpl.class);
    
    private Map<Long, Trainee> traineeStorage;
    private StorageService storageService;
    
    /**
     * Setter-based injection for trainee storage map using @Resource
     * 
     * @param traineeStorage the trainee storage map bean
     */
    @Resource
    public void setTraineeStorage(Map<Long, Trainee> traineeStorage) {
        this.traineeStorage = traineeStorage;
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
    public Trainee create(Trainee trainee) {
        //ID fields will be generated internally
        trainee.setUserId(storageService.generateTraineeId());
        
        traineeStorage.put(trainee.getUserId(), trainee);
        logger.debug("Persisted Trainee entity id={}", trainee.getUserId());
        
        return trainee;
    }
    
    @Override
    public Trainee update(Trainee trainee) {
        if (!traineeStorage.containsKey(trainee.getUserId())) {
            throw new IllegalArgumentException("Trainee not found with ID: " + trainee.getUserId());
        }
        
        traineeStorage.put(trainee.getUserId(), trainee);
        logger.debug("Updated Trainee entity id={}", trainee.getUserId());
        return trainee;
    }
    
    @Override
    public void delete(Long id) {
        Trainee removed = traineeStorage.remove(id);
        if (removed == null) {
            throw new IllegalArgumentException("Trainee not found: " + id);
        }
        logger.debug("Deleted Trainee entity id={}", id);
    }

    @Override
    public Optional<Trainee> findById(Long id) {
        //Service defines what is a valid request, input control against business rules
        return Optional.ofNullable(traineeStorage.get(id));
    }


    @Override
    public List<Trainee> findAll() {
        return new ArrayList<>(traineeStorage.values());
    }
    
    @Override
    public Optional<Trainee> findByUsername(String username) {
        return traineeStorage.values().stream()
                .filter(t -> username.equals(t.getUsername()))
                .findFirst();
    }
}
