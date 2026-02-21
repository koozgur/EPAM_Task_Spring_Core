package com.gymcrm.controller;

import com.gymcrm.dto.request.ActivationRequest;
import com.gymcrm.dto.request.TrainerRegistrationRequest;
import com.gymcrm.dto.request.UpdateTrainerRequest;
import com.gymcrm.dto.response.RegistrationResponse;
import com.gymcrm.dto.response.TrainerProfileResponse;
import com.gymcrm.dto.response.TrainerTrainingResponse;
import com.gymcrm.dto.response.UpdateTrainerResponse;
import com.gymcrm.facade.GymFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/trainers")
public class TrainerController {

    private final GymFacade facade;

    @Autowired
    public TrainerController(GymFacade facade) {
        this.facade = facade;
    }

    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> register(
            @Valid @RequestBody TrainerRegistrationRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(facade.registerTrainer(req));
    }

    @GetMapping("/{username}")
    public ResponseEntity<TrainerProfileResponse> getProfile(
            @PathVariable String username) {
        return ResponseEntity.ok(facade.getTrainerProfile(username));
    }

    @PutMapping("/{username}")
    public ResponseEntity<UpdateTrainerResponse> update(
            @PathVariable String username,
            @Valid @RequestBody UpdateTrainerRequest req) {
        return ResponseEntity.ok(facade.updateTrainer(username, req));
    }

    @GetMapping("/{username}/trainings")
    public ResponseEntity<List<TrainerTrainingResponse>> getTrainings(
            @PathVariable String username,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodFrom,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodTo,
            @RequestParam(required = false) String traineeName) {
        return ResponseEntity.ok(
                facade.getTrainerTrainings(username, periodFrom, periodTo, traineeName));
    }

    @PatchMapping("/{username}/activation")
    public ResponseEntity<Void> setActive(
            @PathVariable String username,
            @Valid @RequestBody ActivationRequest req) {
        facade.setTrainerActive(username, req.getIsActive());
        return ResponseEntity.ok().build();
    }
}
