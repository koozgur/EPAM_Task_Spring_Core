package com.gymcrm.service;

import com.gymcrm.dao.TrainerDAO;
import com.gymcrm.exception.AuthenticationException;
import com.gymcrm.exception.NotFoundException;
import com.gymcrm.exception.StateConflictException;
import com.gymcrm.exception.ValidationException;
import com.gymcrm.model.Trainer;
import com.gymcrm.model.User;
import com.gymcrm.util.CredentialsGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TrainerServiceImpl implements TrainerService {

    private static final Logger logger = LoggerFactory.getLogger(TrainerServiceImpl.class);

    private TrainerDAO trainerDAO;
    private CredentialsGenerator credentialsGenerator;
    private UserService userService;

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
    public Trainer createTrainer(Trainer trainer) {
        validateRequiredFields(trainer);

        User user = trainer.getUser();
        String username = credentialsGenerator.generateUsername(user.getFirstName(), user.getLastName());
        String rawPassword = credentialsGenerator.generatePassword();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRawPassword(rawPassword);  // transient — returned to caller for RegistrationResponse
        user.setIsActive(true);

        return trainerDAO.create(trainer);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean authenticate(String username, String password) {
        return userService.authenticate(username, password);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Trainer> getTrainerByUsername(String username) {
        if (username == null) {
            throw new ValidationException("Username must not be null");
        }
        return trainerDAO.findByUsername(username);
    }

    @Override
    @Transactional
    public Trainer updateTrainer(Trainer trainer) {
        validateRequiredFields(trainer);

        String username = trainer.getUser().getUsername();
        Trainer existing = trainerDAO.findByUsernameWithTrainees(username)
            .orElseThrow(() -> new NotFoundException(
                "Trainer not found with username: " + username));

        // Update allowed fields on the managed entity
        User existingUser = existing.getUser();
        existingUser.setFirstName(trainer.getUser().getFirstName());
        existingUser.setLastName(trainer.getUser().getLastName());
        existingUser.setIsActive(trainer.getUser().getIsActive());
        // specialization is read-only per spec — intentionally not updated here
        logger.warn("Specialization (Training Type) is read-only, cannot update it");

        return trainerDAO.update(existing);
    }

    @Override
    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        if (username == null || oldPassword == null || newPassword == null) {
            throw new ValidationException(
                "Username, old password, and new password must not be null");
        }

        Trainer trainer = trainerDAO.findByUsername(username)
            .orElseThrow(() -> new NotFoundException(
                "Trainer not found with username: " + username));

        if (!passwordEncoder.matches(oldPassword, trainer.getUser().getPassword())) {
            throw new AuthenticationException("Old password does not match");
        }

        trainer.getUser().setPassword(passwordEncoder.encode(newPassword));
        trainerDAO.update(trainer);
    }

    @Override
    @Transactional
    public void activateTrainer(String username) {
        if (username == null) {
            throw new ValidationException("Username must not be null");
        }

        Trainer trainer = trainerDAO.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(
                        "Trainer not found with username: " + username));

        if (trainer.getUser().getIsActive()) {
            throw new StateConflictException("Trainer is already active: " + username);
        }

        trainer.getUser().setIsActive(true);
        trainerDAO.update(trainer);
    }

    @Override
    @Transactional
    public void deactivateTrainer(String username) {
        if (username == null) {
            throw new ValidationException("Username must not be null");
        }

        Trainer trainer = trainerDAO.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(
                        "Trainer not found with username: " + username));

        if (!(trainer.getUser().getIsActive())) {
            throw new StateConflictException("Trainer is already inactive: " + username);
        }

        trainer.getUser().setIsActive(false);
        trainerDAO.update(trainer);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Trainer> getTrainer(Long id) {
        return trainerDAO.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Trainer> getAllTrainers() {
        return trainerDAO.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Trainer> getUnassignedTrainersByTraineeUsername(String traineeUsername) {
        if (traineeUsername == null) {
            throw new ValidationException("Trainee username must not be null");
        }
        return trainerDAO.findUnassignedTrainersByTraineeUsername(traineeUsername);
    }

    /**
     * Validates that the Trainer and its User carry all required fields.
     */
    private void validateRequiredFields(Trainer trainer) {
        if (trainer == null) {
            throw new ValidationException("Trainer must not be null");
        }
        if (trainer.getUser() == null) {
            throw new ValidationException("Trainer must have a User");
        }
        User user = trainer.getUser();
        if (user.getFirstName() == null || user.getFirstName().isBlank()) {
            throw new ValidationException("First name is required");
        }
        if (user.getLastName() == null || user.getLastName().isBlank()) {
            throw new ValidationException("Last name is required");
        }
    }

}
