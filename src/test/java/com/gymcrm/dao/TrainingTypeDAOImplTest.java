package com.gymcrm.dao;

import com.gymcrm.config.AppConfig;
import com.gymcrm.model.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AppConfig.class)
@Transactional
class TrainingTypeDAOImplTest {

    @Autowired
    private TrainingTypeDAO trainingTypeDAO;

    @PersistenceContext
    private EntityManager entityManager;

    private TrainingType existingType;

    @BeforeEach
    void setUp() {
        existingType = getOrCreate("Pilates");
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void findById_ShouldReturnWhenExists() {
        Optional<TrainingType> result = trainingTypeDAO.findById(existingType.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getTrainingTypeName()).isEqualTo("Pilates");
    }

    @Test
    void findById_ShouldReturnEmptyWhenNull() {
        Optional<TrainingType> result = trainingTypeDAO.findById(null);
        assertThat(result).isEmpty();
    }

    @Test
    void findByName_ShouldReturnWhenExists() {
        Optional<TrainingType> result = trainingTypeDAO.findByName("Pilates");

        assertThat(result).isPresent();
        assertThat(result.get().getTrainingTypeName()).isEqualTo("Pilates");
    }

    @Test
    void findByName_ShouldReturnEmptyWhenMissing() {
        Optional<TrainingType> result = trainingTypeDAO.findByName("MissingType");
        assertThat(result).isEmpty();
    }

    @Test
    void findByName_ShouldReturnEmptyWhenNull() {
        Optional<TrainingType> result = trainingTypeDAO.findByName(null);
        assertThat(result).isEmpty();
    }

    @Test
    void findAll_ShouldReturnList() {
        List<TrainingType> result = trainingTypeDAO.findAll();

        assertThat(result).isNotEmpty();
        assertThat(result).extracting(TrainingType::getTrainingTypeName)
                .contains("Pilates");
    }

    private TrainingType getOrCreate(String name) {
        return entityManager
                .createQuery(
                        "select t from TrainingType t where t.trainingTypeName = :name",
                        TrainingType.class)
                .setParameter("name", name)
                .getResultStream()
                .findFirst()
                .orElseGet(() -> {
                    TrainingType created = new TrainingType(name);
                    entityManager.persist(created);
                    return created;
                });
    }
}
