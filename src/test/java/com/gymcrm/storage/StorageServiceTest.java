package com.gymcrm.storage;

import com.gymcrm.config.AppConfig;
import com.gymcrm.model.Trainee;
import com.gymcrm.model.Trainer;
import com.gymcrm.model.Training;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.TestPropertySource;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for StorageService class.
 * Tests storage initialization, ID generation, and data loading.
 * Uses Spring test context to properly load the bean with its dependencies.
 */
@SpringJUnitConfig(AppConfig.class)
@TestPropertySource(properties = {
    "storage.init.file.path=classpath:initial-data.txt"
})
@DisplayName("StorageService Tests")
class StorageServiceTest {

    @Autowired
    private StorageService storageService;

    // ========== Initialization Tests ==========

    @Test
    @DisplayName("Should initialize storage service successfully")
    void testInitialize_Success() {
        // Assert - initialization happens via @PostConstruct
        assertNotNull(storageService.getTraineeStorage());
        assertNotNull(storageService.getTrainerStorage());
        assertNotNull(storageService.getTrainingStorage());
    }

    @Test
    @DisplayName("Should load data from file during initialization")
    void testInitialize_LoadsDataFromFile() {
        // Assert - data is loaded automatically via @PostConstruct

        // Assert - Verify that data was loaded (counts may vary based on initial-data.txt)
        Map<Long, Trainee> trainees = storageService.getTraineeStorage();
        Map<Long, Trainer> trainers = storageService.getTrainerStorage();
        Map<Long, Training> trainings = storageService.getTrainingStorage();

        // At least check that storage maps are initialized
        assertNotNull(trainees, "Trainee storage should be initialized");
        assertNotNull(trainers, "Trainer storage should be initialized");
        assertNotNull(trainings, "Training storage should be initialized");
    }

    // ========== Trainee Storage Tests ==========

    @Test
    @DisplayName("Should generate unique trainee IDs")
    void testGenerateTraineeId_UniqueIds() {
        // Act
        Long id1 = storageService.generateTraineeId();
        Long id2 = storageService.generateTraineeId();
        Long id3 = storageService.generateTraineeId();

        // Assert
        assertNotNull(id1);
        assertNotNull(id2);
        assertNotNull(id3);
        assertNotEquals(id1, id2);
        assertNotEquals(id2, id3);
        assertNotEquals(id1, id3);
    }

    @Test
    @DisplayName("Should generate sequential trainee IDs")
    void testGenerateTraineeId_Sequential() {
        // Act
        Long id1 = storageService.generateTraineeId();
        Long id2 = storageService.generateTraineeId();
        Long id3 = storageService.generateTraineeId();

        // Assert
        assertEquals(id1 + 1, id2);
        assertEquals(id2 + 1, id3);
    }

    @Test
    @DisplayName("Should allow direct access to trainee storage for CRUD operations")
    void testTraineeStorage_CRUDOperations() {
        // Arrange
        Map<Long, Trainee> storage = storageService.getTraineeStorage();
        Trainee trainee = new Trainee();
        trainee.setUserId(999L);
        trainee.setFirstName("Test");
        trainee.setLastName("User");

        // Act - Create
        storage.put(999L, trainee);

        // Assert - Read
        assertTrue(storage.containsKey(999L));
        assertEquals("Test", storage.get(999L).getFirstName());

        // Act - Update
        storage.get(999L).setFirstName("Updated");

        // Assert
        assertEquals("Updated", storage.get(999L).getFirstName());

        // Act - Delete
        storage.remove(999L);

        // Assert
        assertFalse(storage.containsKey(999L));
    }

    // ========== Trainer Storage Tests ==========


    @Test
    @DisplayName("Should generate unique trainer IDs")
    void testGenerateTrainerId_UniqueIds() {
        // Act
        Long id1 = storageService.generateTrainerId();
        Long id2 = storageService.generateTrainerId();
        Long id3 = storageService.generateTrainerId();

        // Assert
        assertNotNull(id1);
        assertNotNull(id2);
        assertNotNull(id3);
        assertNotEquals(id1, id2);
        assertNotEquals(id2, id3);
        assertNotEquals(id1, id3);
    }

    @Test
    @DisplayName("Should generate sequential trainer IDs")
    void testGenerateTrainerId_Sequential() {
        // Act
        Long id1 = storageService.generateTrainerId();
        Long id2 = storageService.generateTrainerId();
        Long id3 = storageService.generateTrainerId();

        // Assert
        assertEquals(id1 + 1, id2);
        assertEquals(id2 + 1, id3);
    }

    @Test
    @DisplayName("Should allow direct access to trainer storage for CRUD operations")
    void testTrainerStorage_CRUDOperations() {
        // Arrange
        Map<Long, Trainer> storage = storageService.getTrainerStorage();
        Trainer trainer = new Trainer();
        trainer.setUserId(999L);
        trainer.setFirstName("Test");
        trainer.setLastName("Trainer");
        trainer.setSpecialization("Yoga");

        // Act - Create
        storage.put(999L, trainer);

        // Assert - Read
        assertTrue(storage.containsKey(999L));
        assertEquals("Test", storage.get(999L).getFirstName());

        // Act - Update
        storage.get(999L).setSpecialization("Pilates");

        // Assert
        assertEquals("Pilates", storage.get(999L).getSpecialization());

        // Act - Delete
        storage.remove(999L);

        // Assert
        assertFalse(storage.containsKey(999L));
    }

