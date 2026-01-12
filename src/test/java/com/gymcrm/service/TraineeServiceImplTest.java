package com.gymcrm.service;

import com.gymcrm.dao.TraineeDAO;
import com.gymcrm.model.Trainee;
import com.gymcrm.util.CredentialsGenerator;
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
@DisplayName("TraineeServiceImpl Tests")
class TraineeServiceImplTest {

    @Mock
    private TraineeDAO traineeDAO;

    @Mock
    private CredentialsGenerator credentialsGenerator;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    private Trainee testTrainee;

    @BeforeEach
    void setUp() {
        testTrainee = new Trainee();
        testTrainee.setUserId(1L);
        testTrainee.setFirstName("John");
        testTrainee.setLastName("Doe");
        testTrainee.setDateOfBirth(LocalDate.of(1990, 1, 1));
        testTrainee.setAddress("123 Main St");
        testTrainee.setIsActive(true);
    }

    @Test
    @DisplayName("Should create trainee successfully with generated credentials")
    void testCreateTrainee_Success() {
        // Arrange
        Trainee newTrainee = new Trainee();
        newTrainee.setFirstName("Jane");
        newTrainee.setLastName("Smith");

        when(credentialsGenerator.generateUsername("Jane", "Smith")).thenReturn("Jane.Smith");
        when(credentialsGenerator.generatePassword()).thenReturn("randomPass123");
        when(traineeDAO.create(any(Trainee.class))).thenAnswer(invocation -> {
            Trainee t = invocation.getArgument(0);
            t.setUserId(1L);
            return t;
        });

        // Act
        Trainee result = traineeService.createTrainee(newTrainee);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals("Jane.Smith", result.getUsername());
        assertEquals("randomPass123", result.getPassword());
    }

    @Test
    @DisplayName("Should call credentials generator with correct first and last name")
    void testCreateTrainee_GeneratesCredentials() {
        // Arrange
        Trainee newTrainee = new Trainee();
        newTrainee.setFirstName("Alice");
        newTrainee.setLastName("Wonder");

        when(credentialsGenerator.generateUsername("Alice", "Wonder")).thenReturn("Alice.Wonder");
        when(credentialsGenerator.generatePassword()).thenReturn("pass123");
        when(traineeDAO.create(any(Trainee.class))).thenReturn(newTrainee);

        // Act
        traineeService.createTrainee(newTrainee);

        // Assert
        verify(credentialsGenerator).generateUsername("Alice", "Wonder");
        verify(credentialsGenerator).generatePassword();
    }

    // ========== updateTrainee Tests ==========

    @Test
    @DisplayName("Should update trainee successfully")
    void testUpdateTrainee_Success() {
        // Arrange
        testTrainee.setLastName("Updated");
        when(traineeDAO.update(testTrainee)).thenReturn(testTrainee);

        // Act
        Trainee result = traineeService.updateTrainee(testTrainee);

        // Assert
        assertNotNull(result);
        assertEquals("Updated", result.getLastName());
        verify(traineeDAO).update(testTrainee);
    }

    @Test
    @DisplayName("Should propagate exception when updating non-existent trainee")
    void testUpdateTrainee_ThrowsWhenNotFound() {
        // Arrange
        when(traineeDAO.update(testTrainee)).thenThrow(new IllegalArgumentException("Trainee not found"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> traineeService.updateTrainee(testTrainee));
        verify(traineeDAO).update(testTrainee);
    }

    // ========== deleteTrainee Tests ==========

    @Test
    @DisplayName("Should delete trainee successfully")
    void testDeleteTrainee_Success() {
        // Arrange
        doNothing().when(traineeDAO).delete(1L);

        // Act
        traineeService.deleteTrainee(1L);

        // Assert
        verify(traineeDAO).delete(1L);
    }

    // ========== getTrainee Tests ==========

    @Test
    @DisplayName("Should return trainee when found")
    void testGetTrainee_Found() {
        // Arrange
        when(traineeDAO.findById(1L)).thenReturn(Optional.of(testTrainee));

        // Act
        Optional<Trainee> result = traineeService.getTrainee(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testTrainee, result.get());
        verify(traineeDAO).findById(1L);
    }

    @Test
    @DisplayName("Should return empty Optional when trainee not found")
    void testGetTrainee_NotFound() {
        // Arrange
        when(traineeDAO.findById(99L)).thenReturn(Optional.empty());

        // Act
        Optional<Trainee> result = traineeService.getTrainee(99L);

        // Assert
        assertFalse(result.isPresent());
        verify(traineeDAO).findById(99L);
    }

    // ========== getAllTrainees Tests ==========

    @Test
    @DisplayName("Should return list of all trainees")
    void testGetAllTrainees_WithData() {
        // Arrange
        Trainee trainee2 = new Trainee();
        trainee2.setUserId(2L);
        trainee2.setFirstName("Jane");
        trainee2.setLastName("Doe");

        List<Trainee> trainees = Arrays.asList(testTrainee, trainee2);
        when(traineeDAO.findAll()).thenReturn(trainees);

        // Act
        List<Trainee> result = traineeService.getAllTrainees();

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(testTrainee));
        assertTrue(result.contains(trainee2));
        verify(traineeDAO).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no trainees exist")
    void testGetAllTrainees_EmptyList() {
        // Arrange
        when(traineeDAO.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Trainee> result = traineeService.getAllTrainees();

        // Assert
        assertTrue(result.isEmpty());
        verify(traineeDAO).findAll();
    }
}
