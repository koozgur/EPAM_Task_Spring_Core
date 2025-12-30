package com.gymcrm.service;

import com.gymcrm.dao.TraineeDAO;
import com.gymcrm.dao.TrainerDAO;
import com.gymcrm.dao.TrainingDAO;
import com.gymcrm.model.Training;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TrainingServiceImpl implements TrainingService {

    private static final Logger logger = LoggerFactory.getLogger(TrainingServiceImpl.class);

    private TrainingDAO trainingDAO;
    private TraineeDAO traineeDAO;
    private TrainerDAO trainerDAO;

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

    @Override
    public Training createTraining(Training training) {
        logger.info("Creating new training: {}", training.getTrainingName());
        
        if (traineeDAO.findById(training.getTraineeId()).isEmpty()) {
            logger.error("Trainee with ID {} not found", training.getTraineeId());
            throw new IllegalArgumentException("Trainee not found");
        }
        
        if (trainerDAO.findById(training.getTrainerId()).isEmpty()) {
            logger.error("Trainer with ID {} not found", training.getTrainerId());
            throw new IllegalArgumentException("Trainer not found");
        }
        
        Training createdTraining = trainingDAO.create(training);
        logger.info("Training created successfully with ID: {}", createdTraining.getId());
        
        return createdTraining;
    }

    @Override
    public Optional<Training> getTraining(Long id) {
        logger.debug("Fetching training with ID: {}", id);
        return trainingDAO.findById(id);
    }

    @Override
    public List<Training> getAllTrainings() {
        logger.debug("Fetching all trainings");
        return trainingDAO.findAll();
    }

    @Override
    public List<Training> getTrainingsByTrainee(Long traineeId) {
        logger.debug("Fetching trainings for trainee ID: {}", traineeId);
        return trainingDAO.findByTraineeId(traineeId);
    }

    @Override
    public List<Training> getTrainingsByTrainer(Long trainerId) {
        logger.debug("Fetching trainings for trainer ID: {}", trainerId);
        return trainingDAO.findByTrainerId(trainerId);
    }
}
