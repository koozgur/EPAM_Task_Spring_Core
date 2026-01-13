package com.gymcrm.util;

import com.gymcrm.dao.TraineeDAO;
import com.gymcrm.dao.TrainerDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

/**
 * Utility class for generating user credentials (username and password).
 * Ensures username uniqueness across both Trainees and Trainers.
 */
@Component
public class CredentialsGenerator {

    private static final Logger logger = LoggerFactory.getLogger(CredentialsGenerator.class);
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int PASSWORD_LENGTH = 10;

    private TraineeDAO traineeDAO;
    private TrainerDAO trainerDAO;

    @Autowired
    public void setTraineeDAO(TraineeDAO traineeDAO) {
        this.traineeDAO = traineeDAO;
    }

    @Autowired
    public void setTrainerDAO(TrainerDAO trainerDAO) {
        this.trainerDAO = trainerDAO;
    }

    /**
     * Generates a unique username based on first and last name.
     * Format: firstName.lastName (with serial number suffix if duplicate exists)
     *
     * @param firstName the user's first name
     * @param lastName the user's last name
     * @return a unique username
     */
    public String generateUsername(String firstName, String lastName) {
        String baseUsername = firstName + "." + lastName;
        String username = baseUsername;
        int serialNumber = 1;

        while (isUsernameTaken(username)) {
            username = baseUsername + serialNumber;
            serialNumber++;
        }

        logger.debug("Generated username: {}", username);
        return username;
    }

    /**
     * Generates a random password of fixed length.
     *
     * @return a randomly generated password
     */
    public String generatePassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            password.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        logger.debug("Generated new password");
        return password.toString();
    }

    /**
     * Checks if a username is already taken by either a Trainee or Trainer.
     *
     * @param username the username to check
     * @return true if the username is already in use, false otherwise
     */
    private boolean isUsernameTaken(String username) {
        return traineeDAO.findByUsername(username).isPresent() 
            || trainerDAO.findByUsername(username).isPresent();
    }
}
