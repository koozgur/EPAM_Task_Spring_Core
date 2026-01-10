package com.gymcrm.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Trainee entity.
 * Tests constructors, getters, setters, equals, hashCode, and toString methods.
 */
class TraineeTest {

    @Test
    void testDefaultConstructor() {
        // When
        Trainee trainee = new Trainee();

        // Then
        assertThat(trainee).isNotNull();
        assertThat(trainee.getUserId()).isNull();
        assertThat(trainee.getFirstName()).isNull();
        assertThat(trainee.getLastName()).isNull();
        assertThat(trainee.getUsername()).isNull();
        assertThat(trainee.getPassword()).isNull();
        assertThat(trainee.getDateOfBirth()).isNull();
        assertThat(trainee.getAddress()).isNull();
        assertThat(trainee.getIsActive()).isNull();
    }

    @Test
    void testConstructorWithAllFields() {
        // Given
        Long id = 1L;
        String firstName = "John";
        String lastName = "Doe";
        String username = "john.doe";
        String password = "password123";
        LocalDate dateOfBirth = LocalDate.of(1990, 5, 15);
        String address = "123 Main St";
        Boolean isActive = true;

        // When
        Trainee trainee = new Trainee(id, firstName, lastName, username, 
                                       password, dateOfBirth, address, isActive);

        // Then
        assertThat(trainee.getUserId()).isEqualTo(id);
        assertThat(trainee.getFirstName()).isEqualTo(firstName);
        assertThat(trainee.getLastName()).isEqualTo(lastName);
        assertThat(trainee.getUsername()).isEqualTo(username);
        assertThat(trainee.getPassword()).isEqualTo(password);
        assertThat(trainee.getDateOfBirth()).isEqualTo(dateOfBirth);
        assertThat(trainee.getAddress()).isEqualTo(address);
        assertThat(trainee.getIsActive()).isEqualTo(isActive);
    }

    @Test
    void testConstructorWithoutId() {
        // Given
        String firstName = "Jane";
        String lastName = "Smith";
        String username = "jane.smith";
        String password = "pass456";
        LocalDate dateOfBirth = LocalDate.of(1995, 8, 20);
        String address = "456 Oak Ave";
        Boolean isActive = false;

        // When
        Trainee trainee = new Trainee(firstName, lastName, username, 
                                       password, dateOfBirth, address, isActive);

        // Then
        assertThat(trainee.getUserId()).isNull();
        assertThat(trainee.getFirstName()).isEqualTo(firstName);
        assertThat(trainee.getLastName()).isEqualTo(lastName);
        assertThat(trainee.getUsername()).isEqualTo(username);
        assertThat(trainee.getPassword()).isEqualTo(password);
        assertThat(trainee.getDateOfBirth()).isEqualTo(dateOfBirth);
        assertThat(trainee.getAddress()).isEqualTo(address);
        assertThat(trainee.getIsActive()).isEqualTo(isActive);
    }

    @Test
    void testGettersAndSetters() {
        // Given
        Trainee trainee = new Trainee();
        Long id = 10L;
        String firstName = "Alice";
        String lastName = "Johnson";
        String username = "alice.johnson";
        String password = "secure789";
        LocalDate dateOfBirth = LocalDate.of(1988, 3, 10);
        String address = "789 Elm St";
        Boolean isActive = true;

        // When
        trainee.setUserId(id);
        trainee.setFirstName(firstName);
        trainee.setLastName(lastName);
        trainee.setUsername(username);
        trainee.setPassword(password);
        trainee.setDateOfBirth(dateOfBirth);
        trainee.setAddress(address);
        trainee.setIsActive(isActive);

        // Then
        assertThat(trainee.getUserId()).isEqualTo(id);
        assertThat(trainee.getFirstName()).isEqualTo(firstName);
        assertThat(trainee.getLastName()).isEqualTo(lastName);
        assertThat(trainee.getUsername()).isEqualTo(username);
        assertThat(trainee.getPassword()).isEqualTo(password);
        assertThat(trainee.getDateOfBirth()).isEqualTo(dateOfBirth);
        assertThat(trainee.getAddress()).isEqualTo(address);
        assertThat(trainee.getIsActive()).isEqualTo(isActive);
    }

    @Test
    void testEqualsWithSameId() {
        // Given
        Trainee trainee1 = new Trainee(1L, "John", "Doe", "john.doe", 
                                        "pass", LocalDate.now(), "Address", true);
        Trainee trainee2 = new Trainee(1L, "Jane", "Smith", "john.doe", 
                                        "different", LocalDate.now(), "Other", false);

        // Then
        assertThat(trainee1).isEqualTo(trainee2);
    }

    @Test
    void testEqualsWithDifferentId() {
        // Given
        Trainee trainee1 = new Trainee(1L, "John", "Doe", "john.doe", 
                                        "pass", LocalDate.now(), "Address", true);
        Trainee trainee2 = new Trainee(2L, "John", "Doe", "john.doe", 
                                        "pass", LocalDate.now(), "Address", true);

        // Then
        assertThat(trainee1).isNotEqualTo(trainee2);
    }

