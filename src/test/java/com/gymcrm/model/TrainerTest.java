package com.gymcrm.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Trainer entity.
 * Tests constructors, getters, setters, equals, hashCode, and toString methods.
 */
class TrainerTest {

    @Test
    void testDefaultConstructor() {
        // When
        Trainer trainer = new Trainer();

        // Then
        assertThat(trainer).isNotNull();
        assertThat(trainer.getUserId()).isNull();
        assertThat(trainer.getFirstName()).isNull();
        assertThat(trainer.getLastName()).isNull();
        assertThat(trainer.getUsername()).isNull();
        assertThat(trainer.getPassword()).isNull();
        assertThat(trainer.getSpecialization()).isNull();
        assertThat(trainer.getIsActive()).isNull();
    }

    @Test
    void testConstructorWithAllFields() {
        // Given
        Long id = 1L;
        String firstName = "Mike";
        String lastName = "Trainer";
        String username = "mike.trainer";
        String password = "secure123";
        String specialization = "Yoga";
        Boolean isActive = true;

        // When
        Trainer trainer = new Trainer(id, firstName, lastName, username,
                password, specialization, isActive);

        // Then
        assertThat(trainer.getUserId()).isEqualTo(id);
        assertThat(trainer.getFirstName()).isEqualTo(firstName);
        assertThat(trainer.getLastName()).isEqualTo(lastName);
        assertThat(trainer.getUsername()).isEqualTo(username);
        assertThat(trainer.getPassword()).isEqualTo(password);
        assertThat(trainer.getSpecialization()).isEqualTo(specialization);
        assertThat(trainer.getIsActive()).isEqualTo(isActive);
    }

    @Test
    void testConstructorWithoutId() {
        // Given
        String firstName = "Sarah";
        String lastName = "Coach";
        String username = "sarah.coach";
        String password = "pass789";
        String specialization = "CrossFit";
        Boolean isActive = false;

        // When
        Trainer trainer = new Trainer(firstName, lastName, username,
                password, specialization, isActive);

        // Then
        assertThat(trainer.getUserId()).isNull();
        assertThat(trainer.getFirstName()).isEqualTo(firstName);
        assertThat(trainer.getLastName()).isEqualTo(lastName);
        assertThat(trainer.getUsername()).isEqualTo(username);
        assertThat(trainer.getPassword()).isEqualTo(password);
        assertThat(trainer.getSpecialization()).isEqualTo(specialization);
        assertThat(trainer.getIsActive()).isEqualTo(isActive);
    }

    @Test
    void testGettersAndSetters() {
        // Given
        Trainer trainer = new Trainer();
        Long id = 5L;
        String firstName = "Tom";
        String lastName = "Fitness";
        String username = "tom.fitness";
        String password = "strong456";
        String specialization = "Bodybuilding";
        Boolean isActive = true;

        // When
        trainer.setUserId(id);
        trainer.setFirstName(firstName);
        trainer.setLastName(lastName);
        trainer.setUsername(username);
        trainer.setPassword(password);
        trainer.setSpecialization(specialization);
        trainer.setIsActive(isActive);

        // Then
        assertThat(trainer.getUserId()).isEqualTo(id);
        assertThat(trainer.getFirstName()).isEqualTo(firstName);
        assertThat(trainer.getLastName()).isEqualTo(lastName);
        assertThat(trainer.getUsername()).isEqualTo(username);
        assertThat(trainer.getPassword()).isEqualTo(password);
        assertThat(trainer.getSpecialization()).isEqualTo(specialization);
        assertThat(trainer.getIsActive()).isEqualTo(isActive);
    }

    @Test
    void testEqualsWithSameId() {
        // Given
        Trainer trainer1 = new Trainer(1L, "Mike", "Trainer", "mike.trainer",
                "pass", "Yoga", true);
        Trainer trainer2 = new Trainer(1L, "Sarah", "Coach", "sarah.coach", 
                                        "different", "CrossFit", false);

        // Then - same userId means equal, regardless of other fields
        assertThat(trainer1).isEqualTo(trainer2);
    }

    @Test
    void testEqualsSymmetry() {
        // Given
        Trainer trainer1 = new Trainer(1L, "Mike", "Trainer", "mike.trainer",
                "pass", "Yoga", true);
        Trainer trainer2 = new Trainer(1L, "Sarah", "Coach", "sarah.coach",
                "different", "CrossFit", false);

        // Then - equals should be symmetric: if a.equals(b) then b.equals(a)
        assertThat(trainer1.equals(trainer2)).isEqualTo(trainer2.equals(trainer1));
    }

