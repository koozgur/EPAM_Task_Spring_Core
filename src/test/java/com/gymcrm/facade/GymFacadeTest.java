package com.gymcrm.facade;

import com.gymcrm.dao.TrainingTypeDAO;
import com.gymcrm.dto.request.*;
import com.gymcrm.dto.response.*;
import com.gymcrm.exception.NotFoundException;
import com.gymcrm.mapper.TraineeMapper;
import com.gymcrm.mapper.TrainerMapper;
import com.gymcrm.mapper.TrainingMapper;
import com.gymcrm.mapper.TrainingTypeMapper;
import com.gymcrm.model.*;
import com.gymcrm.service.TraineeService;
import com.gymcrm.service.TrainerService;
import com.gymcrm.service.TrainingService;
import com.gymcrm.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GymFacadeTest {

    @Mock TraineeService traineeService;
    @Mock TrainerService trainerService;
    @Mock TrainingService trainingService;
    @Mock UserService userService;
    @Mock TrainingTypeDAO trainingTypeDAO;
    @Mock TraineeMapper traineeMapper;
    @Mock TrainerMapper trainerMapper;
    @Mock TrainingMapper trainingMapper;
    @Mock TrainingTypeMapper trainingTypeMapper;

    @InjectMocks
    GymFacade facade;

    @Test
    @DisplayName("registerTrainee returns username and password from created entity")
    void registerTrainee_happyPath_returnsCredentials() {
        User user = new User("John", "Doe", "john.doe1", "pass123456", true);
        Trainee created = new Trainee(user, LocalDate.of(1990, 1, 1), "123 Street");
        when(traineeService.createTrainee(any())).thenReturn(created);

        TraineeRegistrationRequest req = new TraineeRegistrationRequest();
        req.setFirstName("John");
        req.setLastName("Doe");
        req.setDateOfBirth(LocalDate.of(1990, 1, 1));
        req.setAddress("123 Street");

        RegistrationResponse response = facade.registerTrainee(req);

        assertThat(response.getUsername()).isEqualTo("john.doe1");
        assertThat(response.getPassword()).isEqualTo("pass123456");
    }

    @Test
    @DisplayName("registerTrainer resolves specialization from DB and returns credentials")
    void registerTrainer_happyPath_returnsCredentials() {
        TrainingType yoga = new TrainingType(1L, "Yoga");
        User user = new User("Jane", "Smith", "jane.smith1", "pass987654", true);
        Trainer created = new Trainer(user, yoga);
        when(trainingTypeDAO.findById(1L)).thenReturn(Optional.of(yoga));
        when(trainerService.createTrainer(any())).thenReturn(created);

        TrainerRegistrationRequest req = new TrainerRegistrationRequest();
        req.setFirstName("Jane");
        req.setLastName("Smith");
        req.setSpecializationId(1L);

        RegistrationResponse response = facade.registerTrainer(req);

        assertThat(response.getUsername()).isEqualTo("jane.smith1");
        assertThat(response.getPassword()).isEqualTo("pass987654");
    }

    @Test
    @DisplayName("registerTrainer throws NotFoundException when specialization ID does not exist")
    void registerTrainer_unknownSpecializationId_throwsNotFound() {
        when(trainingTypeDAO.findById(99L)).thenReturn(Optional.empty());

        TrainerRegistrationRequest req = new TrainerRegistrationRequest();
        req.setFirstName("Jane");
        req.setLastName("Smith");
        req.setSpecializationId(99L);

        assertThatThrownBy(() -> facade.registerTrainer(req))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("99");

        verifyNoInteractions(trainerService);
    }

    @Test
    @DisplayName("changePassword delegates to userService with correct arguments")
    void changePassword_delegatesToUserService() {
        ChangePasswordRequest req = new ChangePasswordRequest();
        req.setUsername("john.doe1");
        req.setOldPassword("oldPass1");
        req.setNewPassword("newPass99");

        facade.changePassword(req);

        verify(userService).changePassword("john.doe1", "oldPass1", "newPass99");
    }

    @Test
    @DisplayName("getTraineeProfile returns mapped response for existing trainee")
    void getTraineeProfile_exists_returnsMappedResponse() {
        Trainee trainee = new Trainee(new User(), null, null);
        TraineeProfileResponse expected = new TraineeProfileResponse();
        when(traineeService.getTraineeByUsername("john.doe1")).thenReturn(Optional.of(trainee));
        when(traineeMapper.toProfileResponse(trainee)).thenReturn(expected);

        TraineeProfileResponse result = facade.getTraineeProfile("john.doe1");

        assertThat(result).isSameAs(expected);
    }

    @Test
    @DisplayName("getTraineeProfile throws NotFoundException when trainee does not exist")
    void getTraineeProfile_notFound_throwsNotFoundException() {
        when(traineeService.getTraineeByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> facade.getTraineeProfile("ghost"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("ghost");
    }

    @Test
    @DisplayName("updateTrainee passes correct fields in shell entity and returns mapped response")
    void updateTrainee_happyPath_shellEntityHasCorrectFields() {
        Trainee updated = new Trainee();
        UpdateTraineeResponse expected = new UpdateTraineeResponse();
        when(traineeService.updateTrainee(any())).thenReturn(updated);
        when(traineeMapper.toUpdateResponse(updated)).thenReturn(expected);

        UpdateTraineeRequest req = new UpdateTraineeRequest();
        req.setUsername("john.doe1");
        req.setFirstName("John");
        req.setLastName("Doe");
        req.setIsActive(true);
        req.setDateOfBirth(LocalDate.of(1990, 5, 10));
        req.setAddress("New Address");

        UpdateTraineeResponse result = facade.updateTrainee(req);

        assertThat(result).isSameAs(expected);
        ArgumentCaptor<Trainee> captor = ArgumentCaptor.forClass(Trainee.class);
        verify(traineeService).updateTrainee(captor.capture());
        Trainee shell = captor.getValue();
        assertThat(shell.getUser().getUsername()).isEqualTo("john.doe1");
        assertThat(shell.getUser().getFirstName()).isEqualTo("John");
        assertThat(shell.getUser().getIsActive()).isTrue();
        assertThat(shell.getDateOfBirth()).isEqualTo(LocalDate.of(1990, 5, 10));
        assertThat(shell.getAddress()).isEqualTo("New Address");
    }

    @Test
    @DisplayName("deleteTrainee delegates to traineeService")
    void deleteTrainee_delegatesToService() {
        facade.deleteTrainee("john.doe1");
        verify(traineeService).deleteTraineeByUsername("john.doe1");
    }

    @Test
    @DisplayName("getTrainerProfile returns mapped response for existing trainer")
    void getTrainerProfile_exists_returnsMappedResponse() {
        Trainer trainer = new Trainer(new User(), null);
        TrainerProfileResponse expected = new TrainerProfileResponse();
        when(trainerService.getTrainerByUsername("jane.smith1")).thenReturn(Optional.of(trainer));
        when(trainerMapper.toProfileResponse(trainer)).thenReturn(expected);

        assertThat(facade.getTrainerProfile("jane.smith1")).isSameAs(expected);
    }

    @Test
    @DisplayName("getTrainerProfile throws NotFoundException when trainer does not exist")
    void getTrainerProfile_notFound_throwsNotFoundException() {
        when(trainerService.getTrainerByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> facade.getTrainerProfile("ghost"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("ghost");
    }

    @Test
    @DisplayName("updateTrainer shell entity has NO specialization set (read-only per spec)")
    void updateTrainer_specializationNotIncludedInShellEntity() {
        Trainer updated = new Trainer();
        when(trainerService.updateTrainer(any())).thenReturn(updated);
        when(trainerMapper.toUpdateResponse(updated)).thenReturn(new UpdateTrainerResponse());

        UpdateTrainerRequest req = new UpdateTrainerRequest();
        req.setUsername("jane.smith1");
        req.setFirstName("Jane");
        req.setLastName("Smith");
        req.setIsActive(false);

        facade.updateTrainer(req);

        ArgumentCaptor<Trainer> captor = ArgumentCaptor.forClass(Trainer.class);
        verify(trainerService).updateTrainer(captor.capture());
        assertThat(captor.getValue().getSpecialization()).isNull();
    }

    @Test
    @DisplayName("getUnassignedTrainers maps each trainer to summary response")
    void getUnassignedTrainers_returnsMappedList() {
        Trainer t1 = new Trainer(1L, new User(), null);
        Trainer t2 = new Trainer(2L, new User(), null);
        TrainerSummaryResponse s1 = new TrainerSummaryResponse();
        TrainerSummaryResponse s2 = new TrainerSummaryResponse();
        when(trainerService.getUnassignedTrainersByTraineeUsername("john.doe1"))
                .thenReturn(List.of(t1, t2));
        when(trainerMapper.toSummary(t1)).thenReturn(s1);
        when(trainerMapper.toSummary(t2)).thenReturn(s2);

        List<TrainerSummaryResponse> result = facade.getUnassignedTrainers("john.doe1");

        assertThat(result).containsExactly(s1, s2);
    }

    @Test
    @DisplayName("updateTraineeTrainers delegates with correct username and trainer list")
    void updateTraineeTrainers_delegatesCorrectly() {
        Trainer t = new Trainer();
        TrainerSummaryResponse summary = new TrainerSummaryResponse();
        when(traineeService.updateTraineeTrainersList(eq("john.doe1"), eq(List.of("jane.smith1"))))
                .thenReturn(List.of(t));
        when(trainerMapper.toSummary(t)).thenReturn(summary);

        UpdateTraineeTrainersRequest req = new UpdateTraineeTrainersRequest();
        req.setTraineeUsername("john.doe1");
        req.setTrainerUsernames(List.of("jane.smith1"));

        List<TrainerSummaryResponse> result = facade.updateTraineeTrainers(req);

        assertThat(result).containsExactly(summary);
    }

    @Test
    @DisplayName("getTraineeTrainings passes all filter params and returns mapped list")
    void getTraineeTrainings_filtersPassedThrough_returnsMappedList() {
        LocalDate from = LocalDate.of(2025, 1, 1);
        LocalDate to = LocalDate.of(2025, 12, 31);
        Training training = new Training();
        TraineeTrainingResponse response = new TraineeTrainingResponse();
        when(trainingService.getTraineeTrainingsByCriteria("john.doe1", from, to, "Jane", "Yoga"))
                .thenReturn(List.of(training));
        when(trainingMapper.toTraineeResponse(training)).thenReturn(response);

        List<TraineeTrainingResponse> result =
                facade.getTraineeTrainings("john.doe1", from, to, "Jane", "Yoga");

        assertThat(result).containsExactly(response);
    }

    @Test
    @DisplayName("getTraineeTrainings returns empty list when no trainings match filters")
    void getTraineeTrainings_noMatch_returnsEmptyList() {
        when(trainingService.getTraineeTrainingsByCriteria(any(), any(), any(), any(), any()))
                .thenReturn(List.of());

        List<TraineeTrainingResponse> result =
                facade.getTraineeTrainings("john.doe1", null, null, null, null);

        assertThat(result).isEmpty();
        verifyNoInteractions(trainingMapper);
    }

    @Test
    @DisplayName("getTrainerTrainings passes all filter params and returns mapped list")
    void getTrainerTrainings_filtersPassedThrough_returnsMappedList() {
        LocalDate from = LocalDate.of(2025, 3, 1);
        Training training = new Training();
        TrainerTrainingResponse response = new TrainerTrainingResponse();
        when(trainingService.getTrainerTrainingsByCriteria("jane.smith1", from, null, "John"))
                .thenReturn(List.of(training));
        when(trainingMapper.toTrainerResponse(training)).thenReturn(response);

        List<TrainerTrainingResponse> result =
                facade.getTrainerTrainings("jane.smith1", from, null, "John");

        assertThat(result).containsExactly(response);
    }

    @Test
    @DisplayName("addTraining creates training with trainer's specialization as type")
    void addTraining_happyPath_usesTrainerSpecialization() {
        TrainingType yoga = new TrainingType(1L, "Yoga");
        User tUser = new User("John", "Doe", "john.doe1", "p", true);
        User trUser = new User("Jane", "Smith", "jane.smith1", "p", true);
        Trainee trainee = new Trainee(tUser, null, null);
        Trainer trainer = new Trainer(trUser, yoga);

        when(traineeService.getTraineeByUsername("john.doe1")).thenReturn(Optional.of(trainee));
        when(trainerService.getTrainerByUsername("jane.smith1")).thenReturn(Optional.of(trainer));

        AddTrainingRequest req = new AddTrainingRequest();
        req.setTraineeUsername("john.doe1");
        req.setTrainerUsername("jane.smith1");
        req.setName("Morning Yoga");
        req.setDate(LocalDate.of(2025, 6, 1));
        req.setDuration(60);

        facade.addTraining(req);

        ArgumentCaptor<Training> captor = ArgumentCaptor.forClass(Training.class);
        verify(trainingService).createTraining(captor.capture());
        Training saved = captor.getValue();
        assertThat(saved.getTrainingType()).isSameAs(yoga);
        assertThat(saved.getTrainingName()).isEqualTo("Morning Yoga");
        assertThat(saved.getTrainingDuration()).isEqualTo(60);
    }

    @Test
    @DisplayName("addTraining throws NotFoundException when trainee does not exist")
    void addTraining_traineeNotFound_throwsNotFoundException() {
        when(traineeService.getTraineeByUsername("ghost")).thenReturn(Optional.empty());

        AddTrainingRequest req = new AddTrainingRequest();
        req.setTraineeUsername("ghost");
        req.setTrainerUsername("jane.smith1");

        assertThatThrownBy(() -> facade.addTraining(req))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("ghost");

        verifyNoInteractions(trainingService);
    }

    @Test
    @DisplayName("addTraining throws NotFoundException when trainer does not exist")
    void addTraining_trainerNotFound_throwsNotFoundException() {
        Trainee trainee = new Trainee(new User(), null, null);
        when(traineeService.getTraineeByUsername("john.doe1")).thenReturn(Optional.of(trainee));
        when(trainerService.getTrainerByUsername("ghost")).thenReturn(Optional.empty());

        AddTrainingRequest req = new AddTrainingRequest();
        req.setTraineeUsername("john.doe1");
        req.setTrainerUsername("ghost");

        assertThatThrownBy(() -> facade.addTraining(req))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("ghost");

        verifyNoInteractions(trainingService);
    }

    @Test
    @DisplayName("setTraineeActive(true) calls activateTrainee")
    void setTraineeActive_true_callsActivate() {
        facade.setTraineeActive("john.doe1", true);
        verify(traineeService).activateTrainee("john.doe1");
        verify(traineeService, never()).deactivateTrainee(any());
    }

    @Test
    @DisplayName("setTraineeActive(false) calls deactivateTrainee")
    void setTraineeActive_false_callsDeactivate() {
        facade.setTraineeActive("john.doe1", false);
        verify(traineeService).deactivateTrainee("john.doe1");
        verify(traineeService, never()).activateTrainee(any());
    }

    @Test
    @DisplayName("setTrainerActive(true) calls activateTrainer")
    void setTrainerActive_true_callsActivate() {
        facade.setTrainerActive("jane.smith1", true);
        verify(trainerService).activateTrainer("jane.smith1");
        verify(trainerService, never()).deactivateTrainer(any());
    }

    @Test
    @DisplayName("setTrainerActive(false) calls deactivateTrainer")
    void setTrainerActive_false_callsDeactivate() {
        facade.setTrainerActive("jane.smith1", false);
        verify(trainerService).deactivateTrainer("jane.smith1");
        verify(trainerService, never()).activateTrainer(any());
    }

    @Test
    @DisplayName("getAllTrainingTypes maps all types from DAO")
    void getAllTrainingTypes_returnsMappedList() {
        TrainingType yoga = new TrainingType(1L, "Yoga");
        TrainingType cardio = new TrainingType(2L, "Cardio");
        TrainingTypeResponse r1 = new TrainingTypeResponse();
        TrainingTypeResponse r2 = new TrainingTypeResponse();
        when(trainingTypeDAO.findAll()).thenReturn(List.of(yoga, cardio));
        when(trainingTypeMapper.toResponse(yoga)).thenReturn(r1);
        when(trainingTypeMapper.toResponse(cardio)).thenReturn(r2);

        List<TrainingTypeResponse> result = facade.getAllTrainingTypes();

        assertThat(result).containsExactly(r1, r2);
    }
}
