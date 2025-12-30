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

    @Test
    @DisplayName("Should handle missing data file gracefully")
    void testInitialize_MissingFile() {
        // Assert - Storage should be initialized even with missing file
        assertNotNull(storageService.getTraineeStorage());
        assertNotNull(storageService.getTrainerStorage());
        assertNotNull(storageService.getTrainingStorage());
        
        // Storage maps should be empty but initialized
        assertEquals(0, storageService.getTraineeStorage().size());
        assertEquals(0, storageService.getTrainerStorage().size());
        assertEquals(0, storageService.getTrainingStorage().size());
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
        trainee.setId(traineeId);
        trainee.setFirstName("Test");
        trainee.setLastName("Trainee");

        Trainer trainer = new Trainer();
        trainer.setId(trainerId);
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
            storageService.getTraineeStorage().put(traineeId, trainee);
            storageService.getTrainerStorage().put(trainerId, trainer);
            storageService.getTrainingStorage().put(trainingId, training);
        });

        assertEquals(1, storageService.getTraineeStorage().size());
        assertEquals(1, storageService.getTrainerStorage().size());
        assertEquals(1, storageService.getTrainingStorage().size());
    }
}
