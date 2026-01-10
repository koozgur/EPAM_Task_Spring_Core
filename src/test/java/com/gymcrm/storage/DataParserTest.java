package com.gymcrm.storage;

import com.gymcrm.model.Trainee;
import com.gymcrm.model.Trainer;
import com.gymcrm.model.Training;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DataParser class.
 * Tests CSV parsing logic for Trainee, Trainer, and Training entities.
 */
@DisplayName("DataParser Tests")
class DataParserTest {

    private Map<Long, Trainee> traineeStorage;
    private Map<Long, Trainer> trainerStorage;
    private Map<Long, Training> trainingStorage;
    private AtomicLong traineeIdGenerator;
    private AtomicLong trainerIdGenerator;
    private AtomicLong trainingIdGenerator;

    @BeforeEach
    void setUp() {
        traineeStorage = new HashMap<>();
        trainerStorage = new HashMap<>();
        trainingStorage = new HashMap<>();
        traineeIdGenerator = new AtomicLong(1);
        trainerIdGenerator = new AtomicLong(1);
        trainingIdGenerator = new AtomicLong(1);
    }

    // ========== Trainee Parsing Tests ==========

    @Test
    @DisplayName("Should parse valid trainee CSV line with all fields")
    void testParseAndAddTrainee_ValidData() {
        // Arrange
        String line = "John,Doe,1990-05-15,123 Main St,true";

        // Act
        DataParser.parseAndAddTrainee(line, traineeStorage, traineeIdGenerator);

        // Assert
        assertEquals(1, traineeStorage.size());
        Trainee trainee = traineeStorage.get(1L);
        assertNotNull(trainee);
        assertEquals(1L, trainee.getUserId());
        assertEquals("John", trainee.getFirstName());
        assertEquals("Doe", trainee.getLastName());
        assertEquals(LocalDate.of(1990, 5, 15), trainee.getDateOfBirth());
        assertEquals("123 Main St", trainee.getAddress());
        assertTrue(trainee.getIsActive());
    }

    @Test
    @DisplayName("Should parse trainee with empty date of birth")
    void testParseAndAddTrainee_EmptyDateOfBirth() {
        // Arrange
        String line = "Jane,Smith,,456 Oak Ave,false";

        // Act
        DataParser.parseAndAddTrainee(line, traineeStorage, traineeIdGenerator);

        // Assert
        assertEquals(1, traineeStorage.size());
        Trainee trainee = traineeStorage.get(1L);
        assertNotNull(trainee);
        assertEquals("Jane", trainee.getFirstName());
        assertEquals("Smith", trainee.getLastName());
        assertNull(trainee.getDateOfBirth());
        assertEquals("456 Oak Ave", trainee.getAddress());
        assertFalse(trainee.getIsActive());
    }

    @Test
    @DisplayName("Should parse trainee with empty address")
    void testParseAndAddTrainee_EmptyAddress() {
        // Arrange
        String line = "Bob,Johnson,1985-12-20,,true";

        // Act
        DataParser.parseAndAddTrainee(line, traineeStorage, traineeIdGenerator);

        // Assert
        assertEquals(1, traineeStorage.size());
        Trainee trainee = traineeStorage.get(1L);
        assertNotNull(trainee);
        assertEquals("Bob", trainee.getFirstName());
        assertEquals("Johnson", trainee.getLastName());
        assertEquals(LocalDate.of(1985, 12, 20), trainee.getDateOfBirth());
        assertEquals("", trainee.getAddress());
        assertTrue(trainee.getIsActive());
    }

    @Test
    @DisplayName("Should handle invalid trainee CSV format gracefully")
    void testParseAndAddTrainee_InvalidFormat() {
        // Arrange
        String line = "John,Doe,1990-05-15"; // Missing fields

        // Act
        DataParser.parseAndAddTrainee(line, traineeStorage, traineeIdGenerator);

        // Assert
        assertEquals(0, traineeStorage.size(), "No trainee should be added for invalid format");
    }

    @Test
    @DisplayName("Should handle trainee with extra fields")
    void testParseAndAddTrainee_ExtraFields() {
        // Arrange
        String line = "John,Doe,1990-05-15,123 Main St,true,extra,data";

        // Act
        DataParser.parseAndAddTrainee(line, traineeStorage, traineeIdGenerator);

        // Assert
        assertEquals(0, traineeStorage.size(), "No trainee should be added for invalid format");
    }

    @Test
    @DisplayName("Should generate sequential IDs for multiple trainees")
    void testParseAndAddTrainee_SequentialIds() {
        // Arrange
        String line1 = "John,Doe,1990-05-15,123 Main St,true";
        String line2 = "Jane,Smith,1992-08-20,456 Oak Ave,false";

        // Act
        DataParser.parseAndAddTrainee(line1, traineeStorage, traineeIdGenerator);
        DataParser.parseAndAddTrainee(line2, traineeStorage, traineeIdGenerator);

        // Assert
        assertEquals(2, traineeStorage.size());
        assertNotNull(traineeStorage.get(1L));
        assertNotNull(traineeStorage.get(2L));
        assertEquals("John", traineeStorage.get(1L).getFirstName());
        assertEquals("Jane", traineeStorage.get(2L).getFirstName());
    }

