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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
@Api(tags = "Trainees")
public class TraineeController {

    private final GymFacade facade;

    @Autowired
    public TraineeController(GymFacade facade) {
        this.facade = facade;
    }

    @PostMapping("/register")
    @ApiOperation(value = "Register trainee", notes = "No authentication required.")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Created"),
            @ApiResponse(code = 400, message = "Validation error")
    })
    public ResponseEntity<RegistrationResponse> register(
            @Valid @RequestBody TraineeRegistrationRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(facade.registerTrainee(req));
    }

    @GetMapping("/{username}")
    @ApiOperation(value = "Get trainee profile", notes = "Requires HTTP Basic auth. Send credentials via Authorization: Basic <base64(username:password)>.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized — missing or invalid Basic credentials"),
            @ApiResponse(code = 404, message = "Not found")
    })
    public ResponseEntity<TraineeProfileResponse> getProfile(
            @ApiParam(value = "Trainee username", required = true) @PathVariable String username) {
        return ResponseEntity.ok(facade.getTraineeProfile(username));
    }

    @PutMapping("/{username}")
    @ApiOperation(value = "Update trainee profile", notes = "Requires HTTP Basic auth. Send credentials via Authorization: Basic <base64(username:password)>.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "Validation error"),
            @ApiResponse(code = 401, message = "Unauthorized — missing or invalid Basic credentials"),
            @ApiResponse(code = 404, message = "Not found")
    })
    public ResponseEntity<UpdateTraineeResponse> update(
            @ApiParam(value = "Trainee username", required = true) @PathVariable String username,
            @Valid @RequestBody UpdateTraineeRequest req) {
        return ResponseEntity.ok(facade.updateTrainee(username, req));
    }

    @DeleteMapping("/{username}")
    @ApiOperation(value = "Delete trainee profile", notes = "Requires HTTP Basic auth. Send credentials via Authorization: Basic <base64(username:password)>.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized — missing or invalid Basic credentials"),
            @ApiResponse(code = 404, message = "Not found")
    })
    public ResponseEntity<Void> delete(@ApiParam(value = "Trainee username", required = true) @PathVariable String username) {
        facade.deleteTrainee(username);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/{username}/available-trainers")
    @ApiOperation(value = "Get available trainers for trainee", notes = "Requires HTTP Basic auth. Send credentials via Authorization: Basic <base64(username:password)>.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized — missing or invalid Basic credentials"),
            @ApiResponse(code = 404, message = "Not found")
    })
    public ResponseEntity<List<TrainerSummaryResponse>> getAvailableTrainers(
            @ApiParam(value = "Trainee username", required = true) @PathVariable String username) {
        return ResponseEntity.ok(facade.getUnassignedTrainers(username));
    }

    @PutMapping("/{username}/trainers")
    @ApiOperation(value = "Replace trainee trainer list", notes = "Requires HTTP Basic auth. Send credentials via Authorization: Basic <base64(username:password)>.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "Validation error"),
            @ApiResponse(code = 401, message = "Unauthorized — missing or invalid Basic credentials"),
            @ApiResponse(code = 404, message = "Not found")
    })
    public ResponseEntity<List<TrainerSummaryResponse>> updateTrainers(
            @ApiParam(value = "Trainee username", required = true) @PathVariable String username,
            @Valid @RequestBody UpdateTraineeTrainersRequest req) {
        return ResponseEntity.ok(facade.updateTraineeTrainers(username, req));
    }

    @GetMapping("/{username}/trainings")
    @ApiOperation(value = "Get trainee trainings", notes = "Requires HTTP Basic auth. Send credentials via Authorization: Basic <base64(username:password)>.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized — missing or invalid Basic credentials"),
            @ApiResponse(code = 404, message = "Not found")
    })
    public ResponseEntity<List<TraineeTrainingResponse>> getTrainings(
            @ApiParam(value = "Trainee username", required = true) @PathVariable String username,
            @RequestParam(required = false)
            @ApiParam(value = "Filter from date (yyyy-MM-dd)")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodFrom,
            @RequestParam(required = false)
            @ApiParam(value = "Filter to date (yyyy-MM-dd)")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodTo,
            @ApiParam(value = "Trainer name filter, firstName or lastName of the Trainer") @RequestParam(required = false) String trainerName,
            @ApiParam(value = "Training type filter") @RequestParam(required = false) String trainingType) {
        return ResponseEntity.ok(
                facade.getTraineeTrainings(username, periodFrom, periodTo, trainerName, trainingType));
    }

    @PatchMapping("/{username}/activation")
    @ApiOperation(value = "Activate/deactivate trainee", notes = "Requires HTTP Basic auth. Send credentials via Authorization: Basic <base64(username:password)>.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "Validation error"),
            @ApiResponse(code = 401, message = "Unauthorized — missing or invalid Basic credentials"),
            @ApiResponse(code = 409, message = "State conflict")
    })
    public ResponseEntity<Void> setActive(
            @ApiParam(value = "Trainee username", required = true) @PathVariable String username,
            @Valid @RequestBody ActivationRequest req) {
        facade.setTraineeActive(username, req.getIsActive());
        return ResponseEntity.ok().build();
    }
}
