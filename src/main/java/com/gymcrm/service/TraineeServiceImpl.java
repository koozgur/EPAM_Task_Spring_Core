package com.gymcrm.service;

import com.gymcrm.dao.TraineeDAO;
import com.gymcrm.dao.TrainerDAO;
import com.gymcrm.model.Trainee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class TraineeServiceImpl implements TraineeService {

    private static final Logger logger = LoggerFactory.getLogger(TraineeServiceImpl.class);
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int PASSWORD_LENGTH = 10;

    private TraineeDAO traineeDAO;
    private TrainerDAO trainerDAO;

    @Autowired
    public void setTraineeDAO(TraineeDAO traineeDAO) {
        this.traineeDAO = traineeDAO;
    }

    @Autowired
    public void setTrainerDAO(com.gymcrm.dao.TrainerDAO trainerDAO) {
        this.trainerDAO = trainerDAO;
    }

    @Override
    public Trainee createTrainee(Trainee trainee) {
        logger.info("Creating new trainee: {} {}", trainee.getFirstName(), trainee.getLastName());
        
        String username = generateUsername(trainee.getFirstName(), trainee.getLastName());
        String password = generatePassword();
        
        trainee.setUsername(username);
        trainee.setPassword(password);
        
        Trainee createdTrainee = traineeDAO.create(trainee);
        logger.info("Trainee created successfully with ID: {} and Username: {}", createdTrainee.getId(), createdTrainee.getUsername());
        
        return createdTrainee;
    }

    @Override
    public Trainee updateTrainee(Trainee trainee) {
        logger.info("Updating trainee with ID: {}", trainee.getId());
        Trainee updatedTrainee = traineeDAO.update(trainee);
        logger.info("Trainee updated successfully");
        return updatedTrainee;
    }

    @Override
    public void deleteTrainee(Long id) {
        logger.info("Deleting trainee with ID: {}", id);
        traineeDAO.delete(id);
        logger.info("Trainee deleted successfully");
    }

    @Override
    public Optional<Trainee> getTrainee(Long id) {
        logger.debug("Fetching trainee with ID: {}", id);
        return traineeDAO.findById(id);
    }

    @Override
    public List<Trainee> getAllTrainees() {
        logger.debug("Fetching all trainees");
        return traineeDAO.findAll();
    }

    private String generateUsername(String firstName, String lastName) {
        String baseUsername = firstName + "." + lastName;
        String username = baseUsername;
        int serialNumber = 1;

        while (traineeDAO.findByUsername(username).isPresent() || trainerDAO.findByUsername(username).isPresent()) {
            username = baseUsername + serialNumber;
            serialNumber++;
        }
        
        return username;
    }

    private String generatePassword() {
        Random random = new Random();
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            password.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return password.toString();
    }
}
