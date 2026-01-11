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
        logger.info("Creating new trainer: {} {}", trainer.getFirstName(), trainer.getLastName());
        
        String username = credentialsGenerator.generateUsername(trainer.getFirstName(), trainer.getLastName());
        String password = credentialsGenerator.generatePassword();
        
        trainer.setUsername(username);
        trainer.setPassword(password);
        
        Trainer createdTrainer = trainerDAO.create(trainer);
        logger.info("Trainer created successfully with ID: {} and Username: {}", createdTrainer.getUserId(), createdTrainer.getUsername());
        
        return createdTrainer;
    }

    @Override
    public Trainer updateTrainer(Trainer trainer) {
        logger.info("Updating trainer with ID: {}", trainer.getUserId());
        Trainer updatedTrainer = trainerDAO.update(trainer);
        logger.info("Trainer updated successfully");
        return updatedTrainer;
    }

    @Override
    public Optional<Trainer> getTrainer(Long id) {
        logger.debug("Fetching trainer with ID: {}", id);
        return trainerDAO.findById(id);
    }

    @Override
    public List<Trainer> getAllTrainers() {
        logger.debug("Fetching all trainers");
        return trainerDAO.findAll();
    }
}
