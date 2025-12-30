package com.gymcrm.model;

import java.util.Objects;

/**
 * Trainer entity representing a gym instructor/trainer.
 * Contains personal information, credentials, specialization, and activity status.
 */
public class Trainer {
    
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String specialization;
    private Boolean isActive;

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

    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getSpecialization() {
        return specialization;
    }
    
    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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
