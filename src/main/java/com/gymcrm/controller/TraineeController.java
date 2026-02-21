package com.gymcrm.controller;

import com.gymcrm.dto.request.ActivationRequest;
import com.gymcrm.dto.request.TraineeRegistrationRequest;
import com.gymcrm.dto.request.UpdateTraineeRequest;
import com.gymcrm.dto.request.UpdateTraineeTrainersRequest;
import com.gymcrm.dto.response.RegistrationResponse;
import com.gymcrm.dto.response.TraineeProfileResponse;
import com.gymcrm.dto.response.TraineeTrainingResponse;
import com.gymcrm.dto.response.TrainerSummaryResponse;
import com.gymcrm.dto.response.UpdateTraineeResponse;
import com.gymcrm.facade.GymFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/trainees")
public class TraineeController {

    private final GymFacade facade;

    @Autowired
    public TraineeController(GymFacade facade) {
        this.facade = facade;
    }

    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> register(
            @Valid @RequestBody TraineeRegistrationRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(facade.registerTrainee(req));
    }

    @GetMapping("/{username}")
    public ResponseEntity<TraineeProfileResponse> getProfile(
            @PathVariable String username) {
        return ResponseEntity.ok(facade.getTraineeProfile(username));
    }

    @PutMapping("/{username}")
    public ResponseEntity<UpdateTraineeResponse> update(
            @PathVariable String username,
            @Valid @RequestBody UpdateTraineeRequest req) {
        return ResponseEntity.ok(facade.updateTrainee(username, req));
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> delete(@PathVariable String username) {
        facade.deleteTrainee(username);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/{username}/available-trainers")
    public ResponseEntity<List<TrainerSummaryResponse>> getAvailableTrainers(
            @PathVariable String username) {
        return ResponseEntity.ok(facade.getUnassignedTrainers(username));
    }

    @PutMapping("/{username}/trainers")
    public ResponseEntity<List<TrainerSummaryResponse>> updateTrainers(
            @PathVariable String username,
            @Valid @RequestBody UpdateTraineeTrainersRequest req) {
        return ResponseEntity.ok(facade.updateTraineeTrainers(username, req));
    }

    @GetMapping("/{username}/trainings")
    public ResponseEntity<List<TraineeTrainingResponse>> getTrainings(
            @PathVariable String username,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodFrom,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodTo,
            @RequestParam(required = false) String trainerName,
            @RequestParam(required = false) String trainingType) {
        return ResponseEntity.ok(
                facade.getTraineeTrainings(username, periodFrom, periodTo, trainerName, trainingType));
    }

    @PatchMapping("/{username}/activation")
    public ResponseEntity<Void> setActive(
            @PathVariable String username,
            @Valid @RequestBody ActivationRequest req) {
        facade.setTraineeActive(username, req.getIsActive());
        return ResponseEntity.ok().build();
    }
}
