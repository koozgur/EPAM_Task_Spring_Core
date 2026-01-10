package com.gymcrm.model;

import java.util.Objects;

/**
 * Trainer entity representing a gym instructor/trainer.
 * Contains personal information, credentials, specialization, and activity status.
 */
public class Trainer extends User{

    public Long userId;
    private String specialization;

    public Trainer() {
    }

    public Trainer(Long id, String firstName, String lastName, String username, 
                   String password, String specialization, Boolean isActive) {
        this.id = id;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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
        return Objects.equals(id, trainer.id) && 
               Objects.equals(username, trainer.username);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }
    
    @Override
    public String toString() {
        return "Trainer{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                ", specialization='" + specialization + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
