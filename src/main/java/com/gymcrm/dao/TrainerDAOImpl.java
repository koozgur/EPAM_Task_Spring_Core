package com.gymcrm.dao;

import com.gymcrm.model.Trainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

/**
 * JPA implementation of TrainerDAO for managing Trainer entities.
 */
@Repository
public class TrainerDAOImpl implements TrainerDAO {
    //DAO logs technical persistence facts, not business events.
    private static final Logger logger = LoggerFactory.getLogger(TrainerDAOImpl.class);
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    public Trainer create(Trainer trainer) {
        entityManager.persist(trainer);
        
        logger.info("Persisted trainer with id: {} and username: {}",
                trainer.getId(), trainer.getUser().getUsername());
        return trainer;
    }
    
    @Override
    public Trainer update(Trainer trainer) {
        Trainer merged = entityManager.merge(trainer);

        logger.info("Updated trainer with id: {} and username: {}",
                merged.getId(), merged.getUser() != null ? merged.getUser().getUsername() : null);
        return merged;
    }
    
    @Override
    public Optional<Trainer> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return entityManager
                .createQuery(
                        "select t from Trainer t left join fetch t.trainees left join fetch t.trainings where t.id = :id",
                        Trainer.class)
                .setParameter("id", id)
                .getResultStream()
                .findFirst();
    }
    
    @Override
    public List<Trainer> findAll() {
        return entityManager
                .createQuery("select distinct t from Trainer t left join fetch t.trainees left join fetch t.trainings", Trainer.class)
                .getResultList();
    }
    
    @Override
    public Optional<Trainer> findByUsername(String username) {
        if (username == null) {
            return Optional.empty();
        }
        return entityManager
                .createQuery(
                        "select t from Trainer t where t.user.username = :username",
                        Trainer.class)
                .setParameter("username", username)
                .getResultStream()
                .findFirst();
    }
    @Override
    public List<Trainer> findUnassignedTrainersByTraineeUsername(String traineeUsername) {
        if (traineeUsername == null) {
            return List.of();
        }

        return entityManager
                .createQuery(
                        """
                        select tr
                        from Trainer tr
                        where not exists (
                            select 1
                            from Trainee tn
                            join tn.trainers assigned
                            where tn.user.username = :traineeUsername
                            and assigned = tr
                        )
                        """,
                        Trainer.class)
                .setParameter("traineeUsername", traineeUsername)
                .getResultList();
    }

    @Override
    public Optional<Trainer> findByUsernameWithTrainees(String username) {
        return entityManager.createQuery("""
        select distinct t
        from Trainer t
        left join fetch t.trainees tr
        where t.user.username = :username
        """, Trainer.class)
                .setParameter("username", username)
                .getResultStream()
                .findFirst();
    }
}
