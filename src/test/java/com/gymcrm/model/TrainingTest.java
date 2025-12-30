package com.gymcrm.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Training entity.
 * Tests constructors, getters, setters, equals, hashCode, and toString methods.
 */
class TrainingTest {

    @Test
    void testDefaultConstructor() {
        // When
        Training training = new Training();

        // Then
        assertThat(training).isNotNull();
        assertThat(training.getId()).isNull();
        assertThat(training.getTraineeId()).isNull();
        assertThat(training.getTrainerId()).isNull();
        assertThat(training.getTrainingName()).isNull();
        assertThat(training.getTrainingType()).isNull();
        assertThat(training.getTrainingDate()).isNull();
        assertThat(training.getTrainingDuration()).isNull();
    }

    @Test
    void testConstructorWithAllFields() {
        // Given
        Long id = 1L;
        Long traineeId = 10L;
        Long trainerId = 20L;
        String trainingName = "Morning Yoga";
        String trainingType = "Yoga";
        LocalDate trainingDate = LocalDate.of(2024, 1, 15);
        Integer trainingDuration = 60;

        // When
        Training training = new Training(id, traineeId, trainerId, trainingName, 
                                          trainingType, trainingDate, trainingDuration);

        // Then
        assertThat(training.getId()).isEqualTo(id);
        assertThat(training.getTraineeId()).isEqualTo(traineeId);
        assertThat(training.getTrainerId()).isEqualTo(trainerId);
        assertThat(training.getTrainingName()).isEqualTo(trainingName);
        assertThat(training.getTrainingType()).isEqualTo(trainingType);
        assertThat(training.getTrainingDate()).isEqualTo(trainingDate);
        assertThat(training.getTrainingDuration()).isEqualTo(trainingDuration);
    }

    @Test
    void testConstructorWithoutId() {
        // Given
        Long traineeId = 15L;
        Long trainerId = 25L;
        String trainingName = "Evening CrossFit";
        String trainingType = "CrossFit";
        LocalDate trainingDate = LocalDate.of(2024, 2, 20);
        Integer trainingDuration = 90;

        // When
        Training training = new Training(traineeId, trainerId, trainingName, 
                                          trainingType, trainingDate, trainingDuration);

        // Then
        assertThat(training.getId()).isNull();
        assertThat(training.getTraineeId()).isEqualTo(traineeId);
        assertThat(training.getTrainerId()).isEqualTo(trainerId);
        assertThat(training.getTrainingName()).isEqualTo(trainingName);
        assertThat(training.getTrainingType()).isEqualTo(trainingType);
        assertThat(training.getTrainingDate()).isEqualTo(trainingDate);
        assertThat(training.getTrainingDuration()).isEqualTo(trainingDuration);
    }

    @Test
    void testGettersAndSetters() {
        // Given
        Training training = new Training();
        Long id = 100L;
        Long traineeId = 50L;
        Long trainerId = 75L;
        String trainingName = "Cardio Session";
        String trainingType = "Cardio";
        LocalDate trainingDate = LocalDate.of(2024, 3, 10);
        Integer trainingDuration = 45;

        // When
        training.setId(id);
        training.setTraineeId(traineeId);
        training.setTrainerId(trainerId);
        training.setTrainingName(trainingName);
        training.setTrainingType(trainingType);
        training.setTrainingDate(trainingDate);
        training.setTrainingDuration(trainingDuration);

        // Then
        assertThat(training.getId()).isEqualTo(id);
        assertThat(training.getTraineeId()).isEqualTo(traineeId);
        assertThat(training.getTrainerId()).isEqualTo(trainerId);
        assertThat(training.getTrainingName()).isEqualTo(trainingName);
        assertThat(training.getTrainingType()).isEqualTo(trainingType);
        assertThat(training.getTrainingDate()).isEqualTo(trainingDate);
        assertThat(training.getTrainingDuration()).isEqualTo(trainingDuration);
    }

    @Test
    void testEqualsWithSameId() {
        // Given
        Training training1 = new Training(1L, 10L, 20L, "Yoga", 
                                           "Yoga", LocalDate.now(), 60);
        Training training2 = new Training(1L, 15L, 25L, "CrossFit", 
                                           "CrossFit", LocalDate.now(), 90);

        // Then
        assertThat(training1).isEqualTo(training2);
    }

    @Test
    void testEqualsWithDifferentId() {
        // Given
        Training training1 = new Training(1L, 10L, 20L, "Yoga", 
                                           "Yoga", LocalDate.now(), 60);
        Training training2 = new Training(2L, 10L, 20L, "Yoga", 
                                           "Yoga", LocalDate.now(), 60);

        // Then
        assertThat(training1).isNotEqualTo(training2);
    }

    @Test
    void testEqualsWithNull() {
        // Given
        Training training = new Training(1L, 10L, 20L, "Yoga", 
                                          "Yoga", LocalDate.now(), 60);

        // Then
        assertThat(training).isNotEqualTo(null);
    }

    @Test
    void testEqualsWithDifferentClass() {
        // Given
        Training training = new Training(1L, 10L, 20L, "Yoga", 
                                          "Yoga", LocalDate.now(), 60);
        String notATraining = "Not a training";

        // Then
        assertThat(training).isNotEqualTo(notATraining);
    }

    @Test
    void testEqualsWithSelf() {
        // Given
        Training training = new Training(1L, 10L, 20L, "Yoga", 
                                          "Yoga", LocalDate.now(), 60);

        // Then
        assertThat(training).isEqualTo(training);
    }

