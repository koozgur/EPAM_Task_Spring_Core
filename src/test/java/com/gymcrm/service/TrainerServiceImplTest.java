package com.gymcrm.service;

import com.gymcrm.dao.TrainerDAO;
import com.gymcrm.model.Trainer;
import com.gymcrm.util.CredentialsGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TrainerServiceImpl Tests")
class TrainerServiceImplTest {

    @Mock
    private TrainerDAO trainerDAO;

    @Mock
    private CredentialsGenerator credentialsGenerator;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    private Trainer testTrainer;

    @BeforeEach
    void setUp() {
        testTrainer = new Trainer();
        testTrainer.setUserId(1L);
        testTrainer.setFirstName("Mike");
        testTrainer.setLastName("Coach");
        testTrainer.setSpecialization("Cardio");
        testTrainer.setIsActive(true);
    }

    // ========== createTrainer Tests ==========

    @Test
    @DisplayName("Should create trainer successfully with generated credentials")
    void testCreateTrainer_Success() {
        // Arrange
        Trainer newTrainer = new Trainer();
        newTrainer.setFirstName("Sarah");
        newTrainer.setLastName("Fit");
        newTrainer.setSpecialization("Yoga");

        when(credentialsGenerator.generateUsername("Sarah", "Fit")).thenReturn("Sarah.Fit");
        when(credentialsGenerator.generatePassword()).thenReturn("randomPass123");
        when(trainerDAO.create(any(Trainer.class))).thenAnswer(invocation -> {
            Trainer t = invocation.getArgument(0);
            t.setUserId(1L);
            return t;
        });

        // Act
        Trainer result = trainerService.createTrainer(newTrainer);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals("Sarah.Fit", result.getUsername());
        assertEquals("randomPass123", result.getPassword());
    }

    @Test
    @DisplayName("Should call credentials generator with correct first and last name")
    void testCreateTrainer_GeneratesCredentials() {
        // Arrange
        Trainer newTrainer = new Trainer();
        newTrainer.setFirstName("Tom");
        newTrainer.setLastName("Strong");

        when(credentialsGenerator.generateUsername("Tom", "Strong")).thenReturn("Tom.Strong");
        when(credentialsGenerator.generatePassword()).thenReturn("pass123");
        when(trainerDAO.create(any(Trainer.class))).thenReturn(newTrainer);

        // Act
        trainerService.createTrainer(newTrainer);

        // Assert
        verify(credentialsGenerator).generateUsername("Tom", "Strong");
        verify(credentialsGenerator).generatePassword();
    }

    // ========== updateTrainer Tests ==========

    @Test
    @DisplayName("Should update trainer successfully")
    void testUpdateTrainer_Success() {
        // Arrange
        testTrainer.setSpecialization("Strength Training");
        when(trainerDAO.update(testTrainer)).thenReturn(testTrainer);

        // Act
        Trainer result = trainerService.updateTrainer(testTrainer);

        // Assert
        assertNotNull(result);
        assertEquals("Strength Training", result.getSpecialization());
        verify(trainerDAO).update(testTrainer);
    }

    @Test
    @DisplayName("Should propagate exception when updating non-existent trainer")
    void testUpdateTrainer_ThrowsWhenNotFound() {
        // Arrange
        when(trainerDAO.update(testTrainer)).thenThrow(new IllegalArgumentException("Trainer not found"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> trainerService.updateTrainer(testTrainer));
        verify(trainerDAO).update(testTrainer);
    }

    // ========== getTrainer Tests ==========

    @Test
    @DisplayName("Should return trainer when found")
    void testGetTrainer_Found() {
        // Arrange
        when(trainerDAO.findById(1L)).thenReturn(Optional.of(testTrainer));

        // Act
        Optional<Trainer> result = trainerService.getTrainer(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testTrainer, result.get());
        verify(trainerDAO).findById(1L);
    }

    @Test
    @DisplayName("Should return empty Optional when trainer not found")
    void testGetTrainer_NotFound() {
        // Arrange
        when(trainerDAO.findById(99L)).thenReturn(Optional.empty());

        // Act
        Optional<Trainer> result = trainerService.getTrainer(99L);

        // Assert
        assertFalse(result.isPresent());
        verify(trainerDAO).findById(99L);
    }

    // ========== getAllTrainers Tests ==========

    @Test
    @DisplayName("Should return list of all trainers")
    void testGetAllTrainers_WithData() {
        // Arrange
        Trainer trainer2 = new Trainer();
        trainer2.setUserId(2L);
        trainer2.setFirstName("Anna");
        trainer2.setLastName("Flex");
        trainer2.setSpecialization("Pilates");

        List<Trainer> trainers = Arrays.asList(testTrainer, trainer2);
        when(trainerDAO.findAll()).thenReturn(trainers);

        // Act
        List<Trainer> result = trainerService.getAllTrainers();

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(testTrainer));
        assertTrue(result.contains(trainer2));
        verify(trainerDAO).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no trainers exist")
    void testGetAllTrainers_EmptyList() {
        // Arrange
        when(trainerDAO.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Trainer> result = trainerService.getAllTrainers();

        // Assert
        assertTrue(result.isEmpty());
        verify(trainerDAO).findAll();
    }
}
