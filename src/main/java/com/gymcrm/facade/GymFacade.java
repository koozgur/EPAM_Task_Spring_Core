package com.gymcrm.facade;

import com.gymcrm.dao.TrainingTypeDAO;
import com.gymcrm.dto.request.AddTrainingRequest;
import com.gymcrm.dto.request.ChangePasswordRequest;
import com.gymcrm.dto.request.TraineeRegistrationRequest;
import com.gymcrm.dto.request.TrainerRegistrationRequest;
import com.gymcrm.dto.request.UpdateTraineeRequest;
import com.gymcrm.dto.request.UpdateTraineeTrainersRequest;
import com.gymcrm.dto.request.UpdateTrainerRequest;
import com.gymcrm.dto.response.RegistrationResponse;
import com.gymcrm.dto.response.TraineeProfileResponse;
import com.gymcrm.dto.response.TraineeTrainingResponse;
import com.gymcrm.dto.response.TrainerProfileResponse;
import com.gymcrm.dto.response.TrainerSummaryResponse;
import com.gymcrm.dto.response.TrainerTrainingResponse;
import com.gymcrm.dto.response.TrainingTypeResponse;
import com.gymcrm.dto.response.UpdateTraineeResponse;
import com.gymcrm.dto.response.UpdateTrainerResponse;
import com.gymcrm.exception.NotFoundException;
import com.gymcrm.mapper.TraineeMapper;
import com.gymcrm.mapper.TrainerMapper;
import com.gymcrm.mapper.TrainingMapper;
import com.gymcrm.mapper.TrainingTypeMapper;
import com.gymcrm.model.Trainee;
import com.gymcrm.model.Trainer;
import com.gymcrm.model.Training;
import com.gymcrm.model.TrainingType;
import com.gymcrm.model.User;
import com.gymcrm.security.JwtTokenProvider;
import com.gymcrm.service.TraineeService;
import com.gymcrm.service.TrainerService;
import com.gymcrm.service.TrainingService;
import com.gymcrm.service.UserService;
import com.gymcrm.service.WorkloadNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapping done in facade layer; controllers deal in DTOs only, entities mapped to DTos here
 */
@Component
public class GymFacade {
    private static final Logger logger = LoggerFactory.getLogger(GymFacade.class);

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;
    private final UserService userService;
    private final TrainingTypeDAO trainingTypeDAO;
    private final TraineeMapper traineeMapper;
    private final TrainerMapper trainerMapper;
    private final TrainingMapper trainingMapper;
    private final TrainingTypeMapper trainingTypeMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final WorkloadNotificationService workloadNotificationService;

    @Autowired
    public GymFacade(TraineeService traineeService,
                     TrainerService trainerService,
                     TrainingService trainingService,
                     UserService userService,
                     TrainingTypeDAO trainingTypeDAO,
                     TraineeMapper traineeMapper,
                     TrainerMapper trainerMapper,
                     TrainingMapper trainingMapper,
                     TrainingTypeMapper trainingTypeMapper,
                     JwtTokenProvider jwtTokenProvider,
                     WorkloadNotificationService workloadNotificationService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
        this.userService = userService;
        this.trainingTypeDAO = trainingTypeDAO;
        this.traineeMapper = traineeMapper;
        this.trainerMapper = trainerMapper;
        this.trainingMapper = trainingMapper;
        this.trainingTypeMapper = trainingTypeMapper;
        this.jwtTokenProvider = jwtTokenProvider;
        this.workloadNotificationService = workloadNotificationService;
    }

    @Transactional
    public RegistrationResponse registerTrainee(TraineeRegistrationRequest req) {
        User user = new User();
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());

        Trainee trainee = new Trainee(user, req.getDateOfBirth(), req.getAddress());

