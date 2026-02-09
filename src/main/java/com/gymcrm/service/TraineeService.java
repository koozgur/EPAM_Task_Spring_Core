package com.gymcrm.service;

import com.gymcrm.model.Trainee;
import com.gymcrm.model.Trainer;

import java.util.List;
import java.util.Optional;

public interface TraineeService {

    Trainee createTrainee(Trainee trainee);

    /**
     * Authenticate a trainee by username and password matching.
     * Must be called before any trainee operation except profile creation.
     *
     * @param username the trainee username
     * @param password the trainee password
     * @return true if credentials match, false otherwise
     */
    boolean authenticate(String username, String password);

    Optional<Trainee> getTraineeByUsername(String username);

    Trainee updateTrainee(Trainee trainee);

    void changePassword(String username, String oldPassword, String newPassword);

    void activateTrainee(String username);

    void deactivateTrainee(String username);

    void deleteTraineeByUsername(String username);

    /**
     * Update trainee's trainers list.
     * Replaces the current trainer assignments with the provided list.
     *
     * @param traineeUsername the trainee's username
     * @param trainerUsernames list of trainer usernames to assign
     * @return the updated list of assigned trainers
     */
    List<Trainer> updateTraineeTrainersList(String traineeUsername, List<String> trainerUsernames);

    Optional<Trainee> getTrainee(Long id);

    List<Trainee> getAllTrainees();
}