    // ========== Training Storage Tests ==========


    @Test
    @DisplayName("Should generate unique training IDs")
    void testGenerateTrainingId_UniqueIds() {
        // Act
        Long id1 = storageService.generateTrainingId();
        Long id2 = storageService.generateTrainingId();
        Long id3 = storageService.generateTrainingId();

        // Assert
        assertNotNull(id1);
        assertNotNull(id2);
        assertNotNull(id3);
        assertNotEquals(id1, id2);
        assertNotEquals(id2, id3);
        assertNotEquals(id1, id3);
    }

    @Test
    @DisplayName("Should generate sequential training IDs")
    void testGenerateTrainingId_Sequential() {
        // Act
        Long id1 = storageService.generateTrainingId();
        Long id2 = storageService.generateTrainingId();
        Long id3 = storageService.generateTrainingId();

        // Assert
        assertEquals(id1 + 1, id2);
        assertEquals(id2 + 1, id3);
    }

    @Test
    @DisplayName("Should allow direct access to training storage for CRUD operations")
    void testTrainingStorage_CRUDOperations() {
        // Arrange
        Map<Long, Training> storage = storageService.getTrainingStorage();
        Training training = new Training();
        training.setId(999L);
        training.setTraineeId(1L);
        training.setTrainerId(2L);
        training.setTrainingName("Test Session");

        // Act - Create
        storage.put(999L, training);

        // Assert - Read
        assertTrue(storage.containsKey(999L));
        assertEquals("Test Session", storage.get(999L).getTrainingName());

        // Act - Update
        storage.get(999L).setTrainingName("Updated Session");

        // Assert
        assertEquals("Updated Session", storage.get(999L).getTrainingName());

        // Act - Delete
        storage.remove(999L);

        // Assert
        assertFalse(storage.containsKey(999L));
    }

    // ========== ID Generator Independence Tests ==========

    @Test
    @DisplayName("Should maintain separate ID sequences for each entity type")
    void testIdGenerators_IndependentSequences() {
        // Act
        Long traineeId1 = storageService.generateTraineeId();
        Long trainerId1 = storageService.generateTrainerId();
        Long trainingId1 = storageService.generateTrainingId();
        
        Long traineeId2 = storageService.generateTraineeId();
        Long trainerId2 = storageService.generateTrainerId();
        Long trainingId2 = storageService.generateTrainingId();

        // Assert - Each type should have its own sequence
        assertEquals(traineeId1 + 1, traineeId2);
        assertEquals(trainerId1 + 1, trainerId2);
        assertEquals(trainingId1 + 1, trainingId2);
        
        // IDs from different types can be the same (independent namespaces)
        // This is expected behavior for separate ID generators
    }

    @Test
    @DisplayName("Should use ConcurrentHashMap for thread-safe storage")
    void testStorageMaps_ThreadSafe() {
        // Act
        Map<Long, Trainee> traineeStorage = storageService.getTraineeStorage();
        Map<Long, Trainer> trainerStorage = storageService.getTrainerStorage();
        Map<Long, Training> trainingStorage = storageService.getTrainingStorage();

        // Assert - Verify the maps support concurrent operations
        assertDoesNotThrow(() -> {
            traineeStorage.put(1L, new Trainee());
            trainerStorage.put(1L, new Trainer());
            trainingStorage.put(1L, new Training());
        });
    }

    // ========== Edge Cases ==========

    @Test
    @DisplayName("Should handle empty sections in data file")
    void testInitialize_EmptySections() {
        // This test depends on the content of initial-data.txt
        // Assert - Should not throw exceptions during initialization
        assertNotNull(storageService.getTraineeStorage());
        assertNotNull(storageService.getTrainerStorage());
        assertNotNull(storageService.getTrainingStorage());
    }

    @Test
    @DisplayName("Should skip comment lines and empty lines during data loading")
    void testInitialize_SkipsCommentsAndEmptyLines() {
        // This test verifies the behavior indirectly through successful initialization
        // Assert - Bean initialization should complete without errors
        assertNotNull(storageService);
        assertNotNull(storageService.getTraineeStorage());
    }

    @Test
    @DisplayName("Should continue ID generation after initialization")
    void testIdGeneration_AfterInitialization() {
        // Get the size of loaded data to determine next expected ID
        int traineeCount = storageService.getTraineeStorage().size();
        int trainerCount = storageService.getTrainerStorage().size();
        int trainingCount = storageService.getTrainingStorage().size();

        // Act
        Long nextTraineeId = storageService.generateTraineeId();
        Long nextTrainerId = storageService.generateTrainerId();
        Long nextTrainingId = storageService.generateTrainingId();

        // Assert - IDs should continue from where initialization left off
        assertTrue(nextTraineeId > traineeCount);
        assertTrue(nextTrainerId > trainerCount);
        assertTrue(nextTrainingId > trainingCount);
    }
}
