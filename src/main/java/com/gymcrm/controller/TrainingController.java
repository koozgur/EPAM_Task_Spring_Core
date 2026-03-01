package com.gymcrm.controller;

import com.gymcrm.dto.request.AddTrainingRequest;
import com.gymcrm.facade.GymFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/trainings")
@Tag(name = "Trainings")
public class TrainingController {

    private final GymFacade facade;

    @Autowired
    public TrainingController(GymFacade facade) {
        this.facade = facade;
    }

    @PostMapping
        @Operation(summary = "Add training", description = "Requires HTTP Basic auth. Send credentials via Authorization: Basic <base64(username:password)>." )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized — missing or invalid Basic credentials"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<Void> addTraining(@Valid @RequestBody AddTrainingRequest req) {
        facade.addTraining(req);
        return ResponseEntity.ok().build();
    }
}
