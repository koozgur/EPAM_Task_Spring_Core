package com.gymcrm.dao;

import com.gymcrm.config.AppConfig;
import com.gymcrm.model.Trainee;
import com.gymcrm.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AppConfig.class)
@Transactional
class TraineeDAOImplTest {

    @Autowired
    private TraineeDAO traineeDAO;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void create_ShouldPersistTraineeToDatabase() {
        User user = new User("John", "Doe", "john.doe.trainee", "password123", true);
        Trainee trainee = new Trainee(user, LocalDate.of(1990, 1, 1), "Address 1");

        Trainee created = traineeDAO.create(trainee);
        entityManager.flush();
        entityManager.clear();

        assertThat(created.getId()).isNotNull();

        Trainee found = entityManager.find(Trainee.class, created.getId());
        assertThat(found).isNotNull();
        assertThat(found.getUser().getUsername()).isEqualTo("john.doe.trainee");
    }

    @Test
    void create_ShouldCascadePersistUser() {
        User user = new User("Jane", "Smith", "jane.smith.trainee", "pass456", true);
        Trainee trainee = new Trainee(user, LocalDate.of(1992, 2, 2), "Address 2");

        Trainee created = traineeDAO.create(trainee);
        entityManager.flush();

        assertThat(created.getUser().getId()).isNotNull();

        User foundUser = entityManager.find(User.class, created.getUser().getId());
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUsername()).isEqualTo("jane.smith.trainee");
    }

    @Test
    void update_ShouldMergeTraineeChanges() {
        User user = new User("Update", "Trainee", "update.trainee.user", "pass", true);
        Trainee trainee = new Trainee(user, LocalDate.of(1993, 3, 3), "Old Address");
        entityManager.persist(trainee);
        entityManager.flush();
        entityManager.clear();

        Trainee detached = entityManager.find(Trainee.class, trainee.getId());
        entityManager.detach(detached);
        detached.setAddress("New Address");

        Trainee updated = traineeDAO.update(detached);
        entityManager.flush();
        entityManager.clear();

        Trainee reloaded = entityManager.find(Trainee.class, updated.getId());
        assertThat(reloaded).isNotNull();
        assertThat(reloaded.getAddress()).isEqualTo("New Address");
    }

    @Test
    void delete_ShouldRemoveTrainee() {
        User user = new User("Delete", "Me", "delete.trainee.user", "pass", true);
        Trainee trainee = new Trainee(user, LocalDate.of(1994, 4, 4), "Address 3");
        entityManager.persist(trainee);
        entityManager.flush();
        entityManager.clear();

        traineeDAO.delete(trainee.getId());
        entityManager.flush();
        entityManager.clear();

        Trainee found = entityManager.find(Trainee.class, trainee.getId());
        assertThat(found).isNull();
    }

    @Test
    void delete_ShouldNoopWhenNotFound() {
        assertThatCode(() -> traineeDAO.delete(999999L)).doesNotThrowAnyException();
    }

    @Test
    void delete_ShouldNoopWhenIdNull() {
        assertThatCode(() -> traineeDAO.delete(null)).doesNotThrowAnyException();
    }

    @Test
    void findById_ShouldReturnTraineeWhenExists() {
        User user = new User("Find", "ById", "find.trainee.byid", "pass", true);
        Trainee trainee = new Trainee(user, LocalDate.of(1995, 5, 5), "Address 4");
        entityManager.persist(trainee);
        entityManager.flush();
        entityManager.clear();

        Optional<Trainee> result = traineeDAO.findById(trainee.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getUser().getUsername()).isEqualTo("find.trainee.byid");
    }

    @Test
    void findById_ShouldReturnEmptyWhenIdNull() {
        Optional<Trainee> result = traineeDAO.findById(null);
        assertThat(result).isEmpty();
    }

    @Test
    void findAll_ShouldReturnAllTrainees() {
        User user1 = new User("All", "One", "find.trainee.all1", "pass", true);
        User user2 = new User("All", "Two", "find.trainee.all2", "pass", true);
        Trainee trainee1 = new Trainee(user1, LocalDate.of(1996, 6, 6), "Address 5");
        Trainee trainee2 = new Trainee(user2, LocalDate.of(1997, 7, 7), "Address 6");
        entityManager.persist(trainee1);
        entityManager.persist(trainee2);
        entityManager.flush();
        entityManager.clear();

        List<Trainee> result = traineeDAO.findAll();

        assertThat(result).extracting(Trainee::getUser)
                .extracting(User::getUsername)
                .contains("find.trainee.all1", "find.trainee.all2");
    }

    @Test
    void findByUsername_ShouldReturnTraineeWhenExists() {
        User user = new User("Find", "ByUsername", "find.trainee.byusername", "pass", true);
        Trainee trainee = new Trainee(user, LocalDate.of(1998, 8, 8), "Address 7");
        entityManager.persist(trainee);
        entityManager.flush();
        entityManager.clear();

        Optional<Trainee> result = traineeDAO.findByUsername("find.trainee.byusername");

        assertThat(result).isPresent();
        assertThat(result.get().getUser().getUsername()).isEqualTo("find.trainee.byusername");
    }

    @Test
    void findByUsername_ShouldReturnEmptyWhenNotFound() {
        Optional<Trainee> result = traineeDAO.findByUsername("missing.trainee");
        assertThat(result).isEmpty();
    }

    @Test
    void findByUsername_ShouldReturnEmptyWhenNull() {
        Optional<Trainee> result = traineeDAO.findByUsername(null);
        assertThat(result).isEmpty();
    }
}
