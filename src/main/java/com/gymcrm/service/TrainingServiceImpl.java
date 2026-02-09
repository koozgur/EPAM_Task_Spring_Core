package com.gymcrm.service;

import com.gymcrm.dao.TraineeDAO;
import com.gymcrm.dao.TrainerDAO;
import com.gymcrm.dao.TrainingDAO;
import com.gymcrm.dao.TrainingTypeDAO;
import com.gymcrm.model.Trainee;
import com.gymcrm.model.Trainer;
import com.gymcrm.model.Training;
import com.gymcrm.model.TrainingType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TrainingServiceImpl implements TrainingService {

    private static final Logger logger = LoggerFactory.getLogger(TrainingServiceImpl.class);

    private TrainingDAO trainingDAO;
    private TraineeDAO traineeDAO;
    private TrainerDAO trainerDAO;
    private TrainingTypeDAO trainingTypeDAO;

    @Autowired
    public void setTrainingDAO(TrainingDAO trainingDAO) {
        this.trainingDAO = trainingDAO;
    }

    @Autowired
    public void setTraineeDAO(TraineeDAO traineeDAO) {
        this.traineeDAO = traineeDAO;
    }

    @Autowired
    public void setTrainerDAO(TrainerDAO trainerDAO) {
        this.trainerDAO = trainerDAO;
    }

    @Autowired
    public void setTrainingTypeDAO(TrainingTypeDAO trainingTypeDAO) {
        this.trainingTypeDAO = trainingTypeDAO;
    }

    @Override
        @Transactional
    public Training createTraining(Training training) {
        validateRequiredFields(training);

        String traineeUsername = training.getTrainee().getUser().getUsername();
        String trainerUsername = training.getTrainer().getUser().getUsername();

        Trainee trainee = traineeDAO.findByUsername(traineeUsername)
            .orElseThrow(() -> new IllegalArgumentException(
                "Trainee not found with username: " + traineeUsername));

        Trainer trainer = trainerDAO.findByUsername(trainerUsername)
            .orElseThrow(() -> new IllegalArgumentException(
                "Trainer not found with username: " + trainerUsername));

        TrainingType trainingType = resolveTrainingType(training.getTrainingType());

        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingType(trainingType);

        Training created = trainingDAO.create(training);
        logger.info("Created training for trainee: {}, trainer: {}",
            traineeUsername, trainerUsername);
        return created;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Training> getTraining(Long id) {
        return trainingDAO.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Training> getAllTrainings() {
        return trainingDAO.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Training> getTrainingsByTrainee(Long traineeId) {
        return trainingDAO.findByTraineeId(traineeId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Training> getTrainingsByTrainer(Long trainerId) {
        return trainingDAO.findByTrainerId(trainerId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Training> getTraineeTrainingsByCriteria(
            String traineeUsername,
            LocalDate fromDate,
            LocalDate toDate,
            String trainerName,
            String trainingType) {

        if (traineeUsername == null) {
            throw new IllegalArgumentException("Trainee username must not be null");
        }

        return trainingDAO.findByTraineeUsernameAndCriteria(
                traineeUsername,
                fromDate,
                toDate,
                trainerName,
                trainingType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Training> getTrainerTrainingsByCriteria(
            String trainerUsername,
            LocalDate fromDate,
            LocalDate toDate,
            String traineeName) {

        if (trainerUsername == null) {
            throw new IllegalArgumentException("Trainer username must not be null");
        }

        return trainingDAO.findByTrainerUsernameAndCriteria(
                trainerUsername,
                fromDate,
                toDate,
                traineeName);
    }

    // ────────── Private helpers ──────────

    private void validateRequiredFields(Training training) {
        if (training == null) {
            throw new IllegalArgumentException("Training must not be null");
        }
        if (training.getTrainee() == null
                || training.getTrainee().getUser() == null
                || training.getTrainee().getUser().getUsername() == null) {
            throw new IllegalArgumentException("Trainee username is required");
        }
        if (training.getTrainer() == null
                || training.getTrainer().getUser() == null
                || training.getTrainer().getUser().getUsername() == null) {
            throw new IllegalArgumentException("Trainer username is required");
        }
        if (training.getTrainingType() == null) {
            throw new IllegalArgumentException("Training type is required");
        }
        if (training.getTrainingName() == null || training.getTrainingName().isBlank()) {
            throw new IllegalArgumentException("Training name is required");
        }
        if (training.getTrainingDate() == null) {
            throw new IllegalArgumentException("Training date is required");
        }
        if (training.getTrainingDuration() == null) {
            throw new IllegalArgumentException("Training duration is required");
        }
    }

    private TrainingType resolveTrainingType(TrainingType input) {
        if (input == null) {
            throw new IllegalArgumentException("Training type is required");
        }

        if (input.getId() != null) {
            return trainingTypeDAO.findById(input.getId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Training type not found with id: " + input.getId()));
        }

        String name = input.getTrainingTypeName();
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Training type name is required");
        }

        return trainingTypeDAO.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Training type not found with name: " + name));
    }

}
