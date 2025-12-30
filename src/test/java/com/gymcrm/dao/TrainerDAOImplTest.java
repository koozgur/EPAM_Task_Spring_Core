package com.gymcrm.dao;

import com.gymcrm.model.Trainer;
import com.gymcrm.storage.StorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerDAOImplTest {

    @Mock
    private StorageService storageService;

    @InjectMocks
    private TrainerDAOImpl trainerDAO;

    private Map<Long, Trainer> trainerStorage;
    private Trainer testTrainer;

    @BeforeEach
    void setUp() {
        trainerStorage = new HashMap<>();
        testTrainer = new Trainer(1L, "John", "Doe", "John.Doe", "password", "Cardio", true);
    }

    @Test
    void testCreateTrainerWithoutId() {
        Trainer newTrainer = new Trainer("Jane", "Doe", "Jane.Doe", "password", "Yoga", true);
        
        when(storageService.generateTrainerId()).thenReturn(2L);
        when(storageService.getTrainerStorage()).thenReturn(trainerStorage);

        Trainer created = trainerDAO.create(newTrainer);

        assertNotNull(created.getId());
        assertEquals(2L, created.getId());
        assertTrue(trainerStorage.containsKey(2L));
        assertEquals(newTrainer, trainerStorage.get(2L));
        verify(storageService).generateTrainerId();
    }

    @Test
    void testCreateTrainerWithId() {
        when(storageService.getTrainerStorage()).thenReturn(trainerStorage);

        Trainer created = trainerDAO.create(testTrainer);

        assertEquals(1L, created.getId());
        assertTrue(trainerStorage.containsKey(1L));
        assertEquals(testTrainer, trainerStorage.get(1L));
        verify(storageService, never()).generateTrainerId();
    }

    @Test
    void testUpdateExistingTrainer() {
        trainerStorage.put(1L, testTrainer);
        when(storageService.getTrainerStorage()).thenReturn(trainerStorage);

        Trainer updatedInfo = new Trainer(1L, "John", "Updated", "John.Doe", "newpass", "Strength", true);
        
        Trainer result = trainerDAO.update(updatedInfo);

        assertEquals("Updated", result.getLastName());
        assertEquals("Strength", result.getSpecialization());
        assertEquals(updatedInfo, trainerStorage.get(1L));
    }

    @Test
    void testUpdateNonExistentTrainer() {
        when(storageService.getTrainerStorage()).thenReturn(trainerStorage);

        assertThrows(IllegalArgumentException.class, () -> trainerDAO.update(testTrainer));
    }

    @Test
    void testUpdateTrainerWithNullId() {
        Trainer nullIdTrainer = new Trainer("Jane", "Doe", "Jane.Doe", "password", "Yoga", true);
        
        assertThrows(IllegalArgumentException.class, () -> trainerDAO.update(nullIdTrainer));
    }

    @Test
    void testFindByIdExistingTrainer() {
        trainerStorage.put(1L, testTrainer);
        when(storageService.getTrainerStorage()).thenReturn(trainerStorage);

        Optional<Trainer> result = trainerDAO.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(testTrainer, result.get());
    }

    @Test
    void testFindByIdNonExistentTrainer() {
        when(storageService.getTrainerStorage()).thenReturn(trainerStorage);

        Optional<Trainer> result = trainerDAO.findById(99L);

        assertFalse(result.isPresent());
    }

    @Test
    void testFindByIdWithNullId() {
        Optional<Trainer> result = trainerDAO.findById(null);

        assertFalse(result.isPresent());
    }

    @Test
    void testFindAllWithMultipleTrainers() {
        trainerStorage.put(1L, testTrainer);
        Trainer trainer2 = new Trainer(2L, "Jane", "Doe", "Jane.Doe", "password", "Yoga", true);
        trainerStorage.put(2L, trainer2);
        
        when(storageService.getTrainerStorage()).thenReturn(trainerStorage);

        List<Trainer> result = trainerDAO.findAll();

        assertEquals(2, result.size());
        assertTrue(result.contains(testTrainer));
        assertTrue(result.contains(trainer2));
    }

    @Test
    void testFindAllWithEmptyStorage() {
        when(storageService.getTrainerStorage()).thenReturn(trainerStorage);

        List<Trainer> result = trainerDAO.findAll();

        assertTrue(result.isEmpty());
    }

    @Test
    void testFindByUsernameExistingTrainer() {
        trainerStorage.put(1L, testTrainer);
        when(storageService.getTrainerStorage()).thenReturn(trainerStorage);

        Optional<Trainer> result = trainerDAO.findByUsername("John.Doe");

        assertTrue(result.isPresent());
        assertEquals(testTrainer, result.get());
    }

    @Test
    void testFindByUsernameNonExistent() {
        trainerStorage.put(1L, testTrainer);
        when(storageService.getTrainerStorage()).thenReturn(trainerStorage);

        Optional<Trainer> result = trainerDAO.findByUsername("Jane.Doe");

        assertFalse(result.isPresent());
    }

    @Test
    void testFindByUsernameWithNullUsername() {
        Optional<Trainer> result = trainerDAO.findByUsername(null);

        assertFalse(result.isPresent());
    }
}
