package com.gymcrm.workload.repository;

import com.gymcrm.workload.document.TrainerWorkloadDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data MongoDB repository for trainer workload documents.
 */
@Repository
public interface TrainerWorkloadDocumentRepository
        extends MongoRepository<TrainerWorkloadDocument, String> {

    Optional<TrainerWorkloadDocument> findByTrainerUsername(String trainerUsername);
}
