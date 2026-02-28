package com.gymcrm.service;

import com.gymcrm.model.Trainer;

import java.util.List;
import java.util.Optional;

public interface TrainerService {

    Trainer createTrainer(Trainer trainer);

    Optional<Trainer> getTrainerByUsername(String username);

    Trainer updateTrainer(Trainer trainer);

    void activateTrainer(String username);

    void deactivateTrainer(String username);

    List<Trainer> getUnassignedTrainersByTraineeUsername(String traineeUsername);

    Optional<Trainer> getTrainer(Long id);

    List<Trainer> getAllTrainers();
}
