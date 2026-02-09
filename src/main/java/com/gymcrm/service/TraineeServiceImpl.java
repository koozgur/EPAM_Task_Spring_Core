package com.gymcrm.service;

import com.gymcrm.dao.TraineeDAO;
import com.gymcrm.dao.TrainerDAO;
import com.gymcrm.model.Trainee;
import com.gymcrm.model.Trainer;
import com.gymcrm.model.User;
import com.gymcrm.util.CredentialsGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TraineeServiceImpl implements TraineeService {

    private static final Logger logger = LoggerFactory.getLogger(TraineeServiceImpl.class);

    private TraineeDAO traineeDAO;
    private TrainerDAO trainerDAO;
    private CredentialsGenerator credentialsGenerator;

    @Autowired
    public void setTraineeDAO(TraineeDAO traineeDAO) {
        this.traineeDAO = traineeDAO;
    }

    @Autowired
    public void setTrainerDAO(TrainerDAO trainerDAO) {
        this.trainerDAO = trainerDAO;
    }

    @Autowired
    public void setCredentialsGenerator(CredentialsGenerator credentialsGenerator) {
        this.credentialsGenerator = credentialsGenerator;
    }

    @Override
    @Transactional
    public Trainee createTrainee(Trainee trainee) {
        validateRequiredFields(trainee);

        User user = trainee.getUser();
        String username = credentialsGenerator.generateUsername(user.getFirstName(), user.getLastName());
        String password = credentialsGenerator.generatePassword();
        user.setUsername(username);
        user.setPassword(password);
        user.setIsActive(true);

        Trainee created = traineeDAO.create(trainee);
        logger.info("Created trainee profile with username: {}", username);
        return created;
    }

    @Override
    @Transactional
    public Trainee updateTrainee(Trainee trainee) {
        validateRequiredFields(trainee);

        String username = trainee.getUser().getUsername();
        Trainee existing = traineeDAO.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Trainee not found with username: " + username));

        User existingUser = existing.getUser();
        existingUser.setFirstName(trainee.getUser().getFirstName());
        existingUser.setLastName(trainee.getUser().getLastName());
        existingUser.setIsActive(trainee.getUser().getIsActive());
        existing.setDateOfBirth(trainee.getDateOfBirth());
        existing.setAddress(trainee.getAddress());

        Trainee updated = traineeDAO.update(existing);
        logger.info("Updated trainee profile for username: {}", username);
        return updated;
    }

    @Override
    @Transactional
    public void deleteTraineeByUsername(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Username must not be null");
        }

        Trainee trainee = traineeDAO.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Trainee not found with username: " + username));

        traineeDAO.delete(trainee.getId());
        logger.info("Deleted trainee profile for username: {}", username);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Trainee> getTrainee(Long id) {
        return traineeDAO.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Trainee> getAllTrainees() {
        return traineeDAO.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean authenticate(String username, String password) {
        if (username == null || password == null) {
            logger.warn("Authentication attempt with null credentials");
            return false;
        }

        Optional<Trainee> traineeOpt = traineeDAO.findByUsername(username);
        if (traineeOpt.isEmpty()) {
            logger.warn("Authentication failed: trainee not found for username: {}", username);
            return false;
        }

        boolean matches = password.equals(traineeOpt.get().getUser().getPassword());
        if (!matches) {
            logger.warn("Authentication failed: password mismatch for username: {}", username);
        }
        return matches;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Trainee> getTraineeByUsername(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Username must not be null");
        }
        Optional<Trainee> result = traineeDAO.findByUsername(username);
        logger.info("Selected trainee by username: {}, found: {}", username, result.isPresent());
        return result;
    }

    @Override
    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        if (username == null || oldPassword == null || newPassword == null) {
            throw new IllegalArgumentException(
                    "Username, old password, and new password must not be null");
        }

        Trainee trainee = traineeDAO.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Trainee not found with username: " + username));

        if (!oldPassword.equals(trainee.getUser().getPassword())) {
            throw new IllegalArgumentException("Old password does not match");
        }

        trainee.getUser().setPassword(newPassword);
        traineeDAO.update(trainee);
        logger.info("Password changed for trainee: {}", username);
    }

    @Override
    @Transactional
    public void activateTrainee(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Username must not be null");
        }

        Trainee trainee = traineeDAO.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Trainee not found with username: " + username));

        if (trainee.getUser().getIsActive()) {
            throw new IllegalStateException("Trainee is already active: " + username);
        }

        trainee.getUser().setIsActive(true);
        traineeDAO.update(trainee);
        logger.info("Activated trainee: {}", username);
    }

    @Override
    @Transactional
    public void deactivateTrainee(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Username must not be null");
        }

        Trainee trainee = traineeDAO.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Trainee not found with username: " + username));

        if (!trainee.getUser().getIsActive()) {
            throw new IllegalStateException("Trainee is already inactive: " + username);
        }

        trainee.getUser().setIsActive(false);
        traineeDAO.update(trainee);
        logger.info("Deactivated trainee: {}", username);
    }

    @Override
    @Transactional
    public List<Trainer> updateTraineeTrainersList(String traineeUsername, List<String> trainerUsernames) {
        if (traineeUsername == null) {
            throw new IllegalArgumentException("Trainee username must not be null");
        }
        if (trainerUsernames == null) {
            throw new IllegalArgumentException("Trainer usernames list must not be null");
        }

        Trainee trainee = traineeDAO.findByUsername(traineeUsername)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Trainee not found with username: " + traineeUsername));

        List<Trainer> newTrainers = new ArrayList<>();
        for (String trainerUsername : trainerUsernames) {
            Trainer trainer = trainerDAO.findByUsername(trainerUsername)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Trainer not found with username: " + trainerUsername));
            newTrainers.add(trainer);
        }

        trainee.getTrainers().clear();
        trainee.getTrainers().addAll(newTrainers);
        traineeDAO.update(trainee);

        logger.info("Updated trainers list for trainee: {}, trainers count: {}",
                traineeUsername, newTrainers.size());
        return newTrainers;
    }

    /**
     * Validates that the Trainee and its User carry all required fields.
     */
    private void validateRequiredFields(Trainee trainee) {
        if (trainee == null) {
            throw new IllegalArgumentException("Trainee must not be null");
        }
        if (trainee.getUser() == null) {
            throw new IllegalArgumentException("Trainee must have a User");
        }
        User user = trainee.getUser();
        if (user.getFirstName() == null || user.getFirstName().isBlank()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (user.getLastName() == null || user.getLastName().isBlank()) {
            throw new IllegalArgumentException("Last name is required");
        }
    }
}
