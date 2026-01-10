package com.gymcrm.service;

import com.gymcrm.dao.TraineeDAO;
import com.gymcrm.dao.TrainerDAO;
import com.gymcrm.model.Trainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class TrainerServiceImpl implements TrainerService {

    private static final Logger logger = LoggerFactory.getLogger(TrainerServiceImpl.class);
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int PASSWORD_LENGTH = 10;

    private TrainerDAO trainerDAO;
    private TraineeDAO traineeDAO;

    @Autowired
    public void setTrainerDAO(TrainerDAO trainerDAO) {
        this.trainerDAO = trainerDAO;
    }

    @Autowired
    public void setTraineeDAO(TraineeDAO traineeDAO) {
        this.traineeDAO = traineeDAO;
    }

    @Override
    public Trainer createTrainer(Trainer trainer) {
        logger.info("Creating new trainer: {} {}", trainer.getFirstName(), trainer.getLastName());
        
        String username = generateUsername(trainer.getFirstName(), trainer.getLastName());
        String password = generatePassword();
        
        trainer.setUsername(username);
        trainer.setPassword(password);
        
        Trainer createdTrainer = trainerDAO.create(trainer);
        logger.info("Trainer created successfully with ID: {} and Username: {}", createdTrainer.getUserId(), createdTrainer.getUsername());
        
        return createdTrainer;
    }

    @Override
    public Trainer updateTrainer(Trainer trainer) {
        logger.info("Updating trainer with ID: {}", trainer.getUserId());
        Trainer updatedTrainer = trainerDAO.update(trainer);
        logger.info("Trainer updated successfully");
        return updatedTrainer;
    }

    @Override
    public Optional<Trainer> getTrainer(Long id) {
        logger.debug("Fetching trainer with ID: {}", id);
        return trainerDAO.findById(id);
    }

    @Override
    public List<Trainer> getAllTrainers() {
        logger.debug("Fetching all trainers");
        return trainerDAO.findAll();
    }

    private String generateUsername(String firstName, String lastName) {
        String baseUsername = firstName + "." + lastName;
        String username = baseUsername;
        int serialNumber = 1;

        while (trainerDAO.findByUsername(username).isPresent() || traineeDAO.findByUsername(username).isPresent()) {
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
