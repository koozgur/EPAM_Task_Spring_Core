package com.gymcrm.workload.repository;

import com.gymcrm.workload.entity.TrainerWorkloadEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrainerWorkloadRepository extends JpaRepository<TrainerWorkloadEntry, Long> {

    Optional<TrainerWorkloadEntry> findByTrainerUsernameAndYearAndMonth(
            String trainerUsername, Integer year, Integer month);

    List<TrainerWorkloadEntry> findByTrainerUsername(String trainerUsername);
}
