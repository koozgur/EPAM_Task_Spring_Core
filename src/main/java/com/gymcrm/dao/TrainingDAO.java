package com.gymcrm.dao;

import com.gymcrm.model.Training;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TrainingDAO {

    Training create(Training training);

    Optional<Training> findById(Long id);

    List<Training> findAll();

    List<Training> findByTraineeId(Long traineeId);

    List<Training> findByTrainerId(Long trainerId);

    List<Training> findByTraineeUsernameAndCriteria(
            String traineeUsername,
            LocalDate fromDate,
            LocalDate toDate,
            String trainerName,
            String trainingTypeName);

    List<Training> findByTrainerUsernameAndCriteria(
            String trainerUsername,
            LocalDate fromDate,
            LocalDate toDate,
            String traineeName);
}
