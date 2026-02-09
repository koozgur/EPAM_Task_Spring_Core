package com.gymcrm.dao;

import com.gymcrm.model.TrainingType;

import java.util.List;
import java.util.Optional;

/**
 * Read-only DAO for TrainingType lookup values.
 */
public interface TrainingTypeDAO {

    Optional<TrainingType> findById(Long id);

    Optional<TrainingType> findByName(String name);

    List<TrainingType> findAll();
}
