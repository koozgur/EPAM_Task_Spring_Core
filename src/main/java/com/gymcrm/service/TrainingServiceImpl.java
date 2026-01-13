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
        if (training == null) {
            logger.warn("Rejected training creation for null training");
            throw new IllegalArgumentException("Training cannot be null");
        }
        if (training.getTraineeId() == null) {
            logger.warn("Rejected training creation for null trainee ID");
            throw new IllegalArgumentException("Trainee ID cannot be null");
        }
        if (training.getTrainerId() == null) {
            logger.warn("Rejected training creation for null trainer ID");
            throw new IllegalArgumentException("Trainer ID cannot be null");
        }
        if (training.getTrainingName() == null || training.getTrainingName().trim().isEmpty()) {
            logger.warn("Rejected training creation for null/empty training name");
            throw new IllegalArgumentException("Training name cannot be null or empty");
        }
        if (training.getTrainingDate() == null) {
            logger.warn("Rejected training creation for null training date");
            throw new IllegalArgumentException("Training date cannot be null");
        }
        if (training.getTrainingDuration() == null || training.getTrainingDuration() <= 0) {
            logger.warn("Rejected training creation for invalid training duration");
            throw new IllegalArgumentException("Training duration must be a positive number");
        }
        
        // Validate that trainee and trainer exist
        if (traineeDAO.findById(training.getTraineeId()).isEmpty()) {
            logger.warn("Rejected training creation - trainee not found id={}", training.getTraineeId());
            throw new IllegalArgumentException("Trainee not found with ID: " + training.getTraineeId());
        }
        
        if (trainerDAO.findById(training.getTrainerId()).isEmpty()) {
            logger.warn("Rejected training creation - trainer not found id={}", training.getTrainerId());
            throw new IllegalArgumentException("Trainer not found with ID: " + training.getTrainerId());
        }
        
        Training createdTraining = trainingDAO.create(training);
        logger.info("Training created id={} name={}", createdTraining.getId(), createdTraining.getTrainingName());
        
        return createdTraining;
    }

    @Override
    public Optional<Training> getTraining(Long id) {
        if (id == null) {
            logger.warn("Rejected training retrieval for null training ID");
            throw new IllegalArgumentException("Training ID cannot be null");
        }
        
        Optional<Training> training = trainingDAO.findById(id);
        if (training.isPresent()) {
            logger.debug("Training found id={}", id);
        } else {
            logger.debug("Training not found id={}", id);
        }
        return training;
    }

    @Override
    public List<Training> getAllTrainings() {
        List<Training> trainings = trainingDAO.findAll();
        logger.debug("Found {} trainings", trainings.size());
        return trainings;
    }

    @Override
    public List<Training> getTrainingsByTrainee(Long traineeId) {
        if (traineeId == null) {
            logger.warn("Rejected trainings retrieval for null trainee ID");
            throw new IllegalArgumentException("Trainee ID cannot be null");
        }
        
        List<Training> trainings = trainingDAO.findByTraineeId(traineeId);
        logger.debug("Found {} trainings for trainee id={}", trainings.size(), traineeId);
        return trainings;
    }

    @Override
    public List<Training> getTrainingsByTrainer(Long trainerId) {
        if (trainerId == null) {
            logger.warn("Rejected trainings retrieval for null trainer ID");
            throw new IllegalArgumentException("Trainer ID cannot be null");
        }
        
        List<Training> trainings = trainingDAO.findByTrainerId(trainerId);
        logger.debug("Found {} trainings for trainer id={}", trainings.size(), trainerId);
        return trainings;
    }
}
