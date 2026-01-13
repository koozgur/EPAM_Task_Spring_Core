package com.gymcrm.storage;

import com.gymcrm.model.Trainee;
import com.gymcrm.model.Trainer;
import com.gymcrm.model.Training;
import com.gymcrm.util.CredentialsGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Helper class for parsing initial data from file and populating storage maps.
 * Handles CSV-style parsing for Trainee, Trainer, and Training entities.
 */
public class DataParser {
    
    private static final Logger logger = LoggerFactory.getLogger(DataParser.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    /**
     * Parse and add a trainee from CSV line
     * Format: firstName,lastName,dateOfBirth,address,isActive
     * 
     * @param line CSV line to parse
     * @param traineeStorage Storage map to add the trainee
     * @param idGenerator ID generator for trainee
     * @param credentialsGenerator Generator for username and password
     */
    public static void parseAndAddTrainee(String line, Map<Long, Trainee> traineeStorage, AtomicLong idGenerator, CredentialsGenerator credentialsGenerator) {
        String[] parts = line.split(",", -1);
        if (parts.length != 5) {
            logger.warn("Invalid trainee data format: {}", line);
            return;
        }
        
        Long id = idGenerator.getAndIncrement();
        String firstName = parts[0].trim();
        String lastName = parts[1].trim();
        LocalDate dateOfBirth = parts[2].trim().isEmpty() ? null : LocalDate.parse(parts[2].trim(), DATE_FORMATTER);
        String address = parts[3].trim();
        Boolean isActive = Boolean.parseBoolean(parts[4].trim());
        
        Trainee trainee = new Trainee();
        trainee.setUserId(id);
        trainee.setFirstName(firstName);
        trainee.setLastName(lastName);
        trainee.setDateOfBirth(dateOfBirth);
        trainee.setAddress(address);
        trainee.setIsActive(isActive);
        // Generate and set username and password
        trainee.setUsername(credentialsGenerator.generateUsername(firstName, lastName));
        trainee.setPassword(credentialsGenerator.generatePassword());
        
        traineeStorage.put(id, trainee);
        logger.debug("Added trainee: {} {}", firstName, lastName);
    }
    
    /**
     * Parse and add a trainer from CSV line
     * Format: firstName,lastName,specialization,isActive
     * 
     * @param line CSV line to parse
     * @param trainerStorage Storage map to add the trainer
     * @param idGenerator ID generator for trainer
     * @param credentialsGenerator Generator for username and password
     */
    public static void parseAndAddTrainer(String line, Map<Long, Trainer> trainerStorage, AtomicLong idGenerator, CredentialsGenerator credentialsGenerator) {
        String[] parts = line.split(",", -1);
        if (parts.length != 4) {
            logger.warn("Invalid trainer data format: {}", line);
            return;
        }
        
        Long id = idGenerator.getAndIncrement();
        String firstName = parts[0].trim();
        String lastName = parts[1].trim();
        String specialization = parts[2].trim();
        Boolean isActive = Boolean.parseBoolean(parts[3].trim());
        
        Trainer trainer = new Trainer();
        trainer.setUserId(id);
        trainer.setFirstName(firstName);
        trainer.setLastName(lastName);
        trainer.setSpecialization(specialization);
        trainer.setIsActive(isActive);
        // Generate and set username and password
        trainer.setUsername(credentialsGenerator.generateUsername(firstName, lastName));
        trainer.setPassword(credentialsGenerator.generatePassword());
        
        trainerStorage.put(id, trainer);
        logger.debug("Added trainer: {} {}", firstName, lastName);
    }
    
    /**
     * Parse and add a training from CSV line
     * Format: traineeId,trainerId,trainingName,trainingType,trainingDate,trainingDuration
     * 
     * @param line CSV line to parse
     * @param trainingStorage Storage map to add the training
     * @param idGenerator ID generator for training
     */
    public static void parseAndAddTraining(String line, Map<Long, Training> trainingStorage, AtomicLong idGenerator) {
        String[] parts = line.split(",", -1);
        if (parts.length != 6) {
            logger.warn("Invalid training data format: {}", line);
            return;
        }
        
        Long id = idGenerator.getAndIncrement();
        Long traineeId = Long.parseLong(parts[0].trim());
        Long trainerId = Long.parseLong(parts[1].trim());
        String trainingName = parts[2].trim();
        String trainingType = parts[3].trim();
        LocalDate trainingDate = LocalDate.parse(parts[4].trim(), DATE_FORMATTER);
        Integer trainingDuration = Integer.parseInt(parts[5].trim());
        
        Training training = new Training();
        training.setId(id);
        training.setTraineeId(traineeId);
        training.setTrainerId(trainerId);
        training.setTrainingName(trainingName);
        training.setTrainingType(trainingType);
        training.setTrainingDate(trainingDate);
        training.setTrainingDuration(trainingDuration);
        
        trainingStorage.put(id, training);
        logger.debug("Added training: {}", trainingName);
    }
}
