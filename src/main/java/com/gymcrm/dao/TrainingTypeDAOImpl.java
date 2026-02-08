package com.gymcrm.dao;

import com.gymcrm.model.TrainingType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class TrainingTypeDAOImpl implements TrainingTypeDAO {

    private static final Logger logger = LoggerFactory.getLogger(TrainingTypeDAOImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<TrainingType> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(entityManager.find(TrainingType.class, id));
    }

    @Override
    public Optional<TrainingType> findByName(String name) {
        if (name == null) {
            return Optional.empty();
        }
        return entityManager
                .createQuery(
                        "select t from TrainingType t where t.trainingTypeName = :name",
                        TrainingType.class)
                .setParameter("name", name)
                .getResultStream()
                .findFirst();
    }

    @Override
    public List<TrainingType> findAll() {
        List<TrainingType> results = entityManager
                .createQuery("select t from TrainingType t", TrainingType.class)
                .getResultList();
        logger.info("Fetched {} training types", results.size());
        return results;
    }
}
