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

    @Autowired
    public GymFacade(TraineeService traineeService, 
                     TrainerService trainerService, 
                     TrainingService trainingService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
        logger.info("GymFacade initialized with all services");
    }
    
    // ==================== AUTHENTICATION ====================

    /**
     * Authenticate a trainee by username and password matching.
     * Must be called before any trainee operation except profile creation.
     *
     * @param username the trainee's username
     * @param password the trainee's password
     * @return true if credentials match, false otherwise
     */
    public boolean authenticateTrainee(String username, String password) {
        return traineeService.authenticate(username, password);
    }

    /**
     * Authenticate a trainer by username and password matching.
     * Must be called before any trainer operation except profile creation.
     *
     * @param username the trainer's username
     * @param password the trainer's password
     * @return true if credentials match, false otherwise
     */
    public boolean authenticateTrainer(String username, String password) {
        return trainerService.authenticate(username, password);
    }


    public Trainee createTrainee(Trainee trainee) {
        return traineeService.createTrainee(trainee);
    }

    public Trainee updateTrainee(Trainee trainee) {
        return traineeService.updateTrainee(trainee);
    }

    public void deleteTraineeByUsername(String username) {
        traineeService.deleteTraineeByUsername(username);
    }

    public Optional<Trainee> getTrainee(Long id) {
        return traineeService.getTrainee(id);
    }

    public Optional<Trainee> getTraineeByUsername(String username) {
        return traineeService.getTraineeByUsername(username);
    }

    public List<Trainee> getAllTrainees() {
        return traineeService.getAllTrainees();
    }

    public Trainer createTrainer(Trainer trainer) {
        return trainerService.createTrainer(trainer);
    }

    public Trainer updateTrainer(Trainer trainer) {
        return trainerService.updateTrainer(trainer);
    }

    public Optional<Trainer> getTrainer(Long id) {
        return trainerService.getTrainer(id);
    }

    public Optional<Trainer> getTrainerByUsername(String username){
        return trainerService.getTrainerByUsername(username);
    }

    public List<Trainer> getAllTrainers() {
        return trainerService.getAllTrainers();
    }

    public List<Trainer> getUnassignedTrainersByTraineeUsername(String traineeUsername) {
        return trainerService.getUnassignedTrainersByTraineeUsername(traineeUsername);
    }

    /**
     * Update trainee's trainers list.
     * Replaces the current trainer assignments with the provided list.
     *
     * @param traineeUsername   the trainee's username
     * @param trainerUsernames list of trainer usernames to assign
     * @return the updated list of assigned trainers
     */
    public List<Trainer> updateTraineeTrainersList(String traineeUsername, List<String> trainerUsernames) {
        return traineeService.updateTraineeTrainersList(traineeUsername, trainerUsernames);
    }

    public Training createTraining(Training training) {
        return trainingService.createTraining(training);
    }

    public Optional<Training> getTraining(Long id) {
        return trainingService.getTraining(id);
    }

    public List<Training> getAllTrainings() {
        return trainingService.getAllTrainings();
    }

    public List<Training> getTrainingsByTrainee(Long traineeId) {
        return trainingService.getTrainingsByTrainee(traineeId);
    }

    public List<Training> getTrainingsByTrainer(Long trainerId) {
        return trainingService.getTrainingsByTrainer(trainerId);
    }

    public List<Training> getTraineeTrainingsByCriteria(
            String traineeUsername,
            java.time.LocalDate fromDate,
            java.time.LocalDate toDate,
            String trainerName,
            String trainingType) {
        return trainingService.getTraineeTrainingsByCriteria(
                traineeUsername, fromDate, toDate, trainerName, trainingType);
    }

    public List<Training> getTrainerTrainingsByCriteria(
            String trainerUsername,
            java.time.LocalDate fromDate,
            java.time.LocalDate toDate,
            String traineeName) {
        return trainingService.getTrainerTrainingsByCriteria(
                trainerUsername, fromDate, toDate, traineeName);
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
