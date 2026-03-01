package com.gymcrm.dao;

import com.gymcrm.model.Trainee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class TraineeDAOImpl implements TraineeDAO {

    private static final Logger logger = LoggerFactory.getLogger(TraineeDAOImpl.class);

    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    public Trainee create(Trainee trainee) {
        entityManager.persist(trainee);

        logger.info("Persisted trainee with id: {} and username: {}",
                trainee.getId(),
                trainee.getUser() != null ? trainee.getUser().getUsername() : null);
        return trainee;
    }
    
    @Override
    public Trainee update(Trainee trainee) {
        Trainee merged = entityManager.merge(trainee);

        logger.info("Updated trainee with id: {} and username: {}",
                merged.getId(),
                merged.getUser() != null ? merged.getUser().getUsername() : null);
        return merged;
    }
    
    @Override
    public void delete(Long id) {
        if (id == null) {
            return;
        }

        Trainee trainee = entityManager.find(Trainee.class, id);
        if (trainee != null) {
            entityManager.remove(trainee);
            logger.info("Deleted trainee with id: {}", id);
        }
    }

    @Override
    public Optional<Trainee> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return entityManager
                .createQuery(
                "select t from Trainee t left join fetch t.trainers where t.id = :id",
                        Trainee.class)
                .setParameter("id", id)
                .getResultStream()
                .findFirst();
    }

    @Override
    public List<Trainee> findAll() {
        return entityManager
                .createQuery("select distinct t from Trainee t left join fetch t.trainers", Trainee.class)
                .getResultList();
    }
    
    @Override
    public Optional<Trainee> findByUsername(String username) {
        if (username == null) {
            return Optional.empty();
        }
        return entityManager
                .createQuery(
                "select t from Trainee t where t.user.username = :username",
                        Trainee.class)
                .setParameter("username", username)
                .getResultStream()
                .findFirst();
    }
}
