package com.gymcrm.dao;

import com.gymcrm.model.Trainee;
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
class TraineeDAOImplTest {

    @Mock
    private StorageService storageService; //external dependency, not the main entity we are testing

    @InjectMocks
    private TraineeDAOImpl traineeDAO;

    //we aim to test actual logic on the map, explicitly controlling the map
    private Map<Long, Trainee> traineeStorage;
    private Trainee testTrainee;

    @BeforeEach
    void setUp() {
        traineeStorage = new HashMap<>();
        traineeDAO.setTraineeStorage(traineeStorage);
        testTrainee = new Trainee(1L, "John", "Doe", "John.Doe", "password", LocalDate.of(1990, 1, 1), "Address", true);
    }

    @Test
    void testCreateTrainee() {
        Trainee newTrainee = new Trainee("Jane", "Doe", "Jane.Doe", "password", LocalDate.of(1995, 1, 1), "Address", true);
        
        when(storageService.generateTraineeId()).thenReturn(2L);

        Trainee created = traineeDAO.create(newTrainee);

        assertNotNull(created.getUserId());
        assertEquals(2L, created.getUserId());
        assertTrue(traineeStorage.containsKey(2L));
        assertEquals(newTrainee, traineeStorage.get(2L));
        verify(storageService).generateTraineeId();
    }

    @Test
    void testUpdateExistingTrainee() {
        traineeStorage.put(1L, testTrainee);

        Trainee updatedInfo = new Trainee(1L, "John", "Updated", "John.Doe", "newpass", LocalDate.of(1990, 1, 1), "New Address", true);
        
        Trainee result = traineeDAO.update(updatedInfo);

        assertEquals("Updated", result.getLastName());
        assertEquals("New Address", result.getAddress());
        assertEquals(updatedInfo, traineeStorage.get(1L));
    }

    @Test
    void testUpdateNonExistentTrainee() {
        assertThrows(IllegalArgumentException.class, () -> traineeDAO.update(testTrainee));
    }

    @Test
    void testUpdateTraineeWithNullId() {
        Trainee nullIdTrainee = new Trainee("Jane", "Doe", "Jane.Doe", "password", LocalDate.of(1995, 1, 1), "Address", true);
        
        assertThrows(IllegalArgumentException.class, () -> traineeDAO.update(nullIdTrainee));
    }

    @Test
    void testDeleteExistingTrainee() {
        traineeStorage.put(1L, testTrainee);
        traineeDAO.delete(1L);

        assertFalse(traineeStorage.containsKey(1L));
    }

    @Test
    void testDeleteNonExistentTrainee() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> traineeDAO.delete(99L));
        assertEquals("Trainee not found: 99", exception.getMessage());
    }

    @Test
    void testDeleteWithNullId() {
        assertThrows(IllegalArgumentException.class, () -> traineeDAO.delete(null));
    }

    @Test
    void testFindByIdExistingTrainee() {
        traineeStorage.put(1L, testTrainee);

        Optional<Trainee> result = traineeDAO.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(testTrainee, result.get());
    }

    @Test
    void testFindByIdNonExistentTrainee() {
        Optional<Trainee> result = traineeDAO.findById(99L);

        assertFalse(result.isPresent());
    }

    @Test
    void testFindByIdWithNullId() {
        Optional<Trainee> result = traineeDAO.findById(null);

        assertFalse(result.isPresent());
    }

    @Test
    void testFindAllWithMultipleTrainees() {
        traineeStorage.put(1L, testTrainee);
        Trainee trainee2 = new Trainee(2L, "Jane", "Doe", "Jane.Doe", "password", LocalDate.of(1995, 1, 1), "Address", true);
        traineeStorage.put(2L, trainee2);

        List<Trainee> result = traineeDAO.findAll();

        assertEquals(2, result.size());
        assertTrue(result.contains(testTrainee));
        assertTrue(result.contains(trainee2));
    }

    @Test
    void testFindAllWithEmptyStorage() {
        List<Trainee> result = traineeDAO.findAll();

        assertTrue(result.isEmpty());
    }

    @Test
    void testFindByUsernameExistingTrainee() {
        traineeStorage.put(1L, testTrainee);

        Optional<Trainee> result = traineeDAO.findByUsername("John.Doe");

        assertTrue(result.isPresent());
        assertEquals(testTrainee, result.get());
    }

    @Test
    void testFindByUsernameNonExistent() {
        traineeStorage.put(1L, testTrainee);

        Optional<Trainee> result = traineeDAO.findByUsername("Jane.Doe");

        assertFalse(result.isPresent());
    }

    @Test
    void testFindByUsernameWithNullUsername() {
        Optional<Trainee> result = traineeDAO.findByUsername(null);

        assertFalse(result.isPresent());
    }
}
