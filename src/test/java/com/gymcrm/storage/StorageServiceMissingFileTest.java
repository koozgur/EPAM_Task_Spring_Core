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
 * Unit tests for StorageService with missing data file scenario.
 * Tests that the service handles missing initialization files gracefully.
 */
@SpringJUnitConfig(AppConfig.class)
@TestPropertySource(properties = {
    "storage.init.file.path=classpath:non-existent-file.txt"
})
@DisplayName("StorageService Missing File Tests")
class StorageServiceMissingFileTest {

    @Autowired
    private StorageService storageService;
    
    @Resource
    private Map<Long, Trainee> traineeStorage;
    
    @Resource
    private Map<Long, Trainer> trainerStorage;
    
    @Resource
    private Map<Long, Training> trainingStorage;

    @Test
    @DisplayName("Should handle missing data file gracefully")
    void testInitialize_MissingFile() {
        // Assert - Storage should be initialized even with missing file
        assertNotNull(traineeStorage);
        assertNotNull(trainerStorage);
        assertNotNull(trainingStorage);
        
        // Storage maps should be empty but initialized
        assertEquals(0, traineeStorage.size());
        assertEquals(0, trainerStorage.size());
        assertEquals(0, trainingStorage.size());
    }

    @Test
    @DisplayName("Should still generate IDs when no data file is loaded")
    void testGenerateIds_WithMissingFile() {
        // Act
        Long traineeId = storageService.generateTraineeId();
        Long trainerId = storageService.generateTrainerId();
        Long trainingId = storageService.generateTrainingId();

        // Assert
        assertNotNull(traineeId);
        assertNotNull(trainerId);
        assertNotNull(trainingId);
        assertEquals(1L, traineeId);
        assertEquals(1L, trainerId);
        assertEquals(1L, trainingId);
    }

    @Test
    @DisplayName("Should allow adding data after initialization with missing file")
    void testAddData_WithMissingFile() {
        // Arrange
        Long traineeId = storageService.generateTraineeId();
        Long trainerId = storageService.generateTrainerId();
        Long trainingId = storageService.generateTrainingId();

        Trainee trainee = new Trainee();
        trainee.setUserId(traineeId);
        trainee.setFirstName("Test");
        trainee.setLastName("Trainee");

        Trainer trainer = new Trainer();
        trainer.setUserId(trainerId);
        trainer.setFirstName("Test");
        trainer.setLastName("Trainer");
        trainer.setSpecialization("Fitness");

        Training training = new Training();
        training.setId(trainingId);
        training.setTraineeId(traineeId);
        training.setTrainerId(trainerId);
        training.setTrainingName("Test Session");

        // Act & Assert - Should be able to use storage normally
        assertDoesNotThrow(() -> {
            traineeStorage.put(traineeId, trainee);
            trainerStorage.put(trainerId, trainer);
            trainingStorage.put(trainingId, training);
        });

        assertEquals(1, traineeStorage.size());
        assertEquals(1, trainerStorage.size());
        assertEquals(1, trainingStorage.size());
    }
}