    @Test
    void testHashCode() {
        // Given
        Training training1 = new Training(1L, 10L, 20L, "Yoga", 
                                           "Yoga", LocalDate.now(), 60);
        Training training2 = new Training(1L, 15L, 25L, "CrossFit", 
                                           "CrossFit", LocalDate.now(), 90);

        // Then - same id should produce same hash
        assertThat(training1.hashCode()).isEqualTo(training2.hashCode());
    }

    @Test
    void testHashCodeDifferent() {
        // Given
        Training training1 = new Training(1L, 10L, 20L, "Yoga", 
                                           "Yoga", LocalDate.now(), 60);
        Training training2 = new Training(2L, 10L, 20L, "Yoga", 
                                           "Yoga", LocalDate.now(), 60);

        // Then - different id should produce different hash
        assertThat(training1.hashCode()).isNotEqualTo(training2.hashCode());
    }

    @Test
    void testToString() {
        // Given
        Training training = new Training(1L, 10L, 20L, "Morning Yoga", 
                                          "Yoga", LocalDate.of(2024, 1, 15), 60);

        // When
        String result = training.toString();

        // Then
        assertThat(result).contains("Training{");
        assertThat(result).contains("id=1");
        assertThat(result).contains("traineeId=10");
        assertThat(result).contains("trainerId=20");
        assertThat(result).contains("trainingName='Morning Yoga'");
        assertThat(result).contains("trainingType='Yoga'");
        assertThat(result).contains("trainingDate=2024-01-15");
        assertThat(result).contains("trainingDuration=60");
    }

    @Test
    void testNullFieldHandling() {
        // Given
        Training training = new Training(null, null, null, null, 
                                          null, null, null);

        // Then - should handle null fields gracefully
        assertThat(training.getId()).isNull();
        assertThat(training.getTraineeId()).isNull();
        assertThat(training.getTrainerId()).isNull();
        assertThat(training.getTrainingName()).isNull();
        assertThat(training.getTrainingType()).isNull();
        assertThat(training.getTrainingDate()).isNull();
        assertThat(training.getTrainingDuration()).isNull();
        
        // toString should not throw exception with null fields
        assertDoesNotThrow(() -> training.toString());
        
        // equals and hashCode should not throw exception with null fields
        assertDoesNotThrow(() -> training.equals(new Training()));
        assertDoesNotThrow(() -> training.hashCode());
    }

    @Test
    void testEqualsWithNullIds() {
        // Given
        Training training1 = new Training(null, 10L, 20L, "Yoga", 
                                           "Yoga", LocalDate.now(), 60);
        Training training2 = new Training(null, 15L, 25L, "CrossFit", 
                                           "CrossFit", LocalDate.now(), 90);

        // Then - should be equal if both have null ids
        assertThat(training1).isEqualTo(training2);
    }

    @Test
    void testDifferentTrainingDurations() {
        // Given
        Training training = new Training();
        
        // When/Then - test various durations
        training.setTrainingDuration(30);
        assertThat(training.getTrainingDuration()).isEqualTo(30);
        
        training.setTrainingDuration(60);
        assertThat(training.getTrainingDuration()).isEqualTo(60);
        
        training.setTrainingDuration(90);
        assertThat(training.getTrainingDuration()).isEqualTo(90);
        
        training.setTrainingDuration(120);
        assertThat(training.getTrainingDuration()).isEqualTo(120);
    }

    @Test
    void testDifferentTrainingTypes() {
        // Given
        Training training = new Training();
        
        // When/Then - test various training types
        training.setTrainingType("Yoga");
        assertThat(training.getTrainingType()).isEqualTo("Yoga");
        
        training.setTrainingType("CrossFit");
        assertThat(training.getTrainingType()).isEqualTo("CrossFit");
        
        training.setTrainingType("Bodybuilding");
        assertThat(training.getTrainingType()).isEqualTo("Bodybuilding");
        
        training.setTrainingType("Cardio");
        assertThat(training.getTrainingType()).isEqualTo("Cardio");
    }

    @Test
    void testTrainingDateInPast() {
        // Given
        Training training = new Training();
        LocalDate pastDate = LocalDate.of(2020, 1, 1);
        
        // When
        training.setTrainingDate(pastDate);
        
        // Then
        assertThat(training.getTrainingDate()).isEqualTo(pastDate);
        assertThat(training.getTrainingDate()).isBefore(LocalDate.now());
    }

    @Test
    void testTrainingDateInFuture() {
        // Given
        Training training = new Training();
        LocalDate futureDate = LocalDate.of(2025, 12, 31);
        
        // When
        training.setTrainingDate(futureDate);
        
        // Then
        assertThat(training.getTrainingDate()).isEqualTo(futureDate);
        assertThat(training.getTrainingDate()).isAfter(LocalDate.now());
    }

    @Test
    void testTrainingDateToday() {
        // Given
        Training training = new Training();
        LocalDate today = LocalDate.now();
        
        // When
        training.setTrainingDate(today);
        
        // Then
        assertThat(training.getTrainingDate()).isEqualTo(today);
    }

    @Test
    void testZeroDuration() {
        // Given
        Training training = new Training();
        
        // When
        training.setTrainingDuration(0);
        
        // Then
        assertThat(training.getTrainingDuration()).isEqualTo(0);
    }

    @Test
    void testNegativeDuration() {
        // Given
        Training training = new Training();
        
        // When
        training.setTrainingDuration(-10);
        
        // Then
        assertThat(training.getTrainingDuration()).isEqualTo(-10);
    }
}
