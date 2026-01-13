package com.gymcrm.service;

import com.gymcrm.dao.TraineeDAO;
import com.gymcrm.model.Trainee;
import com.gymcrm.util.CredentialsGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TraineeServiceImpl implements TraineeService {

    private static final Logger logger = LoggerFactory.getLogger(TraineeServiceImpl.class);

    private TraineeDAO traineeDAO;
    private CredentialsGenerator credentialsGenerator;

    @Autowired
    public void setTraineeDAO(TraineeDAO traineeDAO) {
        this.traineeDAO = traineeDAO;
    }

    @Autowired
    public void setCredentialsGenerator(CredentialsGenerator credentialsGenerator) {
        this.credentialsGenerator = credentialsGenerator;
    }

    @Override
    public Trainee createTrainee(Trainee trainee) {
        // Input validation
        if (trainee == null) {
            logger.warn("Rejected trainee creation for null trainee");
            throw new IllegalArgumentException("Trainee cannot be null");
        }
        if (trainee.getFirstName() == null || trainee.getFirstName().trim().isEmpty()) {
            logger.warn("Rejected trainee creation for null/empty first name");
            throw new IllegalArgumentException("Trainee first name cannot be null or empty");
        }
        if (trainee.getLastName() == null || trainee.getLastName().trim().isEmpty()) {
            logger.warn("Rejected trainee creation for null/empty last name");
            throw new IllegalArgumentException("Trainee last name cannot be null or empty");
        }

        // Service owns the business logic of creation, DAO just stores the object
        String username = credentialsGenerator.generateUsername(trainee.getFirstName(), trainee.getLastName());
        String password = credentialsGenerator.generatePassword();
        
        trainee.setUsername(username);
        trainee.setPassword(password);
        
        Trainee createdTrainee = traineeDAO.create(trainee);
        logger.info("Trainee created id={} username={}", createdTrainee.getUserId(), createdTrainee.getUsername());

        return createdTrainee;
    }

    @Override
    public Trainee updateTrainee(Trainee trainee) {
        // Input validation
        if (trainee == null) {
            logger.warn("Rejected trainee update for null trainee");
            throw new IllegalArgumentException("Trainee cannot be null");
        }
        if (trainee.getUserId() == null) {
            logger.warn("Rejected trainee update for null trainee ID");
            throw new IllegalArgumentException("Trainee ID cannot be null for update");
        }

        try {
            Trainee updated = traineeDAO.update(trainee);
            logger.info("Trainee updated id={}", updated.getUserId());
            return updated;

        } catch (IllegalArgumentException e) {
            // Expected business failure (not found, invalid state)
            logger.warn("Update failed: trainee id={} not found", trainee.getUserId());
            throw e;
        }
    }

    @Override
    public void deleteTrainee(Long id) {
        if (id == null) {
            logger.warn("Rejected trainee deletion for null trainee ID");
            throw new IllegalArgumentException("Trainee ID cannot be null");
        }
        try{
            traineeDAO.delete(id);
            logger.info("Trainee deleted id={}", id);
        }
        catch (IllegalArgumentException e){
            logger.warn("Trainee not found for id={}", id);
            throw e;
        }
    }

    @Override
    public Optional<Trainee> getTrainee(Long id) {
        if (id == null) {
            logger.warn("Rejected trainee retrieval for null trainee ID");
            throw new IllegalArgumentException("Trainee ID cannot be null");
        }
        
        Optional<Trainee> trainee = traineeDAO.findById(id);
        if (trainee.isPresent()) {
            logger.debug("Trainee found id={}", id);
        } else {
            logger.debug("Trainee not found id={}", id);
        }
        return trainee;
    }

    @Override
    public List<Trainee> getAllTrainees() {
        List<Trainee> trainees = traineeDAO.findAll();
        logger.debug("Found {} trainees", trainees.size());
        return trainees;
    }
}
