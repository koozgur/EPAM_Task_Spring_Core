package com.gymcrm.storage;

import com.gymcrm.model.Trainee;
import com.gymcrm.model.Trainer;
import com.gymcrm.model.Training;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * In-memory storage service for managing Trainee, Trainer, and Training entities.
 * Provides thread-safe storage using ConcurrentHashMap and initializes data from file on startup.
 */
@Component
public class StorageService {
    
    private static final Logger logger = LoggerFactory.getLogger(StorageService.class);
    
    // Storage maps for each entity type
    private final Map<Long, Trainee> traineeStorage = new ConcurrentHashMap<>();
    private final Map<Long, Trainer> trainerStorage = new ConcurrentHashMap<>();
    private final Map<Long, Training> trainingStorage = new ConcurrentHashMap<>();
    
    // ID generators for separate namespaces
    private final AtomicLong traineeIdGenerator = new AtomicLong(1);
    private final AtomicLong trainerIdGenerator = new AtomicLong(1);
    private final AtomicLong trainingIdGenerator = new AtomicLong(1);
    
    @Value("${storage.init.file.path:classpath:initial-data.txt}")
    private String initFilePath;
    
    /**
     * Initialize storage with data from file after bean construction
     */
    @PostConstruct
    public void initialize() {
        logger.info("Initializing storage service...");
        loadDataFromFile();
        logger.info("Storage initialization complete. Trainees: {}, Trainers: {}, Trainings: {}", 
                    traineeStorage.size(), trainerStorage.size(), trainingStorage.size());
    }
    
    /**
     * Load initial data from file
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
                            DataParser.parseAndAddTrainee(line, traineeStorage, traineeIdGenerator);
                        } else if ("TRAINERS".equals(currentSection)) {
                            DataParser.parseAndAddTrainer(line, trainerStorage, trainerIdGenerator);
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
    
    // Trainee Storage Operations
    
    public Map<Long, Trainee> getTraineeStorage() {
        return traineeStorage;
    }
    
    public Long generateTraineeId() {
        return traineeIdGenerator.getAndIncrement();
    }
    
    // Trainer Storage Operations
    
    public Map<Long, Trainer> getTrainerStorage() {
        return trainerStorage;
    }
    
    public Long generateTrainerId() {
        return trainerIdGenerator.getAndIncrement();
    }
    
    // Training Storage Operations
    
    public Map<Long, Training> getTrainingStorage() {
        return trainingStorage;
    }
    
    public Long generateTrainingId() {
        return trainingIdGenerator.getAndIncrement();
    }
}
