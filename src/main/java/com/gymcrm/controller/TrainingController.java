package com.gymcrm.controller;

import com.gymcrm.dto.request.AddTrainingRequest;
import com.gymcrm.facade.GymFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/trainings")
@Api(tags = "Trainings")
public class TrainingController {

    private final GymFacade facade;

    @Autowired
    public TrainingController(GymFacade facade) {
        this.facade = facade;
    }

    @PostMapping
    @ApiOperation(value = "Add training", notes = "Requires HTTP Basic auth. Send credentials via Authorization: Basic <base64(username:password)>.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "Validation error"),
            @ApiResponse(code = 401, message = "Unauthorized — missing or invalid Basic credentials"),
            @ApiResponse(code = 404, message = "Not found")
    })
    public ResponseEntity<Void> addTraining(@Valid @RequestBody AddTrainingRequest req) {
        facade.addTraining(req);
        return ResponseEntity.ok().build();
    }
}
