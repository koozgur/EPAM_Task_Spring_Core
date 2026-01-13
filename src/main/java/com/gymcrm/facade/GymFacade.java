package com.gymcrm.facade;

import com.gymcrm.model.Trainee;
import com.gymcrm.model.Trainer;
import com.gymcrm.model.Training;
import com.gymcrm.service.TraineeService;
import com.gymcrm.service.TrainerService;
import com.gymcrm.service.TrainingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Facade class providing a unified interface to the Gym CRM system.
 * This facade simplifies client interactions by providing a single entry point
 * to all trainee, trainer, and training operations. It delegates requests to
 * the appropriate underlying services while providing comprehensive logging.
 * Benefits:
 * - Simplified interface for clients
 * - Decouples clients from internal service structure
 * - Centralized logging and cross-cutting concerns
 * - Single point of entry for all gym-related operations
 */
@Component
public class GymFacade {
    //Logging should not create noise.
    //Good logging explains why something happened, not how the code executed.
    //To be more specific; if code makes a choice, log it. If code only forwards data, don't.
    private static final Logger logger = LoggerFactory.getLogger(GymFacade.class);
    
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;
    
    /**
     * Constructor-based dependency injection for all services.
     * 
     * @param traineeService the trainee service
     * @param trainerService the trainer service
     * @param trainingService the training service
     */
    @Autowired
    public GymFacade(TraineeService traineeService, 
                     TrainerService trainerService, 
                     TrainingService trainingService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
        logger.info("GymFacade initialized with all services");
    }
    
    // ==================== TRAINEE OPERATIONS ====================
    
    /**
     * Create a new trainee profile.
     * Generates username and password automatically.
     * 
     * @param trainee the trainee to create
     * @return the created trainee with generated credentials
     */
    public Trainee createTrainee(Trainee trainee) {
        return traineeService.createTrainee(trainee);
    }
    
    /**
     * Update an existing trainee profile.
     * 
     * @param trainee the trainee to update
     * @return the updated trainee
     */
    public Trainee updateTrainee(Trainee trainee) {
        return traineeService.updateTrainee(trainee);
    }
    
    /**
     * Delete a trainee profile by ID.
     * 
     * @param id the trainee ID to delete
     */
    public void deleteTrainee(Long id) {
        traineeService.deleteTrainee(id);
    }
    
    /**
     * Get a trainee profile by ID.
     * 
     * @param id the trainee ID
     * @return Optional containing the trainee if found
     */
    public Optional<Trainee> getTrainee(Long id) {
        return traineeService.getTrainee(id);
    }
    
    /**
     * Get all trainee profiles.
     * 
     * @return list of all trainees
     */
    public List<Trainee> getAllTrainees() {
        return traineeService.getAllTrainees();
    }
    
    // ==================== TRAINER OPERATIONS ====================
    
    /**
     * Create a new trainer profile.
     * Generates username and password automatically.
     * 
     * @param trainer the trainer to create
     * @return the created trainer with generated credentials
     */
    public Trainer createTrainer(Trainer trainer) {
        return trainerService.createTrainer(trainer);
    }
    
    /**
     * Update an existing trainer profile.
     * 
     * @param trainer the trainer to update
     * @return the updated trainer
     */
    public Trainer updateTrainer(Trainer trainer) {
        return trainerService.updateTrainer(trainer);
    }
    
    /**
     * Get a trainer profile by ID.
     * 
     * @param id the trainer ID
     * @return Optional containing the trainer if found
     */
    public Optional<Trainer> getTrainer(Long id) {
        return trainerService.getTrainer(id);
    }
    
    /**
     * Get all trainer profiles.
     * 
     * @return list of all trainers
     */
    public List<Trainer> getAllTrainers() {
        return trainerService.getAllTrainers();
    }
    
    // ==================== TRAINING OPERATIONS ====================
    
    /**
     * Create a new training session.
     * 
     * @param training the training to create
     * @return the created training with generated ID
     */
    public Training createTraining(Training training) {
        return trainingService.createTraining(training);
    }
    
    /**
     * Get a training by ID.
     * 
     * @param id the training ID
     * @return Optional containing the training if found
     */
    public Optional<Training> getTraining(Long id) {
        return trainingService.getTraining(id);
    }
    
    /**
     * Get all trainings.
     * 
     * @return list of all trainings
     */
    public List<Training> getAllTrainings() {
        return trainingService.getAllTrainings();
    }
    
    /**
     * Get all trainings for a specific trainee.
     * 
     * @param traineeId the trainee ID
     * @return list of trainings for the trainee
     */
    public List<Training> getTrainingsByTrainee(Long traineeId) {
        return trainingService.getTrainingsByTrainee(traineeId);
    }
    
    /**
     * Get all trainings for a specific trainer.
     * 
     * @param trainerId the trainer ID
     * @return list of trainings for the trainer
     */
    public List<Training> getTrainingsByTrainer(Long trainerId) {
        return trainingService.getTrainingsByTrainer(trainerId);
    }

    /**
     * Get a summary of the gym system.
     * 
     * @return a string summary containing counts of trainees, trainers, and trainings
     */
    public String getGymSummary() {
        int traineeCount = traineeService.getAllTrainees().size();
        int trainerCount = trainerService.getAllTrainers().size();
        int trainingCount = trainingService.getAllTrainings().size();

        logger.info(
                "Use case: gym summary generated (trainees={}, trainers={}, trainings={})",
                traineeCount, trainerCount, trainingCount
        );

        return String.format(
                "Gym CRM Summary: %d Trainees, %d Trainers, %d Training Sessions",
                traineeCount, trainerCount, trainingCount
        );
    }

}
