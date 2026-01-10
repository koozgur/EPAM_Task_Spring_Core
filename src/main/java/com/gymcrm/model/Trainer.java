package com.gymcrm.model;

import java.util.Objects;

/**
 * Trainer entity representing a gym instructor/trainer.
 * Contains personal information, credentials, specialization, and activity status.
 */
public class Trainer extends User{

    private String specialization;

    public Trainer() {
    }

    public Trainer(Long userId, String firstName, String lastName, String username,
                   String password, String specialization, Boolean isActive) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.specialization = specialization;
        this.isActive = isActive;
    }
    
    /**
     * Constructor without ID (for creating new trainers)
     */
    public Trainer(String firstName, String lastName, String username, 
                   String password, String specialization, Boolean isActive) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.specialization = specialization;
        this.isActive = isActive;
    }
    
    public String getSpecialization() {
        return specialization;
    }
    
    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trainer trainer = (Trainer) o;
        return Objects.equals(userId, trainer.userId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
    
    @Override
    public String toString() {
        return "Trainer{" +
                "userId=" + this.userId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                ", specialization='" + specialization + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
