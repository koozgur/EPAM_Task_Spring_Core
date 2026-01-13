package com.gymcrm.storage;

import com.gymcrm.model.Trainee;
import com.gymcrm.model.Trainer;
import com.gymcrm.model.Training;
import com.gymcrm.util.CredentialsGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Storage initializer component responsible for:
 * 1. Loading initial data from file into storage maps during application startup
 * 2. Providing ID generation methods for each entity type
 * 
 * Uses @PostConstruct for bean post-processing to initialize storage with prepared data.
 * File path is configured via property placeholder from external property file.
 */
@Component
public class StorageService {
    
    private static final Logger logger = LoggerFactory.getLogger(StorageService.class);
    
    // Storage maps injected from AppConfig beans
    private Map<Long, Trainee> traineeStorage;
    private Map<Long, Trainer> trainerStorage;
    private Map<Long, Training> trainingStorage;
    
    // ID generators, managed internally
    private final AtomicLong traineeIdGenerator = new AtomicLong(1);
    private final AtomicLong trainerIdGenerator = new AtomicLong(1);
    private final AtomicLong trainingIdGenerator = new AtomicLong(1);
    
    // Credentials generator for username and password
    private CredentialsGenerator credentialsGenerator;

    @Value("${storage.init.file.path:classpath:initial-data.txt}")
    private String initFilePath;
    
    /**
     * Setter-based injection for credentials generator
     * @param credentialsGenerator the credentials generator bean
     */
    @Resource
    public void setCredentialsGenerator(CredentialsGenerator credentialsGenerator) {
        this.credentialsGenerator = credentialsGenerator;
    }
    
    /**
     * Setter-based injection for trainee storage map using @Resource
     * @param traineeStorage the trainee storage map bean
     */
    @Resource
    public void setTraineeStorage(Map<Long, Trainee> traineeStorage) {
        this.traineeStorage = traineeStorage;
    }
    
    /**
     * Setter-based injection for trainer storage map using @Resource
     * @param trainerStorage the trainer storage map bean
     */
    @Resource
    public void setTrainerStorage(Map<Long, Trainer> trainerStorage) {
        this.trainerStorage = trainerStorage;
    }
    
    /**
     * Setter-based injection for training storage map using @Resource
     * @param trainingStorage the training storage map bean
     */
    @Resource
    public void setTrainingStorage(Map<Long, Training> trainingStorage) {
        this.trainingStorage = trainingStorage;
    }
    
    /**
     * Initialize storage with data from file after bean construction.
     * This method is called automatically by Spring after all dependencies are injected.
     */
    @PostConstruct
    public void initialize() {
        logger.info("Initializing storage with data from file: {}", initFilePath);
        loadDataFromFile();
        logger.info("Storage initialization complete. Trainees: {}, Trainers: {}, Trainings: {}", 
                    traineeStorage.size(), trainerStorage.size(), trainingStorage.size());
    }
    
    /**
     * Load initial data from the configured file.
     * Parses sections [TRAINEES], [TRAINERS], [TRAININGS] and populates respective storage maps.
     */
    private void loadDataFromFile() {
        String fileName = initFilePath.replace("classpath:", "");
        
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (is == null) {
                logger.warn("Initial data file not found: {}. Starting with empty storage.", fileName);
                return;
            }
            
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                String line;
                String currentSection = null;
                int lineNumber = 0;
                
                while ((line = reader.readLine()) != null) {
                    lineNumber++;
                    line = line.trim();
                    
                    // Skip empty lines and comments
                    if (line.isEmpty() || line.startsWith("#")) {
                        continue;
                    }
                    
                    // Check for section headers
                    if (line.startsWith("[") && line.endsWith("]")) {
                        currentSection = line.substring(1, line.length() - 1).toUpperCase();
                        logger.debug("Reading section: {}", currentSection);
                        continue;
                    }
                    
                    // Process data based on current section
                    try {
                        if ("TRAINEES".equals(currentSection)) {
                            DataParser.parseAndAddTrainee(line, traineeStorage, traineeIdGenerator, credentialsGenerator);
                        } else if ("TRAINERS".equals(currentSection)) {
                            DataParser.parseAndAddTrainer(line, trainerStorage, trainerIdGenerator, credentialsGenerator);
                        } else if ("TRAININGS".equals(currentSection)) {
                            DataParser.parseAndAddTraining(line, trainingStorage, trainingIdGenerator);
                        }
                    } catch (Exception e) {
                        logger.error("Error parsing line {}: {}. Error: {}", lineNumber, line, e.getMessage());
                    }
                }
                
                logger.info("Data loaded successfully from {}", fileName);
            }
        } catch (IOException e) {
            logger.error("Error reading initial data file: {}", e.getMessage(), e);
        }
    }
    
    // ==================== ID Generation Methods ====================
    
    /**
     * Generate a new unique ID for a Trainee entity
     * @return the next available trainee ID
     */
    public Long generateTraineeId() {
        return traineeIdGenerator.getAndIncrement();
    }
    
    /**
     * Generate a new unique ID for a Trainer entity
     * @return the next available trainer ID
     */
    public Long generateTrainerId() {
        return trainerIdGenerator.getAndIncrement();
    }
    
    /**
     * Generate a new unique ID for a Training entity
     * @return the next available training ID
     */
    public Long generateTrainingId() {
        return trainingIdGenerator.getAndIncrement();
    }
}
