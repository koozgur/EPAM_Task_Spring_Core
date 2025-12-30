package com.gymcrm.dao;

import com.gymcrm.model.Training;
import com.gymcrm.storage.StorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingDAOImplTest {

    @Mock
    private StorageService storageService;

    @InjectMocks
    private TrainingDAOImpl trainingDAO;

    private Map<Long, Training> trainingStorage;
    private Training testTraining;

    @BeforeEach
    void setUp() {
        trainingStorage = new HashMap<>();
        testTraining = new Training(1L, 10L, 20L, "Cardio Session", "Cardio", LocalDate.of(2023, 1, 1), 60);
    }

    @Test
    void testCreateTrainingWithoutId() {
        Training newTraining = new Training(10L, 20L, "Yoga Session", "Yoga", LocalDate.of(2023, 1, 2), 60);
        
        when(storageService.generateTrainingId()).thenReturn(2L);
        when(storageService.getTrainingStorage()).thenReturn(trainingStorage);

        Training created = trainingDAO.create(newTraining);

        assertNotNull(created.getId());
        assertEquals(2L, created.getId());
        assertTrue(trainingStorage.containsKey(2L));
        assertEquals(newTraining, trainingStorage.get(2L));
        verify(storageService).generateTrainingId();
    }

    @Test
    void testCreateTrainingWithId() {
        when(storageService.getTrainingStorage()).thenReturn(trainingStorage);

        Training created = trainingDAO.create(testTraining);

        assertEquals(1L, created.getId());
        assertTrue(trainingStorage.containsKey(1L));
        assertEquals(testTraining, trainingStorage.get(1L));
        verify(storageService, never()).generateTrainingId();
    }

    @Test
    void testFindByIdExistingTraining() {
        trainingStorage.put(1L, testTraining);
        when(storageService.getTrainingStorage()).thenReturn(trainingStorage);

        Optional<Training> result = trainingDAO.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(testTraining, result.get());
    }

    @Test
    void testFindByIdNonExistentTraining() {
        when(storageService.getTrainingStorage()).thenReturn(trainingStorage);

        Optional<Training> result = trainingDAO.findById(99L);

        assertFalse(result.isPresent());
    }

    @Test
    void testFindByIdWithNullId() {
        Optional<Training> result = trainingDAO.findById(null);

        assertFalse(result.isPresent());
    }

    @Test
    void testFindAllWithMultipleTrainings() {
        trainingStorage.put(1L, testTraining);
        Training training2 = new Training(2L, 10L, 20L, "Yoga Session", "Yoga", LocalDate.of(2023, 1, 2), 60);
        trainingStorage.put(2L, training2);
        
        when(storageService.getTrainingStorage()).thenReturn(trainingStorage);

        List<Training> result = trainingDAO.findAll();

        assertEquals(2, result.size());
        assertTrue(result.contains(testTraining));
        assertTrue(result.contains(training2));
    }

    @Test
    void testFindAllWithEmptyStorage() {
        when(storageService.getTrainingStorage()).thenReturn(trainingStorage);

        List<Training> result = trainingDAO.findAll();

        assertTrue(result.isEmpty());
    }

    @Test
    void testFindByTraineeIdMultiple() {
        trainingStorage.put(1L, testTraining); // traineeId = 10
        Training training2 = new Training(2L, 10L, 21L, "Another Session", "Strength", LocalDate.of(2023, 1, 3), 45);
        trainingStorage.put(2L, training2); // traineeId = 10
        Training training3 = new Training(3L, 11L, 20L, "Other Trainee", "Cardio", LocalDate.of(2023, 1, 4), 30);
        trainingStorage.put(3L, training3); // traineeId = 11
        
        when(storageService.getTrainingStorage()).thenReturn(trainingStorage);

        List<Training> result = trainingDAO.findByTraineeId(10L);

        assertEquals(2, result.size());
        assertTrue(result.contains(testTraining));
        assertTrue(result.contains(training2));
        assertFalse(result.contains(training3));
    }

    @Test
    void testFindByTraineeIdNone() {
        trainingStorage.put(1L, testTraining); // traineeId = 10
        when(storageService.getTrainingStorage()).thenReturn(trainingStorage);

        List<Training> result = trainingDAO.findByTraineeId(99L);

        assertTrue(result.isEmpty());
    }

    @Test
    void testFindByTraineeIdWithNullId() {
        List<Training> result = trainingDAO.findByTraineeId(null);

        assertTrue(result.isEmpty());
    }

    @Test
    void testFindByTrainerIdMultiple() {
        trainingStorage.put(1L, testTraining); // trainerId = 20
        Training training2 = new Training(2L, 11L, 20L, "Another Session", "Strength", LocalDate.of(2023, 1, 3), 45);
        trainingStorage.put(2L, training2); // trainerId = 20
        Training training3 = new Training(3L, 10L, 21L, "Other Trainer", "Cardio", LocalDate.of(2023, 1, 4), 30);
        trainingStorage.put(3L, training3); // trainerId = 21
        
        when(storageService.getTrainingStorage()).thenReturn(trainingStorage);

        List<Training> result = trainingDAO.findByTrainerId(20L);

        assertEquals(2, result.size());
        assertTrue(result.contains(testTraining));
        assertTrue(result.contains(training2));
        assertFalse(result.contains(training3));
    }

    @Test
    void testFindByTrainerIdNone() {
        trainingStorage.put(1L, testTraining); // trainerId = 20
        when(storageService.getTrainingStorage()).thenReturn(trainingStorage);

        List<Training> result = trainingDAO.findByTrainerId(99L);

        assertTrue(result.isEmpty());
    }

    @Test
    void testFindByTrainerIdWithNullId() {
        List<Training> result = trainingDAO.findByTrainerId(null);

        assertTrue(result.isEmpty());
    }
}
