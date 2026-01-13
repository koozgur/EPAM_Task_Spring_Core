package com.gymcrm.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Trainee entity representing a gym client/trainee.
 * Contains personal information, credentials, and activity status.
 */
public class Trainee extends User {

    private LocalDate dateOfBirth;
    private String address;

    public Trainee() {
    }

    public Trainee(Long userId, String firstName, String lastName, String username,
                   String password, LocalDate dateOfBirth, String address, Boolean isActive) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.isActive = isActive;
    }
    
    /**
     * Constructor without ID (for creating new trainees)
     */
    public Trainee(String firstName, String lastName, String username, 
                   String password, LocalDate dateOfBirth, String address, Boolean isActive) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.isActive = isActive;
    }
    
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
    
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trainee trainee = (Trainee) o;
        return Objects.equals(userId, trainee.userId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
    
    @Override
    public String toString() {
        return "Trainee{" +
                "userId=" + userId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", address='" + address + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
