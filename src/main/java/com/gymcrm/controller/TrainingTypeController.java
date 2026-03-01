package com.gymcrm.controller;

import com.gymcrm.dto.response.TrainingTypeResponse;
import com.gymcrm.facade.GymFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/training-types")
@Tag(name = "Training Types")
public class TrainingTypeController {

    private final GymFacade facade;

    @Autowired
    public TrainingTypeController(GymFacade facade) {
        this.facade = facade;
    }

    @GetMapping
        @Operation(summary = "Get training types", description = "Requires HTTP Basic auth. Send credentials via Authorization: Basic <base64(username:password)>." )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Unauthorized — missing or invalid Basic credentials")
    })
    public ResponseEntity<List<TrainingTypeResponse>> getAllTrainingTypes() {
        return ResponseEntity.ok(facade.getAllTrainingTypes());
    }
}
