package com.gymcrm.service;

import com.gymcrm.dao.TraineeDAO;
import com.gymcrm.dao.TrainerDAO;
import com.gymcrm.dao.TrainingDAO;
import com.gymcrm.dao.TrainingTypeDAO;
import com.gymcrm.exception.NotFoundException;
import com.gymcrm.exception.ValidationException;
import com.gymcrm.model.Trainee;
import com.gymcrm.model.Trainer;
import com.gymcrm.model.Training;
import com.gymcrm.model.TrainingType;
import com.gymcrm.model.User;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TrainingServiceImpl Tests")
class TrainingServiceImplTest {

    @Mock
    private TrainingDAO trainingDAO;

    @Mock
    private TraineeDAO traineeDAO;

    @Mock
    private TrainerDAO trainerDAO;

    @Mock
    private TrainingTypeDAO trainingTypeDAO;

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private Counter trainingCreatedCounter;

    @InjectMocks
    private TrainingServiceImpl trainingService;

    private Trainee testTrainee;
    private Trainer testTrainer;
    private TrainingType testTrainingType;

    @BeforeEach
    void setUp() {
        User traineeUser = new User();
        traineeUser.setUsername("trainee.user");
        traineeUser.setFirstName("John");
        traineeUser.setLastName("Doe");

        User trainerUser = new User();
        trainerUser.setUsername("trainer.user");
        trainerUser.setFirstName("Mike");
        trainerUser.setLastName("Coach");

        testTrainee = new Trainee();
        testTrainee.setId(10L);
        testTrainee.setUser(traineeUser);

        testTrainer = new Trainer();
        testTrainer.setId(20L);
        testTrainer.setUser(trainerUser);

        testTrainingType = new TrainingType("Cardio");
        testTrainingType.setId(5L);

        // Trainer must be in trainee's assigned trainers set or createTraining throws StateConflictException.
        testTrainee.setTrainers(new HashSet<>(Set.of(testTrainer)));

        // MeterRegistry is called during setMeterRegistry; stub it then re-inject
        // so trainingCreatedCounter is never null during tests.
        when(meterRegistry.counter(anyString())).thenReturn(trainingCreatedCounter);
        trainingService.setMeterRegistry(meterRegistry);
    }

    @Test
    @DisplayName("createTraining: resolves trainee, trainer, and type by id")
    void createTraining_success() {

        Training training = new Training();
        training.setTrainee(testTrainee);
        training.setTrainer(testTrainer);
        TrainingType inputType = new TrainingType();
        inputType.setId(5L);
        training.setTrainingType(inputType);
        training.setTrainingName("Cardio Session");
        training.setTrainingDate(LocalDate.of(2025, 1, 10));
        training.setTrainingDuration(60);

        when(traineeDAO.findByUsername("trainee.user")).thenReturn(Optional.of(testTrainee));
        when(trainerDAO.findByUsername("trainer.user")).thenReturn(Optional.of(testTrainer));
        when(trainingTypeDAO.findById(5L)).thenReturn(Optional.of(testTrainingType));
        when(trainingDAO.create(any(Training.class))).thenAnswer(inv -> {
            Training t = inv.getArgument(0);
            t.setId(1L);
            return t;
        });

        Training result = trainingService.createTraining(training);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(testTrainee, result.getTrainee());
        assertEquals(testTrainer, result.getTrainer());
        assertEquals(testTrainingType, result.getTrainingType());
        verify(trainingDAO).create(training);
    }

    @Test
    @DisplayName("createTraining: trainee not found")
    void createTraining_traineeNotFound() {
        Training training = new Training();
        training.setTrainee(testTrainee);
        training.setTrainer(testTrainer);
        training.setTrainingType(testTrainingType);
        training.setTrainingName("Cardio Session");
        training.setTrainingDate(LocalDate.of(2025, 1, 10));
        training.setTrainingDuration(60);

        when(traineeDAO.findByUsername("trainee.user")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> trainingService.createTraining(training));
        verify(trainerDAO, never()).findByUsername(any());
        verify(trainingDAO, never()).create(any());
    }

    @Test
    @DisplayName("createTraining: trainer not found")
    void createTraining_trainerNotFound() {
        Training training = new Training();
        training.setTrainee(testTrainee);
        training.setTrainer(testTrainer);
        training.setTrainingType(testTrainingType);
        training.setTrainingName("Cardio Session");
        training.setTrainingDate(LocalDate.of(2025, 1, 10));
        training.setTrainingDuration(60);

        when(traineeDAO.findByUsername("trainee.user")).thenReturn(Optional.of(testTrainee));
        when(trainerDAO.findByUsername("trainer.user")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> trainingService.createTraining(training));
        verify(trainingDAO, never()).create(any());
    }

