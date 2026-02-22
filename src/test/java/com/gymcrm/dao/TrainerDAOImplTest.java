package com.gymcrm.dao;

import com.gymcrm.config.AppConfig;
import com.gymcrm.model.Trainer;
import com.gymcrm.model.TrainingType;
import com.gymcrm.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AppConfig.class)
@Transactional
class TrainerDAOImplTest {

    @Autowired
    private TrainerDAO trainerDAO;

    @PersistenceContext
    private EntityManager entityManager;

    private TrainingType testTrainingType;

    @BeforeEach
    void setUp() {
        //create trainingType if not exists
        testTrainingType = entityManager
                .createQuery(
                        "select t from TrainingType t where t.trainingTypeName = :name",
                        TrainingType.class)
                .setParameter("name", "Fitness")
                .getResultStream()
                .findFirst()
                .orElseGet(() -> {
                    TrainingType created = new TrainingType("Fitness");
                    entityManager.persist(created);
                    return created;
                });
        entityManager.flush();
    }

    @Test
    void create_ShouldPersistTrainerToDatabase() {
        User user = new User("John", "Doe", "john.doe.create", "password123", true);
        Trainer trainer = new Trainer(user, testTrainingType);

        Trainer createdTrainer = trainerDAO.create(trainer);
        entityManager.flush();
        entityManager.clear();

        assertThat(createdTrainer.getId()).isNotNull();

        Trainer foundTrainer = entityManager.find(Trainer.class, createdTrainer.getId());
        assertThat(foundTrainer).isNotNull();
        assertThat(foundTrainer.getUser().getUsername()).isEqualTo("john.doe.create");
    }

    @Test
    void create_ShouldCascadePersistUser() {
        User user = new User("Jane", "Smith", "jane.smith.create", "pass456", true);
        Trainer trainer = new Trainer(user, testTrainingType);

        Trainer createdTrainer = trainerDAO.create(trainer);
        entityManager.flush();

        assertThat(createdTrainer.getUser().getId()).isNotNull();

        User foundUser = entityManager.find(User.class, createdTrainer.getUser().getId());
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUsername()).isEqualTo("jane.smith.create");
        assertThat(foundUser.getFirstName()).isEqualTo("Jane");
        assertThat(foundUser.getLastName()).isEqualTo("Smith");
    }

    @Test
    void update_ShouldMergeTrainerChanges() {
        User user = new User("Update", "Test", "update.test.user", "pass", true);
        Trainer trainer = new Trainer(user, testTrainingType);
        entityManager.persist(trainer);
        entityManager.flush();
        entityManager.clear();

        Trainer detached = entityManager.find(Trainer.class, trainer.getId());
        entityManager.detach(detached); //to specifically test the merge behavior of update
        detached.getUser().setLastName("Updated");

        Trainer updated = trainerDAO.update(detached);
        entityManager.flush();
        entityManager.clear();

        Trainer reloaded = entityManager.find(Trainer.class, updated.getId());
        assertThat(reloaded).isNotNull();
        assertThat(reloaded.getUser().getLastName()).isEqualTo("Updated");
    }

    @Test
    void findById_ShouldReturnTrainerWhenExists() {
        User user = new User("Find", "ById", "find.byid.user", "pass", true);
        Trainer trainer = new Trainer(user, testTrainingType);
        entityManager.persist(trainer);
        entityManager.flush();
        entityManager.clear();

        Optional<Trainer> result = trainerDAO.findById(trainer.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getUser().getUsername()).isEqualTo("find.byid.user");
    }

    @Test
    void findById_ShouldReturnEmptyWhenIdNull() {
        Optional<Trainer> result = trainerDAO.findById(null);
        assertThat(result).isEmpty();
    }

    @Test
    void findAll_ShouldReturnAllTrainers() {
        User user1 = new User("All", "One", "find.all.user1", "pass", true);
        User user2 = new User("All", "Two", "find.all.user2", "pass", true);
        Trainer trainer1 = new Trainer(user1, testTrainingType);
        Trainer trainer2 = new Trainer(user2, testTrainingType);
        entityManager.persist(trainer1);
        entityManager.persist(trainer2);
        entityManager.flush();
        entityManager.clear();

        List<Trainer> result = trainerDAO.findAll();

        assertThat(result).extracting(Trainer::getUser)
                .extracting(User::getUsername)
                .contains("find.all.user1", "find.all.user2");
    }

    @Test
    void findByUsername_ShouldReturnTrainerWhenExists() {
        User user = new User("Find", "ByUsername", "find.byusername.user", "pass", true);
        Trainer trainer = new Trainer(user, testTrainingType);
        entityManager.persist(trainer);
        entityManager.flush();
        entityManager.clear();

        Optional<Trainer> result = trainerDAO.findByUsername("find.byusername.user");

        assertThat(result).isPresent();
        assertThat(result.get().getUser().getUsername()).isEqualTo("find.byusername.user");
    }

    @Test
    void findByUsername_ShouldReturnEmptyWhenNotFound() {
        Optional<Trainer> result = trainerDAO.findByUsername("missing.user");
        assertThat(result).isEmpty();
    }

    @Test
    void findByUsername_ShouldReturnEmptyWhenNull() {
        Optional<Trainer> result = trainerDAO.findByUsername(null);
        assertThat(result).isEmpty();
    }
}
