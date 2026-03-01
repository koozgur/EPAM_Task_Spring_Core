package com.gymcrm.controller;

import com.gymcrm.dto.request.ActivationRequest;
import com.gymcrm.dto.request.TrainerRegistrationRequest;
import com.gymcrm.dto.request.UpdateTrainerRequest;
import com.gymcrm.dto.response.RegistrationResponse;
import com.gymcrm.dto.response.TrainerProfileResponse;
import com.gymcrm.dto.response.TrainerTrainingResponse;
import com.gymcrm.dto.response.UpdateTrainerResponse;
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
@RequestMapping("/trainers")
@Tag(name = "Trainers")
public class TrainerController {

    private final GymFacade facade;

    @Autowired
    public TrainerController(GymFacade facade) {
        this.facade = facade;
    }

    @PostMapping("/register")
    @Operation(summary = "Register trainer", description = "No authentication required.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    public ResponseEntity<RegistrationResponse> register(
            @Valid @RequestBody TrainerRegistrationRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(facade.registerTrainer(req));
    }

    @GetMapping("/{username}")
    @Operation(summary = "Get trainer profile", description = "Requires HTTP Basic auth. Send credentials via Authorization: Basic <base64(username:password)>." )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Unauthorized — missing or invalid Basic credentials"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<TrainerProfileResponse> getProfile(
            @Parameter(description = "Trainer username", required = true) @PathVariable String username) {
        return ResponseEntity.ok(facade.getTrainerProfile(username));
    }

    @PutMapping("/{username}")
    @Operation(summary = "Update trainer profile", description = "Requires HTTP Basic auth. Send credentials via Authorization: Basic <base64(username:password)>." )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized — missing or invalid Basic credentials"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<UpdateTrainerResponse> update(
            @Parameter(description = "Trainer username", required = true) @PathVariable String username,
            @Valid @RequestBody UpdateTrainerRequest req) {
        return ResponseEntity.ok(facade.updateTrainer(username, req));
    }

    @GetMapping("/{username}/trainings")
    @Operation(summary = "Get trainer trainings", description = "Requires HTTP Basic auth. Send credentials via Authorization: Basic <base64(username:password)>." )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Unauthorized — missing or invalid Basic credentials"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<List<TrainerTrainingResponse>> getTrainings(
            @Parameter(description = "Trainer username", required = true) @PathVariable String username,
            @RequestParam(required = false)
            @Parameter(description = "Filter from date (yyyy-MM-dd)")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodFrom,
            @RequestParam(required = false)
            @Parameter(description = "Filter to date (yyyy-MM-dd)")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodTo,
            @Parameter(description = "Trainee name filter") @RequestParam(required = false) String traineeName) {
        return ResponseEntity.ok(
                facade.getTrainerTrainings(username, periodFrom, periodTo, traineeName));
    }

    @PatchMapping("/{username}/activation")
    @Operation(summary = "Activate/deactivate trainer", description = "Requires HTTP Basic auth. Send credentials via Authorization: Basic <base64(username:password)>." )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized — missing or invalid Basic credentials"),
            @ApiResponse(responseCode = "409", description = "State conflict")
    })
    public ResponseEntity<Void> setActive(
            @Parameter(description = "Trainer username", required = true) @PathVariable String username,
            @Valid @RequestBody ActivationRequest req) {
        facade.setTrainerActive(username, req.getIsActive());
        return ResponseEntity.ok().build();
    }
}
