package com.gymcrm.controller;

import com.gymcrm.dto.response.TrainingTypeResponse;
import com.gymcrm.facade.GymFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/training-types")
@Api(tags = "Training Types")
public class TrainingTypeController {

    private final GymFacade facade;

    @Autowired
    public TrainingTypeController(GymFacade facade) {
        this.facade = facade;
    }

    @GetMapping
    @ApiOperation(value = "Get training types", notes = "Requires HTTP Basic auth. Send credentials via Authorization: Basic <base64(username:password)>.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized — missing or invalid Basic credentials")
    })
    public ResponseEntity<List<TrainingTypeResponse>> getAllTrainingTypes() {
        return ResponseEntity.ok(facade.getAllTrainingTypes());
    }
}
