package com.gymcrm.controller;

import com.gymcrm.dto.request.ActivationRequest;
import com.gymcrm.dto.request.TrainerRegistrationRequest;
import com.gymcrm.dto.request.UpdateTrainerRequest;
import com.gymcrm.dto.response.RegistrationResponse;
import com.gymcrm.dto.response.TrainerProfileResponse;
import com.gymcrm.dto.response.TrainerTrainingResponse;
import com.gymcrm.dto.response.UpdateTrainerResponse;
import com.gymcrm.facade.GymFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
@Api(tags = "Trainers")
public class TrainerController {

    private final GymFacade facade;

    @Autowired
    public TrainerController(GymFacade facade) {
        this.facade = facade;
    }

    @PostMapping("/register")
    @ApiOperation(value = "Register trainer", notes = "No authentication required.")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Created"),
            @ApiResponse(code = 400, message = "Validation error")
    })
    public ResponseEntity<RegistrationResponse> register(
            @Valid @RequestBody TrainerRegistrationRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(facade.registerTrainer(req));
    }

    @GetMapping("/{username}")
    @ApiOperation(value = "Get trainer profile", notes = "Requires HTTP Basic auth. Send credentials via Authorization: Basic <base64(username:password)>.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized — missing or invalid Basic credentials"),
            @ApiResponse(code = 404, message = "Not found")
    })
    public ResponseEntity<TrainerProfileResponse> getProfile(
            @ApiParam(value = "Trainer username", required = true) @PathVariable String username) {
        return ResponseEntity.ok(facade.getTrainerProfile(username));
    }

    @PutMapping("/{username}")
    @ApiOperation(value = "Update trainer profile", notes = "Requires HTTP Basic auth. Send credentials via Authorization: Basic <base64(username:password)>.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "Validation error"),
            @ApiResponse(code = 401, message = "Unauthorized — missing or invalid Basic credentials"),
            @ApiResponse(code = 404, message = "Not found")
    })
    public ResponseEntity<UpdateTrainerResponse> update(
            @ApiParam(value = "Trainer username", required = true) @PathVariable String username,
            @Valid @RequestBody UpdateTrainerRequest req) {
        return ResponseEntity.ok(facade.updateTrainer(username, req));
    }

    @GetMapping("/{username}/trainings")
    @ApiOperation(value = "Get trainer trainings", notes = "Requires HTTP Basic auth. Send credentials via Authorization: Basic <base64(username:password)>.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized — missing or invalid Basic credentials"),
            @ApiResponse(code = 404, message = "Not found")
    })
    public ResponseEntity<List<TrainerTrainingResponse>> getTrainings(
            @ApiParam(value = "Trainer username", required = true) @PathVariable String username,
            @RequestParam(required = false)
            @ApiParam(value = "Filter from date (yyyy-MM-dd)")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodFrom,
            @RequestParam(required = false)
            @ApiParam(value = "Filter to date (yyyy-MM-dd)")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodTo,
            @ApiParam(value = "Trainee name filter") @RequestParam(required = false) String traineeName) {
        return ResponseEntity.ok(
                facade.getTrainerTrainings(username, periodFrom, periodTo, traineeName));
    }

    @PatchMapping("/{username}/activation")
    @ApiOperation(value = "Activate/deactivate trainer", notes = "Requires HTTP Basic auth. Send credentials via Authorization: Basic <base64(username:password)>.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "Validation error"),
            @ApiResponse(code = 401, message = "Unauthorized — missing or invalid Basic credentials"),
            @ApiResponse(code = 409, message = "State conflict")
    })
    public ResponseEntity<Void> setActive(
            @ApiParam(value = "Trainer username", required = true) @PathVariable String username,
            @Valid @RequestBody ActivationRequest req) {
        facade.setTrainerActive(username, req.getIsActive());
        return ResponseEntity.ok().build();
    }
}
