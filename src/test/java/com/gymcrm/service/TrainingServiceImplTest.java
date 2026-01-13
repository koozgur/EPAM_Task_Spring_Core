package com.gymcrm.service;

import com.gymcrm.dao.TraineeDAO;
import com.gymcrm.dao.TrainerDAO;
import com.gymcrm.dao.TrainingDAO;
import com.gymcrm.model.Trainee;
import com.gymcrm.model.Trainer;
import com.gymcrm.model.Training;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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

    @InjectMocks
    private TrainingServiceImpl trainingService;

    private Training testTraining;
    private Trainee testTrainee;
    private Trainer testTrainer;

    @BeforeEach
    void setUp() {
        testTrainee = new Trainee();
        testTrainee.setUserId(10L);
        testTrainee.setFirstName("John");
        testTrainee.setLastName("Doe");

        testTrainer = new Trainer();
        testTrainer.setUserId(20L);
        testTrainer.setFirstName("Mike");
        testTrainer.setLastName("Coach");

        testTraining = new Training();
        testTraining.setId(1L);
        testTraining.setTraineeId(10L);
        testTraining.setTrainerId(20L);
        testTraining.setTrainingName("Cardio Session");
        testTraining.setTrainingType("Cardio");
        testTraining.setTrainingDate(LocalDate.of(2023, 6, 15));
        testTraining.setTrainingDuration(60);
    }

    // ========== createTraining Tests ==========

    @Test
    @DisplayName("Should create training successfully when trainee and trainer exist")
    void testCreateTraining_Success() {
        // Arrange
        Training newTraining = new Training();
        newTraining.setTraineeId(10L);
        newTraining.setTrainerId(20L);
        newTraining.setTrainingName("Yoga Session");
        newTraining.setTrainingType("Yoga");
        newTraining.setTrainingDate(LocalDate.of(2023, 7, 1));
        newTraining.setTrainingDuration(45);

        when(traineeDAO.findById(10L)).thenReturn(Optional.of(testTrainee));
        when(trainerDAO.findById(20L)).thenReturn(Optional.of(testTrainer));
        when(trainingDAO.create(any(Training.class))).thenAnswer(invocation -> {
            Training t = invocation.getArgument(0);
            t.setId(1L);
            return t;
        });

        // Act
        Training result = trainingService.createTraining(newTraining);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Yoga Session", result.getTrainingName());
        verify(traineeDAO).findById(10L);
        verify(trainerDAO).findById(20L);
        verify(trainingDAO).create(newTraining);
    }

    @Test
    @DisplayName("Should throw exception when trainee does not exist")
    void testCreateTraining_ThrowsWhenTraineeNotFound() {
        // Arrange
        Training newTraining = new Training();
        newTraining.setTraineeId(99L);
        newTraining.setTrainerId(20L);
        newTraining.setTrainingName("Test Session");
        newTraining.setTrainingDate(LocalDate.of(2023, 7, 1));
        newTraining.setTrainingDuration(60);

        when(traineeDAO.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> trainingService.createTraining(newTraining));
        
        assertEquals("Trainee not found with ID: 99", exception.getMessage());
        verify(traineeDAO).findById(99L);
        verify(trainerDAO, never()).findById(any());
        verify(trainingDAO, never()).create(any());
    }

    @Test
    @DisplayName("Should throw exception when trainer does not exist")
    void testCreateTraining_ThrowsWhenTrainerNotFound() {
        // Arrange
        Training newTraining = new Training();
        newTraining.setTraineeId(10L);
        newTraining.setTrainerId(99L);
        newTraining.setTrainingName("Test Session");
        newTraining.setTrainingDate(LocalDate.of(2023, 7, 1));
        newTraining.setTrainingDuration(60);

        when(traineeDAO.findById(10L)).thenReturn(Optional.of(testTrainee));
        when(trainerDAO.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> trainingService.createTraining(newTraining));
        
        assertEquals("Trainer not found with ID: 99", exception.getMessage());
        verify(traineeDAO).findById(10L);
        verify(trainerDAO).findById(99L);
        verify(trainingDAO, never()).create(any());
    }

    // ========== getTraining Tests ==========

    @Test
    @DisplayName("Should return training when found")
    void testGetTraining_Found() {
        // Arrange
        when(trainingDAO.findById(1L)).thenReturn(Optional.of(testTraining));

        // Act
        Optional<Training> result = trainingService.getTraining(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testTraining, result.get());
        verify(trainingDAO).findById(1L);
    }

    @Test
    @DisplayName("Should return empty Optional when training not found")
    void testGetTraining_NotFound() {
        // Arrange
        when(trainingDAO.findById(99L)).thenReturn(Optional.empty());

        // Act
        Optional<Training> result = trainingService.getTraining(99L);

        // Assert
        assertFalse(result.isPresent());
        verify(trainingDAO).findById(99L);
    }

    // ========== getAllTrainings Tests ==========

    @Test
    @DisplayName("Should return list of all trainings")
    void testGetAllTrainings_WithData() {
        // Arrange
        Training training2 = new Training();
        training2.setId(2L);
        training2.setTraineeId(10L);
        training2.setTrainerId(21L);
        training2.setTrainingName("Strength Session");

        List<Training> trainings = Arrays.asList(testTraining, training2);
        when(trainingDAO.findAll()).thenReturn(trainings);

        // Act
        List<Training> result = trainingService.getAllTrainings();

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(testTraining));
        assertTrue(result.contains(training2));
        verify(trainingDAO).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no trainings exist")
    void testGetAllTrainings_EmptyList() {
        // Arrange
        when(trainingDAO.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Training> result = trainingService.getAllTrainings();

        // Assert
        assertTrue(result.isEmpty());
        verify(trainingDAO).findAll();
    }

    // ========== getTrainingsByTrainee Tests ==========

    @Test
    @DisplayName("Should return trainings for given trainee ID")
    void testGetTrainingsByTrainee_WithData() {
        // Arrange
        Training training2 = new Training();
        training2.setId(2L);
        training2.setTraineeId(10L);
        training2.setTrainerId(21L);
        training2.setTrainingName("Another Session");

        List<Training> trainings = Arrays.asList(testTraining, training2);
        when(trainingDAO.findByTraineeId(10L)).thenReturn(trainings);

        // Act
        List<Training> result = trainingService.getTrainingsByTrainee(10L);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(t -> t.getTraineeId().equals(10L)));
        verify(trainingDAO).findByTraineeId(10L);
    }

    @Test
    @DisplayName("Should return empty list when trainee has no trainings")
    void testGetTrainingsByTrainee_EmptyList() {
        // Arrange
        when(trainingDAO.findByTraineeId(99L)).thenReturn(Collections.emptyList());

        // Act
        List<Training> result = trainingService.getTrainingsByTrainee(99L);

        // Assert
        assertTrue(result.isEmpty());
        verify(trainingDAO).findByTraineeId(99L);
    }

    // ========== getTrainingsByTrainer Tests ==========

    @Test
    @DisplayName("Should return trainings for given trainer ID")
    void testGetTrainingsByTrainer_WithData() {
        // Arrange
        Training training2 = new Training();
        training2.setId(2L);
        training2.setTraineeId(11L);
        training2.setTrainerId(20L);
        training2.setTrainingName("Another Session");

        List<Training> trainings = Arrays.asList(testTraining, training2);
        when(trainingDAO.findByTrainerId(20L)).thenReturn(trainings);

        // Act
        List<Training> result = trainingService.getTrainingsByTrainer(20L);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(t -> t.getTrainerId().equals(20L)));
        verify(trainingDAO).findByTrainerId(20L);
    }

    @Test
    @DisplayName("Should return empty list when trainer has no trainings")
    void testGetTrainingsByTrainer_EmptyList() {
        // Arrange
        when(trainingDAO.findByTrainerId(99L)).thenReturn(Collections.emptyList());

        // Act
        List<Training> result = trainingService.getTrainingsByTrainer(99L);

        // Assert
        assertTrue(result.isEmpty());
        verify(trainingDAO).findByTrainerId(99L);
    }
}
