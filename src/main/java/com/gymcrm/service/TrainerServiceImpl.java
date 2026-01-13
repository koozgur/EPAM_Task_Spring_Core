package com.gymcrm.service;

import com.gymcrm.dao.TrainerDAO;
import com.gymcrm.model.Trainer;
import com.gymcrm.util.CredentialsGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TrainerServiceImpl implements TrainerService {

    private static final Logger logger = LoggerFactory.getLogger(TrainerServiceImpl.class);

    private TrainerDAO trainerDAO;
    private CredentialsGenerator credentialsGenerator;

    @Autowired
    public void setTrainerDAO(TrainerDAO trainerDAO) {
        this.trainerDAO = trainerDAO;
    }

    @Autowired
    public void setCredentialsGenerator(CredentialsGenerator credentialsGenerator) {
        this.credentialsGenerator = credentialsGenerator;
    }

    @Override
    public Trainer createTrainer(Trainer trainer) {
        if (trainer == null) {
            logger.warn("Rejected trainer creation for null trainer");
            throw new IllegalArgumentException("Trainer cannot be null");
        }
        if (trainer.getFirstName() == null || trainer.getFirstName().trim().isEmpty()) {
            logger.warn("Rejected trainer creation for null/empty first name");
            throw new IllegalArgumentException("Trainer first name cannot be null or empty");
        }
        if (trainer.getLastName() == null || trainer.getLastName().trim().isEmpty()) {
            logger.warn("Rejected trainer creation for null/empty last name");
            throw new IllegalArgumentException("Trainer last name cannot be null or empty");
        }

        // Service owns the business logic of creation, DAO just stores the object
        String username = credentialsGenerator.generateUsername(trainer.getFirstName(), trainer.getLastName());
        String password = credentialsGenerator.generatePassword();
        
        trainer.setUsername(username);
        trainer.setPassword(password);
        
        Trainer createdTrainer = trainerDAO.create(trainer);
        logger.info("Trainer created id={} username={}", createdTrainer.getUserId(), createdTrainer.getUsername());
        
        return createdTrainer;
    }

    @Override
    public Trainer updateTrainer(Trainer trainer) {
        if (trainer == null) {
            logger.warn("Rejected trainer update for null trainer");
            throw new IllegalArgumentException("Trainer cannot be null");
        }
        if (trainer.getUserId() == null) {
            logger.warn("Rejected trainer update for null trainer ID");
            throw new IllegalArgumentException("Trainer ID cannot be null for update");
        }

        try {
            Trainer updated = trainerDAO.update(trainer);
            logger.info("Trainer updated id={}", updated.getUserId());
            return updated;

        } catch (IllegalArgumentException e) {
            // Expected business failure (not found, invalid state)
            logger.warn("Update failed: trainer id={} not found", trainer.getUserId());
            throw e;
        }
    }

    @Override
    public Optional<Trainer> getTrainer(Long id) {
        if (id == null) {
            logger.warn("Rejected trainer retrieval for null trainer ID");
            throw new IllegalArgumentException("Trainer ID cannot be null");
        }
        
        Optional<Trainer> trainer = trainerDAO.findById(id);
        if (trainer.isPresent()) {
            logger.debug("Trainer found id={}", id);
        } else {
            logger.debug("Trainer not found id={}", id);
        }
        return trainer;
    }

    @Override
    public List<Trainer> getAllTrainers() {
        List<Trainer> trainers = trainerDAO.findAll();
        logger.debug("Found {} trainers", trainers.size());
        return trainers;
    }
}