        Trainee created = traineeService.createTrainee(trainee);
        String token = jwtTokenProvider.generateToken(created.getUser().getUsername());
        return new RegistrationResponse(
                created.getUser().getUsername(),
                created.getUser().getRawPassword(),
                token);
    }

    @Transactional
    public RegistrationResponse registerTrainer(TrainerRegistrationRequest req) {
        TrainingType specialization = trainingTypeDAO.findById(req.getSpecializationId())
                .orElseThrow(() -> new NotFoundException(
                        "Training type not found: " + req.getSpecializationId()));

        User user = new User();
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());

        Trainer trainer = new Trainer(user, specialization);

        Trainer created = trainerService.createTrainer(trainer);
        String token = jwtTokenProvider.generateToken(created.getUser().getUsername());
        return new RegistrationResponse(
                created.getUser().getUsername(),
                created.getUser().getRawPassword(),
                token);
    }

    @Transactional
    public void changePassword(ChangePasswordRequest req) {
        userService.changePassword(req.getUsername(), req.getOldPassword(), req.getNewPassword());
    }

    @Transactional(readOnly = true)
    public TraineeProfileResponse getTraineeProfile(String username) {
        Trainee trainee = traineeService.getTraineeByUsername(username)
                .orElseThrow(() -> new NotFoundException("Trainee not found: " + username));
        return traineeMapper.toProfileResponse(trainee);
    }

    @Transactional
    public UpdateTraineeResponse updateTrainee(String username, UpdateTraineeRequest req) {
        User user = new User(req.getFirstName(), req.getLastName(), username, req.getIsActive());

        Trainee trainee = new Trainee();
        trainee.setUser(user);
        trainee.setDateOfBirth(req.getDateOfBirth());
        trainee.setAddress(req.getAddress());

        Trainee updated = traineeService.updateTrainee(trainee);
        return traineeMapper.toUpdateResponse(updated);
    }

    @Transactional
    public void deleteTrainee(String username) {
        // Trainings must be fetched before deletion since cascade removes them from DB
        Trainee trainee = traineeService.getTraineeByUsername(username)
                .orElseThrow(() -> new NotFoundException("Trainee not found: " + username));
        List<Training> trainingsToDelete = trainingService.getTrainingsByTrainee(trainee.getId());

        traineeService.deleteTraineeByUsername(username);

        trainingsToDelete.forEach(workloadNotificationService::notifyDelete);
    }

    @Transactional(readOnly = true)
    public TrainerProfileResponse getTrainerProfile(String username) {
        Trainer trainer = trainerService.getTrainerByUsername(username)
                .orElseThrow(() -> new NotFoundException("Trainer not found: " + username));
        return trainerMapper.toProfileResponse(trainer);
    }

    @Transactional
    public UpdateTrainerResponse updateTrainer(String username, UpdateTrainerRequest req) {
        User user = new User();
        user.setUsername(username);
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setIsActive(req.getIsActive());

        Trainer trainer = new Trainer();
        trainer.setUser(user);
        Trainer updated = trainerService.updateTrainer(trainer);
        return trainerMapper.toUpdateResponse(updated);
    }

    @Transactional(readOnly = true)
    public List<TrainerSummaryResponse> getUnassignedTrainers(String traineeUsername) {
        return trainerService.getUnassignedTrainersByTraineeUsername(traineeUsername)
                .stream()
                .map(trainerMapper::toSummary)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<TrainerSummaryResponse> updateTraineeTrainers(String username, UpdateTraineeTrainersRequest req) {
        List<Trainer> trainers = traineeService.updateTraineeTrainersList(
                username, req.getTrainerUsernames());
        return trainers.stream()
                .map(trainerMapper::toSummary)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TraineeTrainingResponse> getTraineeTrainings(
            String username, LocalDate from, LocalDate to,
            String trainerName, String trainingType) {
        return trainingService.getTraineeTrainingsByCriteria(
                        username, from, to, trainerName, trainingType)
                .stream()
                .map(trainingMapper::toTraineeResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TrainerTrainingResponse> getTrainerTrainings(
            String username, LocalDate from, LocalDate to, String traineeName) {
        return trainingService.getTrainerTrainingsByCriteria(username, from, to, traineeName)
                .stream()
                .map(trainingMapper::toTrainerResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void addTraining(AddTrainingRequest req) {
        Trainee trainee = traineeService.getTraineeByUsername(req.getTraineeUsername())
                .orElseThrow(() -> new NotFoundException(
                        "Trainee not found: " + req.getTraineeUsername()));
        Trainer trainer = trainerService.getTrainerByUsername(req.getTrainerUsername())
                .orElseThrow(() -> new NotFoundException(
                        "Trainer not found: " + req.getTrainerUsername()));

        Training training = new Training(
                trainee,
                trainer,
                req.getName(),
                trainer.getSpecialization(),  // training type comes from the trainer's specialization
                req.getDate(),
                req.getDuration());

        Training created = trainingService.createTraining(training);
        workloadNotificationService.notifyAdd(created);
    }

    @Transactional
    public void setTraineeActive(String username, Boolean isActive) {
        if (isActive) {
            traineeService.activateTrainee(username);
        } else {
            traineeService.deactivateTrainee(username);
        }
    }

    @Transactional
    public void setTrainerActive(String username, Boolean isActive) {
        if (isActive) {
            trainerService.activateTrainer(username);
        } else {
            trainerService.deactivateTrainer(username);
        }
    }

    @Transactional(readOnly = true)
    public List<TrainingTypeResponse> getAllTrainingTypes() {
        return trainingTypeDAO.findAll()
                .stream()
                .map(trainingTypeMapper::toResponse)
                .collect(Collectors.toList());
    }
}
