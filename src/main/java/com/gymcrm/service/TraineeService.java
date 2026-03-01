package com.gymcrm.service;

import com.gymcrm.model.Trainee;
import com.gymcrm.model.Trainer;

import java.util.List;
import java.util.Optional;

public interface TraineeService {

    Trainee createTrainee(Trainee trainee);

    Optional<Trainee> getTraineeByUsername(String username);

    Trainee updateTrainee(Trainee trainee);

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
