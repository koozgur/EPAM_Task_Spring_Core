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
        logger.info("Creating new trainee: {} {}", trainee.getFirstName(), trainee.getLastName());
        
        String username = credentialsGenerator.generateUsername(trainee.getFirstName(), trainee.getLastName());
        String password = credentialsGenerator.generatePassword();
        
        trainee.setUsername(username);
        trainee.setPassword(password);
        
        Trainee createdTrainee = traineeDAO.create(trainee);
        logger.info("Trainee created successfully with ID: {} and Username: {}", createdTrainee.getUserId(), createdTrainee.getUsername());
        
        return createdTrainee;
    }

    @Override
    public Trainee updateTrainee(Trainee trainee) {
        logger.info("Updating trainee with ID: {}", trainee.getUserId());
        Trainee updatedTrainee = traineeDAO.update(trainee);
        logger.info("Trainee updated successfully");
        return updatedTrainee;
    }

    @Override
    public void deleteTrainee(Long id) {
        logger.info("Deleting trainee with ID: {}", id);
        traineeDAO.delete(id);
        logger.info("Trainee deleted successfully");
    }

    @Override
    public Optional<Trainee> getTrainee(Long id) {
        logger.debug("Fetching trainee with ID: {}", id);
        return traineeDAO.findById(id);
    }

    @Override
    public List<Trainee> getAllTrainees() {
        logger.debug("Fetching all trainees");
        return traineeDAO.findAll();
    }
}
