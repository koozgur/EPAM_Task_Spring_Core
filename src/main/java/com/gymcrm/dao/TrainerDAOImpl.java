package com.gymcrm.dao;

import com.gymcrm.model.Trainer;
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
 * Implementation of TrainerDAO for managing Trainer entities.
 * Uses injected storage map bean for data persistence and StorageService for ID generation.
 */
@Repository
public class TrainerDAOImpl implements TrainerDAO {
    
    private static final Logger logger = LoggerFactory.getLogger(TrainerDAOImpl.class);
    
    private Map<Long, Trainer> trainerStorage;
    private StorageService storageService;
    
    /**
     * Setter-based injection for trainer storage map using @Resource
     * 
     * @param trainerStorage the trainer storage map bean
     */
    @Resource
    public void setTrainerStorage(Map<Long, Trainer> trainerStorage) {
        this.trainerStorage = trainerStorage;
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
    public Trainer create(Trainer trainer) {
        //ID fields will be generated internally
        trainer.setUserId(storageService.generateTrainerId());
        
        trainerStorage.put(trainer.getUserId(), trainer);
        logger.debug("Persisted Trainer entity id={}", trainer.getUserId());
        
        return trainer;
    }
    
    @Override
    public Trainer update(Trainer trainer) {
        if (!trainerStorage.containsKey(trainer.getUserId())) {
            throw new IllegalArgumentException("Trainer not found with ID: " + trainer.getUserId());
        }
        
        trainerStorage.put(trainer.getUserId(), trainer);
        logger.debug("Updated Trainer entity id={}", trainer.getUserId());
        return trainer;
    }
    
    @Override
    public Optional<Trainer> findById(Long id) {
        //Service defines what is a valid request, input control against business rules
        return Optional.ofNullable(trainerStorage.get(id));
    }
    
    @Override
    public List<Trainer> findAll() {
        return new ArrayList<>(trainerStorage.values());
    }
    
    @Override
    public Optional<Trainer> findByUsername(String username) {
        return trainerStorage.values().stream()
                .filter(t -> username.equals(t.getUsername()))
                .findFirst();
    }
}
