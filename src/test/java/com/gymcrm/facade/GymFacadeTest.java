package com.gymcrm.facade;

import com.gymcrm.model.Trainee;
import com.gymcrm.model.Trainer;
import com.gymcrm.model.Training;
import com.gymcrm.service.TraineeService;
import com.gymcrm.service.TrainerService;
import com.gymcrm.service.TrainingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for GymFacade class.
 * Focuses on testing the getGymSummary() method which contains business logic.
 */
@ExtendWith(MockitoExtension.class)
class GymFacadeTest {

    @Mock
    private TraineeService traineeService;

    @Mock
    private TrainerService trainerService;

    @Mock
    private TrainingService trainingService;

    @InjectMocks
    private GymFacade gymFacade;

    // ==================== getGymSummary() TESTS ====================

    @Test
    @DisplayName("getGymSummary returns correct format with data")
    void getGymSummary_withData_returnsCorrectFormat() {
        // Arrange
        List<Trainee> trainees = Arrays.asList(new Trainee(), new Trainee(), new Trainee());
        List<Trainer> trainers = Arrays.asList(new Trainer(), new Trainer());
        List<Training> trainings = Arrays.asList(new Training(), new Training(), new Training(), new Training());

        when(traineeService.getAllTrainees()).thenReturn(trainees);
        when(trainerService.getAllTrainers()).thenReturn(trainers);
        when(trainingService.getAllTrainings()).thenReturn(trainings);

        // Act
        String summary = gymFacade.getGymSummary();

        // Assert
        assertEquals("Gym CRM Summary: 3 Trainees, 2 Trainers, 4 Training Sessions", summary);
    }

    @Test
    @DisplayName("getGymSummary returns zeros when gym is empty")
    void getGymSummary_withEmptyGym_returnsZeroCounts() {
        // Arrange
        when(traineeService.getAllTrainees()).thenReturn(Collections.emptyList());
        when(trainerService.getAllTrainers()).thenReturn(Collections.emptyList());
        when(trainingService.getAllTrainings()).thenReturn(Collections.emptyList());

        // Act
        String summary = gymFacade.getGymSummary();

        // Assert
        assertEquals("Gym CRM Summary: 0 Trainees, 0 Trainers, 0 Training Sessions", summary);
    }
}