    @Test
    @DisplayName("Should trim whitespace from trainee fields")
    void testParseAndAddTrainee_TrimWhitespace() {
        // Arrange
        String line = "  John  ,  Doe  ,  1990-05-15  ,  123 Main St  ,  true  ";

        // Act
        DataParser.parseAndAddTrainee(line, traineeStorage, traineeIdGenerator);

        // Assert
        assertEquals(1, traineeStorage.size());
        Trainee trainee = traineeStorage.get(1L);
        assertEquals("John", trainee.getFirstName());
        assertEquals("Doe", trainee.getLastName());
        assertEquals("123 Main St", trainee.getAddress());
    }

    // ========== Trainer Parsing Tests ==========

    @Test
    @DisplayName("Should parse valid trainer CSV line with all fields")
    void testParseAndAddTrainer_ValidData() {
        // Arrange
        String line = "Alice,Williams,Yoga,true";

        // Act
        DataParser.parseAndAddTrainer(line, trainerStorage, trainerIdGenerator);

        // Assert
        assertEquals(1, trainerStorage.size());
        Trainer trainer = trainerStorage.get(1L);
        assertNotNull(trainer);
        assertEquals(1L, trainer.getId());
        assertEquals("Alice", trainer.getFirstName());
        assertEquals("Williams", trainer.getLastName());
        assertEquals("Yoga", trainer.getSpecialization());
        assertTrue(trainer.getIsActive());
    }

    @Test
    @DisplayName("Should parse trainer with empty specialization")
    void testParseAndAddTrainer_EmptySpecialization() {
        // Arrange
        String line = "Bob,Brown,,false";

        // Act
        DataParser.parseAndAddTrainer(line, trainerStorage, trainerIdGenerator);

        // Assert
        assertEquals(1, trainerStorage.size());
        Trainer trainer = trainerStorage.get(1L);
        assertNotNull(trainer);
        assertEquals("Bob", trainer.getFirstName());
        assertEquals("Brown", trainer.getLastName());
        assertEquals("", trainer.getSpecialization());
        assertFalse(trainer.getIsActive());
    }

    @Test
    @DisplayName("Should handle invalid trainer CSV format gracefully")
    void testParseAndAddTrainer_InvalidFormat() {
        // Arrange
        String line = "Alice,Williams,Yoga"; // Missing isActive field

        // Act
        DataParser.parseAndAddTrainer(line, trainerStorage, trainerIdGenerator);

        // Assert
        assertEquals(0, trainerStorage.size(), "No trainer should be added for invalid format");
    }

    @Test
    @DisplayName("Should handle trainer with extra fields")
    void testParseAndAddTrainer_ExtraFields() {
        // Arrange
        String line = "Alice,Williams,Yoga,true,extra,data";

        // Act
        DataParser.parseAndAddTrainer(line, trainerStorage, trainerIdGenerator);

        // Assert
        assertEquals(0, trainerStorage.size(), "No trainer should be added for invalid format");
    }

    @Test
    @DisplayName("Should generate sequential IDs for multiple trainers")
    void testParseAndAddTrainer_SequentialIds() {
        // Arrange
        String line1 = "Alice,Williams,Yoga,true";
        String line2 = "Bob,Brown,Pilates,false";
        String line3 = "Carol,Davis,CrossFit,true";

        // Act
        DataParser.parseAndAddTrainer(line1, trainerStorage, trainerIdGenerator);
        DataParser.parseAndAddTrainer(line2, trainerStorage, trainerIdGenerator);
        DataParser.parseAndAddTrainer(line3, trainerStorage, trainerIdGenerator);

        // Assert
        assertEquals(3, trainerStorage.size());
        assertNotNull(trainerStorage.get(1L));
        assertNotNull(trainerStorage.get(2L));
        assertNotNull(trainerStorage.get(3L));
        assertEquals("Alice", trainerStorage.get(1L).getFirstName());
        assertEquals("Bob", trainerStorage.get(2L).getFirstName());
        assertEquals("Carol", trainerStorage.get(3L).getFirstName());
    }

    @Test
    @DisplayName("Should trim whitespace from trainer fields")
    void testParseAndAddTrainer_TrimWhitespace() {
        // Arrange
        String line = "  Alice  ,  Williams  ,  Yoga  ,  true  ";

        // Act
        DataParser.parseAndAddTrainer(line, trainerStorage, trainerIdGenerator);

        // Assert
        assertEquals(1, trainerStorage.size());
        Trainer trainer = trainerStorage.get(1L);
        assertEquals("Alice", trainer.getFirstName());
        assertEquals("Williams", trainer.getLastName());
        assertEquals("Yoga", trainer.getSpecialization());
    }

