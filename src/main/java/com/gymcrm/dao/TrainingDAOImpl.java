package com.gymcrm.dao;

import com.gymcrm.model.Training;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class TrainingDAOImpl implements TrainingDAO {
    
    private static final Logger logger = LoggerFactory.getLogger(TrainingDAOImpl.class);

    private static final String BASE_QUERY =
            "select t from Training t " +
                    "join t.trainee tr " +
                    "join tr.user tru " +
                    "join t.trainer te " +
                    "join te.user teu " +
                    "join t.trainingType tt " +
                    "where 1=1";



    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    public Training create(Training training) {
        entityManager.persist(training);
        logger.info("Persisted training with id: {}", training.getId());
        return training;
    }
    
    @Override
    public Optional<Training> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(entityManager.find(Training.class, id));
    }
    
    @Override
    public List<Training> findAll() {
        return entityManager
                .createQuery("select t from Training t", Training.class)
                .getResultList();
    }
    
    @Override
    public List<Training> findByTraineeId(Long traineeId) {
        if (traineeId == null) {
            return List.of();
        }
        return entityManager
                .createQuery(
                        "select t from Training t where t.trainee.id = :traineeId",
                        Training.class)
                .setParameter("traineeId", traineeId)
                .getResultList();
    }
    
    @Override
    public List<Training> findByTrainerId(Long trainerId) {
        if (trainerId == null) {
            return List.of();
        }
        return entityManager
                .createQuery(
                        "select t from Training t where t.trainer.id = :trainerId",
                        Training.class)
                .setParameter("trainerId", trainerId)
                .getResultList();
    }

    @Override
    public List<Training> findByTraineeUsernameAndCriteria(
            String traineeUsername,
            LocalDate fromDate,
            LocalDate toDate,
            String trainerName,
            String trainingTypeName) {

        StringBuilder jpql = new StringBuilder(BASE_QUERY);
        Map<String, Object> params = new HashMap<>();

        appendTraineeUsernameFilter(jpql, traineeUsername, params);
        appendDateFilters(jpql, fromDate, toDate, params);
        appendTrainerNameFilter(jpql, trainerName, params);
        appendTrainingTypeFilter(jpql, trainingTypeName, params);

        TypedQuery<Training> query = entityManager.createQuery(jpql.toString(), Training.class);

        params.forEach(query::setParameter);

        logger.debug("Executing JPQL: {}", jpql);
        return query.getResultList();
    }

    @Override
    public List<Training> findByTrainerUsernameAndCriteria(
            String trainerUsername,
            LocalDate fromDate,
            LocalDate toDate,
            String traineeName) {

        StringBuilder jpql = new StringBuilder(BASE_QUERY);
        Map<String, Object> params = new HashMap<>();

        appendTrainerUsernameFilter(jpql, trainerUsername, params);
        appendDateFilters(jpql, fromDate, toDate, params);
        appendTraineeNameFilter(jpql, traineeName, params);

        TypedQuery<Training> query = entityManager.createQuery(jpql.toString(), Training.class);

        params.forEach(query::setParameter);

        logger.debug("Executing JPQL: {}", jpql);
        return query.getResultList();
    }


    private void appendDateFilters(
            StringBuilder jpql,
            LocalDate fromDate,
            LocalDate toDate,
            Map<String, Object> params) {

        if (fromDate != null) {
            jpql.append(" and t.trainingDate >= :fromDate");
            params.put("fromDate", fromDate);
        }
        if (toDate != null) {
            jpql.append(" and t.trainingDate <= :toDate");
            params.put("toDate", toDate);
        }
    }

    private void appendTraineeUsernameFilter(
            StringBuilder jpql,
            String traineeUsername,
            Map<String, Object> params) {

        jpql.append(" and tru.username = :traineeUsername");
        params.put("traineeUsername", traineeUsername);
    }

    private void appendTrainerUsernameFilter(
            StringBuilder jpql,
            String trainerUsername,
            Map<String, Object> params) {

        jpql.append(" and teu.username = :trainerUsername");
        params.put("trainerUsername", trainerUsername);
    }

    private void appendTrainerNameFilter(
            StringBuilder jpql,
            String trainerName,
            Map<String, Object> params) {

        if (trainerName == null || trainerName.isBlank()) {
            return;
        }

        jpql.append(
                " and (lower(teu.firstName) like :trainerName " +
                        "or lower(teu.lastName) like :trainerName)"
        );

        params.put("trainerName", "%" + trainerName.toLowerCase() + "%");
    }


    private void appendTraineeNameFilter(
            StringBuilder jpql,
            String traineeName,
            Map<String, Object> params) {

        if (traineeName == null || traineeName.isBlank()) {
            return;
        }

        jpql.append(
                " and (lower(tru.firstName) like :traineeName " +
                        "or lower(tru.lastName) like :traineeName)"
        );

        params.put("traineeName", "%" + traineeName.toLowerCase() + "%");
    }


    private void appendTrainingTypeFilter(
            StringBuilder jpql,
            String trainingTypeName,
            Map<String, Object> params) {

        if (trainingTypeName == null || trainingTypeName.isBlank()) {
            return;
        }

        jpql.append(" and lower(tt.trainingTypeName) = :trainingTypeName");
        params.put("trainingTypeName", trainingTypeName.toLowerCase());
    }

}
