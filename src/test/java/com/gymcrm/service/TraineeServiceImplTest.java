package com.gymcrm.service;

import com.gymcrm.dao.TraineeDAO;
import com.gymcrm.dao.TrainerDAO;
import com.gymcrm.model.Trainee;
import com.gymcrm.model.Trainer;
import com.gymcrm.model.User;
import com.gymcrm.util.CredentialsGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TraineeServiceImpl Tests")
class TraineeServiceImplTest {

    @Mock
    private TraineeDAO traineeDAO;

    @Mock
    private TrainerDAO trainerDAO;

    @Mock
    private CredentialsGenerator credentialsGenerator;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    private User testUser;
    private Trainee testTrainee;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setUsername("John.Doe");
        testUser.setPassword("password123");
        testUser.setIsActive(true);

        testTrainee = new Trainee();
        testTrainee.setId(1L);
        testTrainee.setUser(testUser);
        testTrainee.setDateOfBirth(LocalDate.of(1990, 1, 1));
        testTrainee.setAddress("123 Main St");
    }

    @Test
    @DisplayName("createTrainee: generates credentials and persists")
    void createTrainee_success() {
        Trainee newTrainee = new Trainee();
        User newUser = new User();
        newUser.setFirstName("Jane");
        newUser.setLastName("Smith");
        newTrainee.setUser(newUser);

        when(credentialsGenerator.generateUsername("Jane", "Smith")).thenReturn("Jane.Smith");
        when(credentialsGenerator.generatePassword()).thenReturn("randomPass10");
        when(traineeDAO.create(any(Trainee.class))).thenAnswer(inv -> {
            Trainee t = inv.getArgument(0);
            t.setId(2L);
            return t;
        });

        Trainee result = traineeService.createTrainee(newTrainee);

        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals("Jane.Smith", result.getUser().getUsername());
        assertEquals("randomPass10", result.getUser().getPassword());
        assertTrue(result.getUser().getIsActive());
        verify(credentialsGenerator).generateUsername("Jane", "Smith");
        verify(credentialsGenerator).generatePassword();
        verify(traineeDAO).create(newTrainee);
    }

    @Test
    @DisplayName("createTrainee: null trainee rejected")
    void createTrainee_nullTrainee() {
        assertThrows(IllegalArgumentException.class,
                () -> traineeService.createTrainee(null));
        verifyNoInteractions(traineeDAO);
    }

    @Test
    @DisplayName("createTrainee: blank first name rejected")
    void createTrainee_blankFirstName() {
        User user = new User();
        user.setFirstName("  ");
        user.setLastName("Doe");
        Trainee trainee = new Trainee();
        trainee.setUser(user);

        assertThrows(IllegalArgumentException.class,
                () -> traineeService.createTrainee(trainee));
        verifyNoInteractions(traineeDAO);
    }

    @Test
    @DisplayName("updateTrainee: updates existing record")
    void updateTrainee_success() {
        User updatedUser = new User();
        updatedUser.setFirstName("Johnny");
        updatedUser.setLastName("Doe");
        updatedUser.setUsername("John.Doe");
        updatedUser.setIsActive(false);
        Trainee incoming = new Trainee();
        incoming.setUser(updatedUser);
        incoming.setDateOfBirth(LocalDate.of(1991, 5, 15));
        incoming.setAddress("456 Oak Ave");

        when(traineeDAO.findByUsername("John.Doe")).thenReturn(Optional.of(testTrainee));
        when(traineeDAO.update(testTrainee)).thenReturn(testTrainee);

        Trainee result = traineeService.updateTrainee(incoming);

        assertNotNull(result);
        assertEquals("Johnny", testTrainee.getUser().getFirstName());
        assertFalse(testTrainee.getUser().getIsActive());
        assertEquals(LocalDate.of(1991, 5, 15), testTrainee.getDateOfBirth());
        assertEquals("456 Oak Ave", testTrainee.getAddress());
        verify(traineeDAO).update(testTrainee);
    }

    @Test
    @DisplayName("updateTrainee: not found by username")
    void updateTrainee_notFound() {
        User user = new User();
        user.setFirstName("Jane");
        user.setLastName("Doe");
        user.setUsername("nonexistent");
        Trainee incoming = new Trainee();
        incoming.setUser(user);

        when(traineeDAO.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> traineeService.updateTrainee(incoming));
        verify(traineeDAO, never()).update(any());
    }

    @Test
    @DisplayName("deleteTraineeByUsername: deletes by username")
    void deleteTraineeByUsername_success() {
        when(traineeDAO.findByUsername("John.Doe")).thenReturn(Optional.of(testTrainee));
        doNothing().when(traineeDAO).delete(1L);

        traineeService.deleteTraineeByUsername("John.Doe");

        verify(traineeDAO).findByUsername("John.Doe");
        verify(traineeDAO).delete(1L);
    }

    @Test
    @DisplayName("deleteTraineeByUsername: null username rejected")
    void deleteTraineeByUsername_nullUsername() {
        assertThrows(IllegalArgumentException.class,
                () -> traineeService.deleteTraineeByUsername(null));
        verify(traineeDAO, never()).delete(anyLong());
    }

    @Test
    @DisplayName("getTrainee: returns trainee when found")
    void getTrainee_found() {
        when(traineeDAO.findById(1L)).thenReturn(Optional.of(testTrainee));

        Optional<Trainee> result = traineeService.getTrainee(1L);

        assertTrue(result.isPresent());
        assertEquals(testTrainee, result.get());
    }

    @Test
    @DisplayName("authenticate: returns true on match")
    void authenticate_success() {
        when(traineeDAO.findByUsername("John.Doe")).thenReturn(Optional.of(testTrainee));

        assertTrue(traineeService.authenticate("John.Doe", "password123"));
    }

    @Test
    @DisplayName("authenticate: returns false when user not found")
    void authenticate_userNotFound() {
        when(traineeDAO.findByUsername("ghost")).thenReturn(Optional.empty());

        assertFalse(traineeService.authenticate("ghost", "anyPass"));
    }

    @Test
    @DisplayName("changePassword: updates when old password matches")
    void changePassword_success() {
        when(traineeDAO.findByUsername("John.Doe")).thenReturn(Optional.of(testTrainee));
        when(traineeDAO.update(testTrainee)).thenReturn(testTrainee);

        traineeService.changePassword("John.Doe", "password123", "newPass456");

        assertEquals("newPass456", testTrainee.getUser().getPassword());
        verify(traineeDAO).update(testTrainee);
    }

    @Test
    @DisplayName("changePassword: rejects wrong old password")
    void changePassword_oldPasswordMismatch() {
        when(traineeDAO.findByUsername("John.Doe")).thenReturn(Optional.of(testTrainee));

        assertThrows(IllegalArgumentException.class,
                () -> traineeService.changePassword("John.Doe", "wrongOld", "newPass"));
        verify(traineeDAO, never()).update(any());
    }

    @Test
    @DisplayName("activateTrainee: activates inactive trainee")
    void activateTrainee_success() {
        testUser.setIsActive(false);
        when(traineeDAO.findByUsername("John.Doe")).thenReturn(Optional.of(testTrainee));
        when(traineeDAO.update(testTrainee)).thenReturn(testTrainee);

        traineeService.activateTrainee("John.Doe");

        assertTrue(testTrainee.getUser().getIsActive());
        verify(traineeDAO).update(testTrainee);
    }

    @Test
    @DisplayName("activateTrainee: rejects already active trainee")
    void activateTrainee_alreadyActive() {
        testUser.setIsActive(true);
        when(traineeDAO.findByUsername("John.Doe")).thenReturn(Optional.of(testTrainee));

        assertThrows(IllegalStateException.class,
                () -> traineeService.activateTrainee("John.Doe"));
        verify(traineeDAO, never()).update(any());
    }

    @Test
    @DisplayName("deactivateTrainee: deactivates active trainee")
    void deactivateTrainee_success() {
        testUser.setIsActive(true);
        when(traineeDAO.findByUsername("John.Doe")).thenReturn(Optional.of(testTrainee));
        when(traineeDAO.update(testTrainee)).thenReturn(testTrainee);

        traineeService.deactivateTrainee("John.Doe");

        assertFalse(testTrainee.getUser().getIsActive());
        verify(traineeDAO).update(testTrainee);
    }

    @Test
    @DisplayName("deactivateTrainee: rejects already inactive trainee")
    void deactivateTrainee_alreadyInactive() {
        testUser.setIsActive(false);
        when(traineeDAO.findByUsername("John.Doe")).thenReturn(Optional.of(testTrainee));

        assertThrows(IllegalStateException.class,
                () -> traineeService.deactivateTrainee("John.Doe"));
        verify(traineeDAO, never()).update(any());
    }

    @Test
    @DisplayName("updateTraineeTrainersList: replaces trainers list")
    void updateTraineeTrainersList_success() {
        User trainerUser1 = new User();
        trainerUser1.setFirstName("Trainer");
        trainerUser1.setLastName("One");
        trainerUser1.setUsername("Trainer.One");
        Trainer trainer1 = new Trainer();
        trainer1.setId(10L);
        trainer1.setUser(trainerUser1);

        User trainerUser2 = new User();
        trainerUser2.setFirstName("Trainer");
        trainerUser2.setLastName("Two");
        trainerUser2.setUsername("Trainer.Two");
        Trainer trainer2 = new Trainer();
        trainer2.setId(11L);
        trainer2.setUser(trainerUser2);

        testTrainee.setTrainers(new ArrayList<>(List.of(trainer1)));

        when(traineeDAO.findByUsername("John.Doe")).thenReturn(Optional.of(testTrainee));
        when(trainerDAO.findByUsername("Trainer.One")).thenReturn(Optional.of(trainer1));
        when(trainerDAO.findByUsername("Trainer.Two")).thenReturn(Optional.of(trainer2));
        when(traineeDAO.update(testTrainee)).thenReturn(testTrainee);

        List<Trainer> result = traineeService.updateTraineeTrainersList(
                "John.Doe", List.of("Trainer.One", "Trainer.Two"));

        assertEquals(2, result.size());
        assertEquals(2, testTrainee.getTrainers().size());
        verify(traineeDAO).update(testTrainee);
    }

    @Test
    @DisplayName("updateTraineeTrainersList: rejects missing trainer")
    void updateTraineeTrainersList_trainerNotFound() {
        when(traineeDAO.findByUsername("John.Doe")).thenReturn(Optional.of(testTrainee));
        when(trainerDAO.findByUsername("missing.trainer")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> traineeService.updateTraineeTrainersList(
                        "John.Doe", List.of("missing.trainer")));
        verify(traineeDAO, never()).update(any());
    }
}
