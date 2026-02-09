package com.gymcrm.service;

import com.gymcrm.model.Training;

import java.util.List;
import java.util.Optional;


public interface TrainingService {

    Training createTraining(Training training);

    Optional<Training> getTraining(Long id);

    List<Training> getAllTrainings();

    List<Training> getTrainingsByTrainee(Long traineeId);

    List<Training> getTrainingsByTrainer(Long trainerId);

    /**
     * Get trainee trainings list by username and criteria.
     *
     * @param traineeUsername the trainee username
     * @param fromDate        filter start date (optional)
     * @param toDate          filter end date (optional)
     * @param trainerName     filter by trainer name (optional)
     * @param trainingType    filter by training type name (optional)
     * @return list of trainings matching criteria
     */
    List<Training> getTraineeTrainingsByCriteria(
            String traineeUsername,
            java.time.LocalDate fromDate,
            java.time.LocalDate toDate,
            String trainerName,
            String trainingType);

    /**
     * Get trainer trainings list by username and criteria.
     *
     * @param trainerUsername the trainer username
     * @param fromDate        filter start date (optional)
     * @param toDate          filter end date (optional)
     * @param traineeName     filter by trainee name (optional)
     * @return list of trainings matching criteria
     */
    List<Training> getTrainerTrainingsByCriteria(
            String trainerUsername,
            java.time.LocalDate fromDate,
            java.time.LocalDate toDate,
            String traineeName);
}