    @Test
    void testEqualsWithSameUsername() {
        // Given
        Trainee trainee1 = new Trainee(1L, "John", "Doe", "john.doe", 
                                        "pass", LocalDate.now(), "Address", true);
        Trainee trainee2 = new Trainee(1L, "Jane", "Smith", "john.doe", 
                                        "different", LocalDate.now(), "Other", false);

        // Then - should be equal because id and username match
        assertThat(trainee1).isEqualTo(trainee2);
    }

    @Test
    void testEqualsWithNull() {
        // Given
        Trainee trainee = new Trainee(1L, "John", "Doe", "john.doe", 
                                       "pass", LocalDate.now(), "Address", true);

        // Then
        assertThat(trainee).isNotEqualTo(null);
    }

    @Test
    void testEqualsWithDifferentClass() {
        // Given
        Trainee trainee = new Trainee(1L, "John", "Doe", "john.doe", 
                                       "pass", LocalDate.now(), "Address", true);
        String notATrainee = "Not a trainee";

        // Then
        assertThat(trainee).isNotEqualTo(notATrainee);
    }

    @Test
    void testEqualsWithSelf() {
        // Given
        Trainee trainee = new Trainee(1L, "John", "Doe", "john.doe", 
                                       "pass", LocalDate.now(), "Address", true);

        // Then
        assertThat(trainee).isEqualTo(trainee);
    }

    @Test
    void testHashCode() {
        // Given
        Trainee trainee1 = new Trainee(1L, "John", "Doe", "john.doe", 
                                        "pass", LocalDate.now(), "Address", true);
        Trainee trainee2 = new Trainee(1L, "Jane", "Smith", "john.doe", 
                                        "different", LocalDate.now(), "Other", false);

        // Then - same id and username should produce same hash
        assertThat(trainee1.hashCode()).isEqualTo(trainee2.hashCode());
    }

    @Test
    void testHashCodeDifferent() {
        // Given
        Trainee trainee1 = new Trainee(1L, "John", "Doe", "john.doe", 
                                        "pass", LocalDate.now(), "Address", true);
        Trainee trainee2 = new Trainee(2L, "Jane", "Smith", "jane.smith", 
                                        "different", LocalDate.now(), "Other", false);

        // Then - different id and username should produce different hash
        assertThat(trainee1.hashCode()).isNotEqualTo(trainee2.hashCode());
    }

    @Test
    void testToString() {
        // Given
        Trainee trainee = new Trainee(1L, "John", "Doe", "john.doe", 
                                       "password", LocalDate.of(1990, 5, 15), 
                                       "123 Main St", true);

        // When
        String result = trainee.toString();

        // Then
        assertThat(result).contains("Trainee{");
        assertThat(result).contains("userId=1");
        assertThat(result).contains("firstName='John'");
        assertThat(result).contains("lastName='Doe'");
        assertThat(result).contains("username='john.doe'");
        assertThat(result).contains("dateOfBirth=1990-05-15");
        assertThat(result).contains("address='123 Main St'");
        assertThat(result).contains("isActive=true");
        assertThat(result).doesNotContain("password"); // Password should not be in toString
    }

    @Test
    void testNullFieldHandling() {
        // Given
        Trainee trainee = new Trainee(null, null, null, null, 
                                       null, null, null, null);

        assertThat(trainee.getUserId()).isNull();
        assertThat(trainee.getFirstName()).isNull();
        assertThat(trainee.getLastName()).isNull();
        assertThat(trainee.getUsername()).isNull();
        assertThat(trainee.getPassword()).isNull();
        assertThat(trainee.getDateOfBirth()).isNull();
        assertThat(trainee.getAddress()).isNull();
        assertThat(trainee.getIsActive()).isNull();
        
        // toString should not throw exception with null fields
        assertDoesNotThrow(() -> trainee.toString());
        
        // equals and hashCode should not throw exception with null fields
        assertDoesNotThrow(() -> trainee.equals(new Trainee()));
        assertDoesNotThrow(() -> trainee.hashCode());
    }

    @Test
    void testEqualsWithNullIds() {
        // Given
        Trainee trainee1 = new Trainee(null, "John", "Doe", "john.doe", 
                                        "pass", LocalDate.now(), "Address", true);
        Trainee trainee2 = new Trainee(null, "Jane", "Smith", "john.doe", 
                                        "different", LocalDate.now(), "Other", false);

        // Then - should be equal if usernames match even with null ids
        assertThat(trainee1).isEqualTo(trainee2);
    }

    @Test
    void testEqualsWithNullUsernames() {
        // Given
        Trainee trainee1 = new Trainee(1L, "John", "Doe", null, 
                                        "pass", LocalDate.now(), "Address", true);
        Trainee trainee2 = new Trainee(1L, "Jane", "Smith", null, 
                                        "different", LocalDate.now(), "Other", false);

        // Then - should be equal if ids match even with null usernames
        assertThat(trainee1).isEqualTo(trainee2);
    }
}
