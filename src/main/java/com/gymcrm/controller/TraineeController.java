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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/trainees")
@Tag(name = "Trainees")
public class TraineeController {

    private final GymFacade facade;

    @Autowired
    public TraineeController(GymFacade facade) {
        this.facade = facade;
    }

    @PostMapping("/register")
    @Operation(summary = "Register trainee", description = "No authentication required.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    public ResponseEntity<RegistrationResponse> register(
            @Valid @RequestBody TraineeRegistrationRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(facade.registerTrainee(req));
    }

    @GetMapping("/{username}")
    @Operation(summary = "Get trainee profile", description = "Requires Bearer token. Send JWT via Authorization: Bearer <token>.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Unauthorized — missing or invalid Bearer token"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<TraineeProfileResponse> getProfile(
            @Parameter(description = "Trainee username", required = true) @PathVariable String username) {
        return ResponseEntity.ok(facade.getTraineeProfile(username));
    }

    @PutMapping("/{username}")
    @Operation(summary = "Update trainee profile", description = "Requires Bearer token. Send JWT via Authorization: Bearer <token>.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized — missing or invalid Bearer token"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<UpdateTraineeResponse> update(
            @Parameter(description = "Trainee username", required = true) @PathVariable String username,
            @Valid @RequestBody UpdateTraineeRequest req) {
        return ResponseEntity.ok(facade.updateTrainee(username, req));
    }

    @DeleteMapping("/{username}")
    @Operation(summary = "Delete trainee profile", description = "Requires Bearer token. Send JWT via Authorization: Bearer <token>.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Unauthorized — missing or invalid Bearer token"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<Void> delete(@Parameter(description = "Trainee username", required = true) @PathVariable String username) {
        facade.deleteTrainee(username);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/{username}/available-trainers")
    @Operation(summary = "Get available trainers for trainee", description = "Requires Bearer token. Send JWT via Authorization: Bearer <token>.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Unauthorized — missing or invalid Bearer token"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<List<TrainerSummaryResponse>> getAvailableTrainers(
            @Parameter(description = "Trainee username", required = true) @PathVariable String username) {
        return ResponseEntity.ok(facade.getUnassignedTrainers(username));
    }

    @PutMapping("/{username}/trainers")
    @Operation(summary = "Replace trainee trainer list", description = "Requires Bearer token. Send JWT via Authorization: Bearer <token>.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized — missing or invalid Bearer token"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<List<TrainerSummaryResponse>> updateTrainers(
            @Parameter(description = "Trainee username", required = true) @PathVariable String username,
            @Valid @RequestBody UpdateTraineeTrainersRequest req) {
        return ResponseEntity.ok(facade.updateTraineeTrainers(username, req));
    }

    @GetMapping("/{username}/trainings")
    @Operation(summary = "Get trainee trainings", description = "Requires Bearer token. Send JWT via Authorization: Bearer <token>.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Unauthorized — missing or invalid Bearer token"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<List<TraineeTrainingResponse>> getTrainings(
            @Parameter(description = "Trainee username", required = true) @PathVariable String username,
            @RequestParam(required = false)
            @Parameter(description = "Filter from date (yyyy-MM-dd)")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodFrom,
            @RequestParam(required = false)
            @Parameter(description = "Filter to date (yyyy-MM-dd)")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodTo,
            @Parameter(description = "Trainer name filter, firstName or lastName of the Trainer") @RequestParam(required = false) String trainerName,
            @Parameter(description = "Training type filter") @RequestParam(required = false) String trainingType) {
        return ResponseEntity.ok(
                facade.getTraineeTrainings(username, periodFrom, periodTo, trainerName, trainingType));
    }

    @PatchMapping("/{username}/activation")
    @Operation(summary = "Activate/deactivate trainee", description = "Requires Bearer token. Send JWT via Authorization: Bearer <token>.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized — missing or invalid Bearer token"),
            @ApiResponse(responseCode = "409", description = "State conflict")
    })
    public ResponseEntity<Void> setActive(
            @Parameter(description = "Trainee username", required = true) @PathVariable String username,
            @Valid @RequestBody ActivationRequest req) {
        facade.setTraineeActive(username, req.getIsActive());
        return ResponseEntity.ok().build();
    }
}
