package com.gymcrm.workload.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;

/**
 * Stores aggregated training minutes per trainer per (year, month).
 * One row per (trainerUsername, year, month) — enforced by unique constraint.
 * In-memory H2 database
 */
@Entity
@Table(
    name = "trainer_workload",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_trainer_training_year_month",
        columnNames = {"trainer_username", "training_year", "training_month"}
    )
)
public class TrainerWorkloadEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trainer_username", nullable = false)
    private String trainerUsername;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "training_year", nullable = false)
    private Integer year;

    @Column(name = "training_month", nullable = false)
    private Integer month;

    @Column(name = "total_minutes", nullable = false)
    private Integer totalMinutes = 0;

    @Version
    @Column(name = "row_version", nullable = false)
    private Long version;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTrainerUsername() { return trainerUsername; }
    public void setTrainerUsername(String trainerUsername) { this.trainerUsername = trainerUsername; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public Integer getMonth() { return month; }
    public void setMonth(Integer month) { this.month = month; }

    public Integer getTotalMinutes() { return totalMinutes; }
    public void setTotalMinutes(Integer totalMinutes) { this.totalMinutes = totalMinutes; }
}
