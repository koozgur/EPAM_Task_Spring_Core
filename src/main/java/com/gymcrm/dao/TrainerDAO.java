package com.gymcrm.dao;

import com.gymcrm.model.Trainer;

import java.util.List;
import java.util.Optional;

public interface TrainerDAO {

    Trainer create(Trainer trainer);

    Trainer update(Trainer trainer);

    Optional<Trainer> findById(Long id);

    List<Trainer> findAll();

    Optional<Trainer> findByUsername(String username);

    List<Trainer> findUnassignedTrainersByTraineeUsername(String traineeUsername);

    Optional<Trainer> findByUsernameWithTrainees(String username);
}
