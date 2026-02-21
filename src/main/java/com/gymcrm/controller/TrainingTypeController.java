package com.gymcrm.controller;

import com.gymcrm.dto.response.TrainingTypeResponse;
import com.gymcrm.facade.GymFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/training-types")
public class TrainingTypeController {

    private final GymFacade facade;

    @Autowired
    public TrainingTypeController(GymFacade facade) {
        this.facade = facade;
    }

    @GetMapping
    public ResponseEntity<List<TrainingTypeResponse>> getAllTrainingTypes() {
        return ResponseEntity.ok(facade.getAllTrainingTypes());
    }
}