    @Test
    @DisplayName("createTraining: training type not found by id")
    void createTraining_typeNotFoundById() {
        Training training = new Training();
        training.setTrainee(testTrainee);
        training.setTrainer(testTrainer);
        TrainingType inputType = new TrainingType();
        inputType.setId(5L);
        training.setTrainingType(inputType);
        training.setTrainingName("Cardio Session");
        training.setTrainingDate(LocalDate.of(2025, 1, 10));
        training.setTrainingDuration(60);

        when(traineeDAO.findByUsername("trainee.user")).thenReturn(Optional.of(testTrainee));
        when(trainerDAO.findByUsername("trainer.user")).thenReturn(Optional.of(testTrainer));
        when(trainingTypeDAO.findById(5L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> trainingService.createTraining(training));
        verify(trainingDAO, never()).create(any());
    }

    @Test
    @DisplayName("createTraining: missing training name rejected")
    void createTraining_missingName() {
        Training training = new Training();
        training.setTrainee(testTrainee);
        training.setTrainer(testTrainer);
        training.setTrainingType(testTrainingType);
        training.setTrainingName(" ");
        training.setTrainingDate(LocalDate.of(2025, 1, 10));
        training.setTrainingDuration(60);

        assertThrows(ValidationException.class,
                () -> trainingService.createTraining(training));
        verifyNoInteractions(trainingDAO);
    }

    @Test
    @DisplayName("getTraineeTrainingsByCriteria: null username rejected")
    void getTraineeTrainingsByCriteria_nullUsername() {
        assertThrows(ValidationException.class,
                () -> trainingService.getTraineeTrainingsByCriteria(
                        null, LocalDate.now(), LocalDate.now(), null, null));
    }

    @Test
    @DisplayName("getTrainerTrainingsByCriteria: delegates to DAO")
    void getTrainerTrainingsByCriteria_delegates() {
        List<Training> trainings = List.of(new Training());
        when(trainingDAO.findByTrainerUsernameAndCriteria(
                "trainer.user", LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31), "John"))
                .thenReturn(trainings);

        List<Training> result = trainingService.getTrainerTrainingsByCriteria(
                "trainer.user", LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31), "John");

        assertEquals(trainings, result);
    }

    @Test
    @DisplayName("getTraining: returns training when found")
    void getTraining_found() {
        Training training = new Training();
        training.setId(7L);
        when(trainingDAO.findById(7L)).thenReturn(Optional.of(training));

        Optional<Training> result = trainingService.getTraining(7L);

        assertTrue(result.isPresent());
        assertEquals(training, result.get());
        verify(trainingDAO).findById(7L);
    }

    @Test
    @DisplayName("getTraining: returns empty when not found")
    void getTraining_notFound() {
        when(trainingDAO.findById(99L)).thenReturn(Optional.empty());

        Optional<Training> result = trainingService.getTraining(99L);

        assertTrue(result.isEmpty());
        verify(trainingDAO).findById(99L);
    }

    @Test
    @DisplayName("getAllTrainings: delegates to DAO")
    void getAllTrainings_delegates() {
        List<Training> trainings = List.of(new Training(), new Training());
        when(trainingDAO.findAll()).thenReturn(trainings);

        List<Training> result = trainingService.getAllTrainings();

        assertEquals(trainings, result);
        verify(trainingDAO).findAll();
    }

    @Test
    @DisplayName("getTrainingsByTrainee: delegates to DAO")
    void getTrainingsByTrainee_delegates() {
        List<Training> trainings = List.of(new Training());
        when(trainingDAO.findByTraineeId(10L)).thenReturn(trainings);

        List<Training> result = trainingService.getTrainingsByTrainee(10L);

        assertEquals(trainings, result);
        verify(trainingDAO).findByTraineeId(10L);
    }

    @Test
    @DisplayName("getTrainingsByTrainer: delegates to DAO")
    void getTrainingsByTrainer_delegates() {
        List<Training> trainings = List.of(new Training());
        when(trainingDAO.findByTrainerId(20L)).thenReturn(trainings);

        List<Training> result = trainingService.getTrainingsByTrainer(20L);

        assertEquals(trainings, result);
        verify(trainingDAO).findByTrainerId(20L);
    }
}
