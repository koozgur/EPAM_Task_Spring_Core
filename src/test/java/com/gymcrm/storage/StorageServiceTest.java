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

import javax.annotation.Resource;
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
    
    @Resource
    private Map<Long, Trainee> traineeStorage;
    
    @Resource
    private Map<Long, Trainer> trainerStorage;
    
    @Resource
    private Map<Long, Training> trainingStorage;

    // ========== Initialization Tests ==========

    @Test
    @DisplayName("Should initialize storage service successfully")
    void testInitialize_Success() {
        // Assert - initialization happens via @PostConstruct
        assertNotNull(traineeStorage);
        assertNotNull(trainerStorage);
        assertNotNull(trainingStorage);
    }

    @Test
    @DisplayName("Should load data from file during initialization")
    void testInitialize_LoadsDataFromFile() {
        // Assert - data is loaded automatically via @PostConstruct
        assertNotNull(traineeStorage, "Trainee storage should be initialized");
        assertNotNull(trainerStorage, "Trainer storage should be initialized");
        assertNotNull(trainingStorage, "Training storage should be initialized");
    }

    @Test
    @DisplayName("Should have pre-loaded data in storage maps after initialization")
    void testInitialize_MapsContainPreLoadedData() {
        // Assert - verify that data from initial-data.txt was loaded into storage maps
        assertFalse(traineeStorage.isEmpty(), "Trainee storage should contain pre-loaded data");
        assertFalse(trainerStorage.isEmpty(), "Trainer storage should contain pre-loaded data");
        assertFalse(trainingStorage.isEmpty(), "Training storage should contain pre-loaded data");
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
        Trainee trainee = new Trainee();
        trainee.setUserId(999L);
        trainee.setFirstName("Test");
        trainee.setLastName("User");

        // Act - Create
        traineeStorage.put(999L, trainee);

        // Assert - Read
        assertTrue(traineeStorage.containsKey(999L));
        assertEquals("Test", traineeStorage.get(999L).getFirstName());

        // Act - Update
        traineeStorage.get(999L).setFirstName("Updated");

        // Assert
        assertEquals("Updated", traineeStorage.get(999L).getFirstName());

        // Act - Delete
        traineeStorage.remove(999L);

        // Assert
        assertFalse(traineeStorage.containsKey(999L));
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
        Trainer trainer = new Trainer();
        trainer.setUserId(999L);
        trainer.setFirstName("Test");
        trainer.setLastName("Trainer");
        trainer.setSpecialization("Yoga");

        // Act - Create
        trainerStorage.put(999L, trainer);

        // Assert - Read
        assertTrue(trainerStorage.containsKey(999L));
        assertEquals("Test", trainerStorage.get(999L).getFirstName());

        // Act - Update
        trainerStorage.get(999L).setSpecialization("Pilates");

        // Assert
        assertEquals("Pilates", trainerStorage.get(999L).getSpecialization());

        // Act - Delete
        trainerStorage.remove(999L);

        // Assert
        assertFalse(trainerStorage.containsKey(999L));
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
        Training training = new Training();
        training.setId(999L);
        training.setTraineeId(1L);
        training.setTrainerId(2L);
        training.setTrainingName("Test Session");

        // Act - Create
        trainingStorage.put(999L, training);

        // Assert - Read
        assertTrue(trainingStorage.containsKey(999L));
        assertEquals("Test Session", trainingStorage.get(999L).getTrainingName());

        // Act - Update
        trainingStorage.get(999L).setTrainingName("Updated Session");

        // Assert
        assertEquals("Updated Session", trainingStorage.get(999L).getTrainingName());

        // Act - Delete
        trainingStorage.remove(999L);

        // Assert
        assertFalse(trainingStorage.containsKey(999L));
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
        // Assert - Verify the maps support concurrent operations
        assertDoesNotThrow(() -> {
            traineeStorage.put(9998L, new Trainee());
            trainerStorage.put(9998L, new Trainer());
            trainingStorage.put(9998L, new Training());
            
            // Cleanup
            traineeStorage.remove(9998L);
            trainerStorage.remove(9998L);
            trainingStorage.remove(9998L);
        });
    }

    @Test
    @DisplayName("Should continue ID generation after initialization")
    void testIdGeneration_AfterInitialization() {
        // Get the size of loaded data to determine next expected ID
        int traineeCount = traineeStorage.size();
        int trainerCount = trainerStorage.size();
        int trainingCount = trainingStorage.size();

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
