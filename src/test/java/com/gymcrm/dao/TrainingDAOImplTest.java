package com.gymcrm.dao;

import com.gymcrm.config.AppConfig;
import com.gymcrm.model.Trainee;
import com.gymcrm.model.Trainer;
import com.gymcrm.model.Training;
import com.gymcrm.model.TrainingType;
import com.gymcrm.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AppConfig.class)
@Transactional
class TrainingDAOImplTest {

    @Autowired
    private TrainingDAO trainingDAO;

    @PersistenceContext
    private EntityManager entityManager;

    //helpers
    private TrainingType getOrCreateType(String name) {
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

    private Trainee createTrainee(String username) {
        return createTraineeWithName(username, "Trainee", "User");
    }

    private Trainee createTraineeWithName(String username, String firstName, String lastName) {
        User user = new User(firstName, lastName, username, "pass", true);
        Trainee trainee = new Trainee(user, LocalDate.of(1995, 1, 1), "Address");
        entityManager.persist(trainee);
        return trainee;
    }

    private Trainer createTrainer(String username, String firstName) {
        TrainingType type = getOrCreateType("Cardio");
        User user = new User(firstName, "Trainer", username, "pass", true);
        Trainer trainer = new Trainer(user, type);
        entityManager.persist(trainer);
        return trainer;
    }

    //tests
    @BeforeEach
    void setUp() {
                getOrCreateType("Cardio");
                getOrCreateType("Strength");
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void create_ShouldPersistTraining() {
        Trainee trainee = createTrainee("train.create.trainee");
        Trainer trainer = createTrainer("train.create.trainer", "Coach");
        TrainingType cardio = getOrCreateType("Cardio");

        Training training = new Training(
                trainee,
                trainer,
                "Cardio Session",
                cardio,
                LocalDate.of(2024, 1, 10),
                60);

        Training created = trainingDAO.create(training);
        entityManager.flush();
        entityManager.clear();

        assertThat(created.getId()).isNotNull();
        Training found = entityManager.find(Training.class, created.getId());
        assertThat(found).isNotNull();
        assertThat(found.getTrainingName()).isEqualTo("Cardio Session");
    }

    @Test
    void findById_ShouldReturnWhenExists() {
        Trainee trainee = createTrainee("train.find.id.trainee");
        Trainer trainer = createTrainer("train.find.id.trainer", "Coach");
        TrainingType cardio = getOrCreateType("Cardio");
        Training training = new Training(
                trainee,
                trainer,
                "Find Session",
                cardio,
                LocalDate.of(2024, 2, 1),
                45);
        entityManager.persist(training);
        entityManager.flush();
        entityManager.clear();

        Optional<Training> result = trainingDAO.findById(training.getId());
        assertThat(result).isPresent();
        assertThat(result.get().getTrainingName()).isEqualTo("Find Session");
    }

    @Test
    void findById_ShouldReturnEmptyWhenNull() {
        Optional<Training> result = trainingDAO.findById(null);
        assertThat(result).isEmpty();
    }

    @Test
    void findAll_ShouldReturnAllTrainings() {
        Trainee trainee = createTrainee("train.find.all.trainee");
        Trainer trainer = createTrainer("train.find.all.trainer", "Coach");
        TrainingType cardio = getOrCreateType("Cardio");
        TrainingType strength = getOrCreateType("Strength");
        Training t1 = new Training(trainee, trainer, "Session 1", cardio,
                LocalDate.of(2024, 3, 1), 30);
        Training t2 = new Training(trainee, trainer, "Session 2", strength,
                LocalDate.of(2024, 3, 2), 40);
        entityManager.persist(t1);
        entityManager.persist(t2);
        entityManager.flush();
        entityManager.clear();

        List<Training> result = trainingDAO.findAll();
        assertThat(result).extracting(Training::getTrainingName)
                .contains("Session 1", "Session 2");
    }

    @Test
    void findByTraineeId_ShouldReturnTrainings() {
        Trainee trainee = createTrainee("train.by.trainee.id");
        Trainer trainer = createTrainer("train.by.trainee.trainer", "Coach");
        TrainingType cardio = getOrCreateType("Cardio");
        TrainingType strength = getOrCreateType("Strength");
        Training t1 = new Training(trainee, trainer, "Trainee A", cardio,
                LocalDate.of(2024, 4, 1), 30);
        Training t2 = new Training(trainee, trainer, "Trainee B", strength,
                LocalDate.of(2024, 4, 2), 40);
        entityManager.persist(t1);
        entityManager.persist(t2);
        entityManager.flush();
        entityManager.clear();

        List<Training> result = trainingDAO.findByTraineeId(trainee.getId());
        assertThat(result).hasSize(2);
    }

    @Test
    void findByTraineeId_ShouldReturnEmptyWhenNull() {
        List<Training> result = trainingDAO.findByTraineeId(null);
        assertThat(result).isEmpty();
    }

    @Test
    void findByTrainerId_ShouldReturnTrainings() {
        Trainee trainee = createTrainee("train.by.trainer.trainee");
        Trainer trainer = createTrainer("train.by.trainer.id", "Coach");
        TrainingType cardio = getOrCreateType("Cardio");
        TrainingType strength = getOrCreateType("Strength");
        Training t1 = new Training(trainee, trainer, "Trainer A", cardio,
                LocalDate.of(2024, 5, 1), 30);
        Training t2 = new Training(trainee, trainer, "Trainer B", strength,
                LocalDate.of(2024, 5, 2), 40);
        entityManager.persist(t1);
        entityManager.persist(t2);
        entityManager.flush();
        entityManager.clear();

        List<Training> result = trainingDAO.findByTrainerId(trainer.getId());
        assertThat(result).hasSize(2);
    }

    @Test
    void findByTrainerId_ShouldReturnEmptyWhenNull() {
        List<Training> result = trainingDAO.findByTrainerId(null);
        assertThat(result).isEmpty();
    }

    @Test
    void findByTraineeUsernameAndCriteria_ShouldFilterByDateTrainerAndType() {
        Trainee trainee = createTrainee("trainee.criteria.user");
        Trainer trainer1 = createTrainer("trainer.criteria.user", "Alex");
        Trainer trainer2 = createTrainer("trainer.other.user", "Other");
        TrainingType cardio = getOrCreateType("Cardio");
        TrainingType strength = getOrCreateType("Strength");

        Training matching = new Training(trainee, trainer1, "Match", cardio,
                LocalDate.of(2024, 6, 10), 50);
        Training outOfDate = new Training(trainee, trainer1, "Old", cardio,
                LocalDate.of(2023, 12, 31), 30);
        Training wrongTrainer = new Training(trainee, trainer2, "Wrong Trainer", cardio,
                LocalDate.of(2024, 6, 12), 30);
        Training wrongType = new Training(trainee, trainer1, "Wrong Type", strength,
                LocalDate.of(2024, 6, 12), 30);

        entityManager.persist(matching);
        entityManager.persist(outOfDate);
        entityManager.persist(wrongTrainer);
        entityManager.persist(wrongType);
        entityManager.flush();
        entityManager.clear();

        List<Training> result = trainingDAO.findByTraineeUsernameAndCriteria(
                "trainee.criteria.user",
                LocalDate.of(2024, 6, 1),
                LocalDate.of(2024, 6, 30),
                "alex",
                "Cardio");

        assertThat(result).extracting(Training::getTrainingName)
                .containsExactly("Match");
    }

    @Test
    void findByTrainerUsernameAndCriteria_ShouldFilterByDateAndTraineeName() {
        Trainee trainee1 = createTrainee("trainee.criteria.one");
        Trainee trainee2 = createTraineeWithName("trainee.criteria.two", "Sara", "Other");
        Trainer trainer = createTrainer("trainer.criteria.main", "Coach");
        TrainingType cardio = getOrCreateType("Cardio");

        Training matching = new Training(trainee1, trainer, "Match", cardio,
                LocalDate.of(2024, 7, 10), 50);
        Training wrongTrainee = new Training(trainee2, trainer, "Wrong Trainee", cardio,
                LocalDate.of(2024, 7, 11), 50);
        Training outOfDate = new Training(trainee1, trainer, "Old", cardio,
                LocalDate.of(2023, 7, 11), 50);

        entityManager.persist(matching);
        entityManager.persist(wrongTrainee);
        entityManager.persist(outOfDate);
        entityManager.flush();
        entityManager.clear();

        List<Training> result = trainingDAO.findByTrainerUsernameAndCriteria(
                "trainer.criteria.main",
                LocalDate.of(2024, 7, 1),
                LocalDate.of(2024, 7, 31),
                "Trainee");

        assertThat(result).extracting(Training::getTrainingName)
                .containsExactly("Match");
    }
}
