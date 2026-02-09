package com.gymcrm.dao;

import com.gymcrm.model.Trainee;

import java.util.List;
import java.util.Optional;

public interface TraineeDAO {

    Trainee create(Trainee trainee);

    Trainee update(Trainee trainee);

    void delete(Long id);

    Optional<Trainee> findById(Long id);

    List<Trainee> findAll();

    Optional<Trainee> findByUsername(String username);
}
