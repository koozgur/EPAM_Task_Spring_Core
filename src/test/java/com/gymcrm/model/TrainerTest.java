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
        Trainer trainer2 = new Trainer(1L, "Sarah", "Coach", "mike.trainer", 
                                        "different", "CrossFit", false);

        // Then
        assertThat(trainer1).isEqualTo(trainer2);
    }

    @Test
    void testEqualsWithDifferentId() {
        // Given
        Trainer trainer1 = new Trainer(1L, "Mike", "Trainer", "mike.trainer", 
                                        "pass", "Yoga", true);
        Trainer trainer2 = new Trainer(2L, "Mike", "Trainer", "mike.trainer", 
                                        "pass", "Yoga", true);

        // Then
        assertThat(trainer1).isNotEqualTo(trainer2);
    }

    @Test
    void testEqualsWithSameUsername() {
        // Given
        Trainer trainer1 = new Trainer(1L, "Mike", "Trainer", "mike.trainer", 
                                        "pass", "Yoga", true);
        Trainer trainer2 = new Trainer(1L, "Sarah", "Coach", "mike.trainer", 
                                        "different", "CrossFit", false);

        // Then - should be equal because id and username match
        assertThat(trainer1).isEqualTo(trainer2);
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
        Trainer trainer2 = new Trainer(1L, "Sarah", "Coach", "mike.trainer", 
                                        "different", "CrossFit", false);

        // Then - same id and username should produce same hash
        assertThat(trainer1.hashCode()).isEqualTo(trainer2.hashCode());
    }

    @Test
    void testHashCodeDifferent() {
        // Given
        Trainer trainer1 = new Trainer(1L, "Mike", "Trainer", "mike.trainer", 
                                        "pass", "Yoga", true);
        Trainer trainer2 = new Trainer(2L, "Sarah", "Coach", "sarah.coach", 
                                        "different", "CrossFit", false);

        // Then - different id and username should produce different hash
        assertThat(trainer1.hashCode()).isNotEqualTo(trainer2.hashCode());
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
        Trainer trainer2 = new Trainer(null, "Sarah", "Coach", "mike.trainer", 
                                        "different", "CrossFit", false);

        // Then - should be equal if usernames match even with null ids
        assertThat(trainer1).isEqualTo(trainer2);
    }

    @Test
    void testEqualsWithNullUsernames() {
        // Given
        Trainer trainer1 = new Trainer(1L, "Mike", "Trainer", null, 
                                        "pass", "Yoga", true);
        Trainer trainer2 = new Trainer(1L, "Sarah", "Coach", null, 
                                        "different", "CrossFit", false);

        // Then - should be equal if ids match even with null usernames
        assertThat(trainer1).isEqualTo(trainer2);
    }

    @Test
    void testDifferentSpecializations() {
        // Given
        Trainer trainer = new Trainer();
        
        // When/Then - test various specializations
        trainer.setSpecialization("Yoga");
        assertThat(trainer.getSpecialization()).isEqualTo("Yoga");
        
        trainer.setSpecialization("CrossFit");
        assertThat(trainer.getSpecialization()).isEqualTo("CrossFit");
        
        trainer.setSpecialization("Bodybuilding");
        assertThat(trainer.getSpecialization()).isEqualTo("Bodybuilding");
        
        trainer.setSpecialization("Cardio");
        assertThat(trainer.getSpecialization()).isEqualTo("Cardio");
    }
}
