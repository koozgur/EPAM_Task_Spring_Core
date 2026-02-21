package com.gymcrm.controller;

import com.gymcrm.dto.request.AddTrainingRequest;
import com.gymcrm.facade.GymFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/trainings")
public class TrainingController {

    private final GymFacade facade;

    @Autowired
    public TrainingController(GymFacade facade) {
        this.facade = facade;
    }

    @PostMapping
    public ResponseEntity<Void> addTraining(@Valid @RequestBody AddTrainingRequest req) {
        facade.addTraining(req);
        return ResponseEntity.ok().build();
    }
}
