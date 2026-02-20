package com.gymcrm.service;

import com.gymcrm.dao.TrainerDAO;
import com.gymcrm.exception.AuthenticationException;
import com.gymcrm.exception.NotFoundException;
import com.gymcrm.exception.StateConflictException;
import com.gymcrm.exception.ValidationException;
import com.gymcrm.model.Trainer;
import com.gymcrm.model.TrainingType;
import com.gymcrm.model.User;
import com.gymcrm.util.CredentialsGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TrainerServiceImpl Tests")
class TrainerServiceImplTest {

    @Mock
    private TrainerDAO trainerDAO;

    @Mock
    private CredentialsGenerator credentialsGenerator;

    @Mock
    private UserService userService;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    private User testUser;
    private Trainer testTrainer;
    private TrainingType testSpecialization;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setFirstName("Mike");
        testUser.setLastName("Coach");
        testUser.setUsername("Mike.Coach");
        testUser.setPassword("password123");
        testUser.setIsActive(true);

        testSpecialization = new TrainingType("Cardio");
        testSpecialization.setId(1L);

        testTrainer = new Trainer();
        testTrainer.setId(1L);
        testTrainer.setUser(testUser);
        testTrainer.setSpecialization(testSpecialization);
    }

    @Test
    @DisplayName("createTrainer: generates credentials and persists")
    void createTrainer_success() {
        Trainer newTrainer = new Trainer();
        User newUser = new User();
        newUser.setFirstName("Sarah");
        newUser.setLastName("Fit");
        newTrainer.setUser(newUser);

        when(credentialsGenerator.generateUsername("Sarah", "Fit")).thenReturn("Sarah.Fit");
        when(credentialsGenerator.generatePassword()).thenReturn("randomPass10");
        when(trainerDAO.create(any(Trainer.class))).thenAnswer(inv -> {
            Trainer t = inv.getArgument(0);
            t.setId(2L);
            return t;
        });

        Trainer result = trainerService.createTrainer(newTrainer);

        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals("Sarah.Fit", result.getUser().getUsername());
        assertEquals("randomPass10", result.getUser().getPassword());
        assertTrue(result.getUser().getIsActive());
        verify(credentialsGenerator).generateUsername("Sarah", "Fit");
        verify(credentialsGenerator).generatePassword();
        verify(trainerDAO).create(newTrainer);
    }

    @Test
    @DisplayName("createTrainer: null trainer rejected")
    void createTrainer_nullTrainer() {
        assertThrows(ValidationException.class,
                () -> trainerService.createTrainer(null));
        verifyNoInteractions(trainerDAO);
    }

    @Test
    @DisplayName("updateTrainer: updates existing record")
    void updateTrainer_success() {
        User updatedUser = new User();
        updatedUser.setFirstName("Michael");
        updatedUser.setLastName("Coach");
        updatedUser.setUsername("Mike.Coach");
        updatedUser.setIsActive(false);

        TrainingType newType = new TrainingType("Strength");
        Trainer incoming = new Trainer();
        incoming.setUser(updatedUser);
        incoming.setSpecialization(newType);

        when(trainerDAO.findByUsername("Mike.Coach")).thenReturn(Optional.of(testTrainer));
        when(trainerDAO.update(testTrainer)).thenReturn(testTrainer);

        Trainer result = trainerService.updateTrainer(incoming);

        assertNotNull(result);
        assertEquals("Michael", testTrainer.getUser().getFirstName());
        assertFalse(testTrainer.getUser().getIsActive());
        assertEquals(newType, testTrainer.getSpecialization());
        verify(trainerDAO).update(testTrainer);
    }

    @Test
    @DisplayName("updateTrainer: not found by username")
    void updateTrainer_notFound() {
        User user = new User();
        user.setFirstName("Jane");
        user.setLastName("Doe");
        user.setUsername("nonexistent");
        Trainer incoming = new Trainer();
        incoming.setUser(user);

        when(trainerDAO.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> trainerService.updateTrainer(incoming));
        verify(trainerDAO, never()).update(any());
    }

    @Test
    @DisplayName("authenticate: returns true on match")
    void authenticate_success() {
        when(userService.authenticate("Mike.Coach", "password123")).thenReturn(true);

        assertTrue(trainerService.authenticate("Mike.Coach", "password123"));
    }

    @Test
    @DisplayName("authenticate: returns false when user not found")
    void authenticate_userNotFound() {
        when(userService.authenticate("ghost", "anyPass")).thenReturn(false);

        assertFalse(trainerService.authenticate("ghost", "anyPass"));
    }

    @Test
    @DisplayName("changePassword: updates when old password matches")
    void changePassword_success() {
        when(trainerDAO.findByUsername("Mike.Coach")).thenReturn(Optional.of(testTrainer));
        when(trainerDAO.update(testTrainer)).thenReturn(testTrainer);

        trainerService.changePassword("Mike.Coach", "password123", "newPass456");

        assertEquals("newPass456", testTrainer.getUser().getPassword());
        verify(trainerDAO).update(testTrainer);
    }

    @Test
    @DisplayName("changePassword: rejects wrong old password")
    void changePassword_oldPasswordMismatch() {
        when(trainerDAO.findByUsername("Mike.Coach")).thenReturn(Optional.of(testTrainer));

        assertThrows(AuthenticationException.class,
                () -> trainerService.changePassword("Mike.Coach", "wrongOld", "newPass"));
        verify(trainerDAO, never()).update(any());
    }

    @Test
    @DisplayName("activateTrainer: activates inactive trainer")
    void activateTrainer_success() {
        testUser.setIsActive(false);
        when(trainerDAO.findByUsername("Mike.Coach")).thenReturn(Optional.of(testTrainer));
        when(trainerDAO.update(testTrainer)).thenReturn(testTrainer);

        trainerService.activateTrainer("Mike.Coach");

        assertTrue(testTrainer.getUser().getIsActive());
        verify(trainerDAO).update(testTrainer);
    }

    @Test
    @DisplayName("activateTrainer: rejects already active trainer")
    void activateTrainer_alreadyActive() {
        testUser.setIsActive(true);
        when(trainerDAO.findByUsername("Mike.Coach")).thenReturn(Optional.of(testTrainer));

        assertThrows(StateConflictException.class,
                () -> trainerService.activateTrainer("Mike.Coach"));
        verify(trainerDAO, never()).update(any());
    }

    @Test
    @DisplayName("deactivateTrainer: deactivates active trainer")
    void deactivateTrainer_success() {
        testUser.setIsActive(true);
        when(trainerDAO.findByUsername("Mike.Coach")).thenReturn(Optional.of(testTrainer));
        when(trainerDAO.update(testTrainer)).thenReturn(testTrainer);

        trainerService.deactivateTrainer("Mike.Coach");

        assertFalse(testTrainer.getUser().getIsActive());
        verify(trainerDAO).update(testTrainer);
    }

    @Test
    @DisplayName("deactivateTrainer: rejects already inactive trainer")
    void deactivateTrainer_alreadyInactive() {
        testUser.setIsActive(false);
        when(trainerDAO.findByUsername("Mike.Coach")).thenReturn(Optional.of(testTrainer));

        assertThrows(StateConflictException.class,
                () -> trainerService.deactivateTrainer("Mike.Coach"));
        verify(trainerDAO, never()).update(any());
    }

    @Test
    @DisplayName("getUnassignedTrainersByTraineeUsername: returns list")
    void getUnassignedTrainersByTraineeUsername_success() {
        List<Trainer> unassigned = List.of(testTrainer);
        when(trainerDAO.findUnassignedTrainersByTraineeUsername("trainee.user"))
                .thenReturn(unassigned);

        List<Trainer> result = trainerService.getUnassignedTrainersByTraineeUsername("trainee.user");

        assertEquals(unassigned, result);
    }

    @Test
    @DisplayName("getUnassignedTrainersByTraineeUsername: null username rejected")
    void getUnassignedTrainersByTraineeUsername_nullUsername() {
        assertThrows(ValidationException.class,
                () -> trainerService.getUnassignedTrainersByTraineeUsername(null));
        verify(trainerDAO, never()).findUnassignedTrainersByTraineeUsername(any());
    }
}
