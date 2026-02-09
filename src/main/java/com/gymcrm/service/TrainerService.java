package com.gymcrm.service;

import com.gymcrm.model.Trainer;

import java.util.List;
import java.util.Optional;

public interface TrainerService {

    Trainer createTrainer(Trainer trainer);

    /**
     * Authenticate a trainer by username and password matching.
     * Must be called before any trainer operation except profile creation.
     *
     * @param username the trainer username
     * @param password the trainer password
     * @return true if credentials match, false otherwise
     */
    boolean authenticate(String username, String password);

    Optional<Trainer> getTrainerByUsername(String username);

    Trainer updateTrainer(Trainer trainer);

    void changePassword(String username, String oldPassword, String newPassword);

    void activateTrainer(String username);

    void deactivateTrainer(String username);

    List<Trainer> getUnassignedTrainersByTraineeUsername(String traineeUsername);

    Optional<Trainer> getTrainer(Long id);

    List<Trainer> getAllTrainers();
}