    // ========== Training Parsing Tests ==========

    @Test
    @DisplayName("Should parse valid training CSV line with all fields")
    void testParseAndAddTraining_ValidData() {
        // Arrange
        String line = "1,2,Morning Yoga Session,Yoga,2024-01-15,60";

        // Act
        DataParser.parseAndAddTraining(line, trainingStorage, trainingIdGenerator);

        // Assert
        assertEquals(1, trainingStorage.size());
        Training training = trainingStorage.get(1L);
        assertNotNull(training);
        assertEquals(1L, training.getId());
        assertEquals(1L, training.getTraineeId());
        assertEquals(2L, training.getTrainerId());
        assertEquals("Morning Yoga Session", training.getTrainingName());
        assertEquals("Yoga", training.getTrainingType());
        assertEquals(LocalDate.of(2024, 1, 15), training.getTrainingDate());
        assertEquals(60, training.getTrainingDuration());
    }

    @Test
    @DisplayName("Should handle invalid training CSV format gracefully")
    void testParseAndAddTraining_InvalidFormat() {
        // Arrange
        String line = "1,2,Morning Yoga Session,Yoga"; // Missing fields

        // Act
        DataParser.parseAndAddTraining(line, trainingStorage, trainingIdGenerator);

        // Assert
        assertEquals(0, trainingStorage.size(), "No training should be added for invalid format");
    }

    @Test
    @DisplayName("Should handle training with extra fields")
    void testParseAndAddTraining_ExtraFields() {
        // Arrange
        String line = "1,2,Morning Yoga Session,Yoga,2024-01-15,60,extra";

        // Act
        DataParser.parseAndAddTraining(line, trainingStorage, trainingIdGenerator);

        // Assert
        assertEquals(0, trainingStorage.size(), "No training should be added for invalid format");
    }

    @Test
    @DisplayName("Should generate sequential IDs for multiple trainings")
    void testParseAndAddTraining_SequentialIds() {
        // Arrange
        String line1 = "1,2,Morning Yoga Session,Yoga,2024-01-15,60";
        String line2 = "2,3,Evening Pilates,Pilates,2024-01-16,45";

        // Act
        DataParser.parseAndAddTraining(line1, trainingStorage, trainingIdGenerator);
        DataParser.parseAndAddTraining(line2, trainingStorage, trainingIdGenerator);

        // Assert
        assertEquals(2, trainingStorage.size());
        assertNotNull(trainingStorage.get(1L));
        assertNotNull(trainingStorage.get(2L));
        assertEquals("Morning Yoga Session", trainingStorage.get(1L).getTrainingName());
        assertEquals("Evening Pilates", trainingStorage.get(2L).getTrainingName());
    }

    @Test
    @DisplayName("Should trim whitespace from training fields")
    void testParseAndAddTraining_TrimWhitespace() {
        // Arrange
        String line = "  1  ,  2  ,  Morning Yoga Session  ,  Yoga  ,  2024-01-15  ,  60  ";

        // Act
        DataParser.parseAndAddTraining(line, trainingStorage, trainingIdGenerator);

        // Assert
        assertEquals(1, trainingStorage.size());
        Training training = trainingStorage.get(1L);
        assertEquals(1L, training.getTraineeId());
        assertEquals(2L, training.getTrainerId());
        assertEquals("Morning Yoga Session", training.getTrainingName());
        assertEquals("Yoga", training.getTrainingType());
        assertEquals(60, training.getTrainingDuration());
    }

    @Test
    @DisplayName("Should handle invalid number format for training IDs")
    void testParseAndAddTraining_InvalidNumberFormat() {
        // Arrange
        String line = "abc,xyz,Morning Yoga Session,Yoga,2024-01-15,60";

        // Act & Assert
        assertThrows(NumberFormatException.class, () -> 
            DataParser.parseAndAddTraining(line, trainingStorage, trainingIdGenerator)
        );
        assertEquals(0, trainingStorage.size());
    }

    @Test
    @DisplayName("Should handle invalid date format for training")
    void testParseAndAddTraining_InvalidDateFormat() {
        // Arrange
        String line = "1,2,Morning Yoga Session,Yoga,invalid-date,60";

        // Act & Assert
        assertThrows(Exception.class, () -> 
            DataParser.parseAndAddTraining(line, trainingStorage, trainingIdGenerator)
        );
        assertEquals(0, trainingStorage.size());
    }

    @Test
    @DisplayName("Should handle invalid duration format for training")
    void testParseAndAddTraining_InvalidDurationFormat() {
        // Arrange
        String line = "1,2,Morning Yoga Session,Yoga,2024-01-15,invalid";

        // Act & Assert
        assertThrows(NumberFormatException.class, () -> 
            DataParser.parseAndAddTraining(line, trainingStorage, trainingIdGenerator)
        );
        assertEquals(0, trainingStorage.size());
    }
}
