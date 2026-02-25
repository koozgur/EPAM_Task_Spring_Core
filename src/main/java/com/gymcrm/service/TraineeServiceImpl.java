package com.gymcrm.service;

import com.gymcrm.dao.TraineeDAO;
import com.gymcrm.dao.TrainerDAO;
import com.gymcrm.exception.AuthenticationException;
import com.gymcrm.exception.NotFoundException;
import com.gymcrm.exception.StateConflictException;
import com.gymcrm.exception.ValidationException;
import com.gymcrm.model.Trainee;
import com.gymcrm.model.Trainer;
import com.gymcrm.model.User;
import com.gymcrm.util.CredentialsGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private UserService userService;

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

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Trainee createTrainee(Trainee trainee) {
        validateRequiredFields(trainee);

        User user = trainee.getUser();
        String username = credentialsGenerator.generateUsername(user.getFirstName(), user.getLastName());
        String rawPassword = credentialsGenerator.generatePassword();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRawPassword(rawPassword);  // transient — returned to caller for RegistrationResponse
        user.setIsActive(true);

        return traineeDAO.create(trainee);
    }

    @Override
    @Transactional
    public Trainee updateTrainee(Trainee trainee) {
        validateRequiredFields(trainee);

        String username = trainee.getUser().getUsername();
        Trainee existing = traineeDAO.findByUsername(username)
            .orElseThrow(() -> new NotFoundException(
                "Trainee not found with username: " + username));

        User existingUser = existing.getUser();
        existingUser.setFirstName(trainee.getUser().getFirstName());
        existingUser.setLastName(trainee.getUser().getLastName());
        existingUser.setIsActive(trainee.getUser().getIsActive());
        existing.setDateOfBirth(trainee.getDateOfBirth());
        existing.setAddress(trainee.getAddress());

        return traineeDAO.update(existing);
    }

    @Override
    @Transactional
    public void deleteTraineeByUsername(String username) {
        if (username == null) {
            throw new ValidationException("Username must not be null");
        }

        Trainee trainee = traineeDAO.findByUsername(username)
            .orElseThrow(() -> new NotFoundException(
                "Trainee not found with username: " + username));

        traineeDAO.delete(trainee.getId());
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
        return userService.authenticate(username, password);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Trainee> getTraineeByUsername(String username) {
        if (username == null) {
            throw new ValidationException("Username must not be null");
        }
        return traineeDAO.findByUsername(username);
    }

    @Override
    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        if (username == null || oldPassword == null || newPassword == null) {
            throw new ValidationException(
                "Username, old password, and new password must not be null");
        }

        Trainee trainee = traineeDAO.findByUsername(username)
            .orElseThrow(() -> new NotFoundException(
                "Trainee not found with username: " + username));

        if (!passwordEncoder.matches(oldPassword, trainee.getUser().getPassword())) {
            throw new AuthenticationException("Old password does not match");
        }

        trainee.getUser().setPassword(passwordEncoder.encode(newPassword));
        traineeDAO.update(trainee);
    }

    @Override
    @Transactional
    public void activateTrainee(String username) {
        if (username == null) {
            throw new ValidationException("Username must not be null");
        }

        Trainee trainee = traineeDAO.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(
                        "Trainee not found with username: " + username));

        if (trainee.getUser().getIsActive()) {
            throw new StateConflictException("Trainee is already active: " + username);
        }

        trainee.getUser().setIsActive(true);
        traineeDAO.update(trainee);
    }

    @Override
    @Transactional
    public void deactivateTrainee(String username) {
        if (username == null) {
            throw new ValidationException("Username must not be null");
        }

        Trainee trainee = traineeDAO.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(
                        "Trainee not found with username: " + username));

        if (!trainee.getUser().getIsActive()) {
            throw new StateConflictException("Trainee is already inactive: " + username);
        }

        trainee.getUser().setIsActive(false);
        traineeDAO.update(trainee);
    }

    @Override
    @Transactional
    public List<Trainer> updateTraineeTrainersList(String traineeUsername, List<String> trainerUsernames) {
        if (traineeUsername == null) {
            throw new ValidationException("Trainee username must not be null");
        }
        if (trainerUsernames == null) {
            throw new ValidationException("Trainer usernames list must not be null");
        }

        Trainee trainee = traineeDAO.findByUsername(traineeUsername)
                .orElseThrow(() -> new NotFoundException(
                        "Trainee not found with username: " + traineeUsername));

        List<Trainer> newTrainers = new ArrayList<>();
        for (String trainerUsername : trainerUsernames) {
                Trainer trainer = trainerDAO.findByUsername(trainerUsername)
                    .orElseThrow(() -> new NotFoundException(
                        "Trainer not found with username: " + trainerUsername));
            newTrainers.add(trainer);
        }

        trainee.getTrainers().clear();
        trainee.getTrainers().addAll(newTrainers);
        traineeDAO.update(trainee);
        return newTrainers;
    }

    /**
     * Validates that the Trainee and its User carry all required fields.
     */
    private void validateRequiredFields(Trainee trainee) {
        if (trainee == null) {
            throw new ValidationException("Trainee must not be null");
        }
        if (trainee.getUser() == null) {
            throw new ValidationException("Trainee must have a User");
        }
        User user = trainee.getUser();
        if (user.getFirstName() == null || user.getFirstName().isBlank()) {
            throw new ValidationException("First name is required");
        }
        if (user.getLastName() == null || user.getLastName().isBlank()) {
            throw new ValidationException("Last name is required");
        }
    }
}