    @Test
    void testEqualsWithNull() {
        // Given
        Trainer trainer = new Trainer(1L, "Mike", "Trainer", "mike.trainer",
                "pass", "Yoga", true);

        // Then
        assertThat(trainer).isNotEqualTo(null);
    }

    @Test
    void testEqualsWithDifferentClass() {
        // Given
        Trainer trainer = new Trainer(1L, "Mike", "Trainer", "mike.trainer",
                "pass", "Yoga", true);
        String notATrainer = "Not a trainer";

        // Then
        assertThat(trainer).isNotEqualTo(notATrainer);
    }

    @Test
    void testEqualsWithSelf() {
        // Given
        Trainer trainer = new Trainer(1L, "Mike", "Trainer", "mike.trainer",
                "pass", "Yoga", true);

        // Then
        assertThat(trainer).isEqualTo(trainer);
    }

    @Test
    void testHashCode() {
        // Given
        Trainer trainer1 = new Trainer(1L, "Mike", "Trainer", "mike.trainer",
                "pass", "Yoga", true);
        Trainer trainer2 = new Trainer(1L, "Sarah", "Coach", "sarah.coach", 
                                        "different", "CrossFit", false);

        // Then - same userId should produce same hash
        assertThat(trainer1.hashCode()).isEqualTo(trainer2.hashCode());

        // Given
        Trainer trainer3 = new Trainer(1L, "Mike", "Trainer", "mike.trainer",
                "pass", "Yoga", true);
        Trainer trainer4 = new Trainer(2L, "Sarah", "Coach", "sarah.coach",
                "different", "CrossFit", false);

        // Then - different id should produce different hash
        assertThat(trainer3.hashCode()).isNotEqualTo(trainer4.hashCode());
    }

    @Test
    void testToString() {
        // Given
        Trainer trainer = new Trainer(1L, "Mike", "Trainer", "mike.trainer",
                "password", "Yoga", true);

        // When
        String result = trainer.toString();

        // Then
        assertThat(result).contains("Trainer{");
        assertThat(result).contains("userId=1");
        assertThat(result).contains("firstName='Mike'");
        assertThat(result).contains("lastName='Trainer'");
        assertThat(result).contains("username='mike.trainer'");
        assertThat(result).contains("specialization='Yoga'");
        assertThat(result).contains("isActive=true");
        assertThat(result).doesNotContain("password"); // Password should not be in toString
    }

    @Test
    void testNullFieldHandling() {
        // Given
        Trainer trainer = new Trainer(null, null, null, null,
                null, null, null);

        assertThat(trainer.getUserId()).isNull();
        assertThat(trainer.getFirstName()).isNull();
        assertThat(trainer.getLastName()).isNull();
        assertThat(trainer.getUsername()).isNull();
        assertThat(trainer.getPassword()).isNull();
        assertThat(trainer.getSpecialization()).isNull();
        assertThat(trainer.getIsActive()).isNull();

        // toString should not throw exception with null fields
        assertDoesNotThrow(() -> trainer.toString());

        // equals and hashCode should not throw exception with null fields
        assertDoesNotThrow(() -> trainer.equals(new Trainer()));
        assertDoesNotThrow(() -> trainer.hashCode());
    }

    @Test
    void testEqualsWithNullIds() {
        // Given
        Trainer trainer1 = new Trainer(null, "Mike", "Trainer", "mike.trainer",
                "pass", "Yoga", true);
        Trainer trainer2 = new Trainer(null, "Sarah", "Coach", "sarah.coach",
                "different", "CrossFit", false);

        // Then - both have null ids, Objects.equals(null, null) returns true
        assertThat(trainer1).isEqualTo(trainer2);
    }

    @Test
    void testHashCodeConsistency() {
        // Given
        Trainer trainer = new Trainer(1L, "Mike", "Trainer", "mike.trainer",
                "pass", "Yoga", true);

        // Then - multiple calls should return same value
        int hash1 = trainer.hashCode();
        int hash2 = trainer.hashCode();
        int hash3 = trainer.hashCode();
        
        assertThat(hash1).isEqualTo(hash2).isEqualTo(hash3);
    }
}
