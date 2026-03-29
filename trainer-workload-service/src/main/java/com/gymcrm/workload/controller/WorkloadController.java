package com.gymcrm.workload.controller;

import com.gymcrm.workload.dto.WorkloadRequest;
import com.gymcrm.workload.dto.WorkloadSummaryResponse;
import com.gymcrm.workload.service.WorkloadService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for trainer workload management.
 * POST /api/workload       — accept ADD/DELETE workload event (called by main service)
 * GET  /api/workload/{username} — retrieve monthly summary for a trainer
 */
@RestController
@RequestMapping("/api/workload")
public class WorkloadController {

    private static final Logger log = LoggerFactory.getLogger(WorkloadController.class);

    private final WorkloadService workloadService;

    public WorkloadController(WorkloadService workloadService) {
        this.workloadService = workloadService;
    }

    @PostMapping
    public ResponseEntity<Void> updateWorkload(@Valid @RequestBody WorkloadRequest req) {
        log.info("Received workload update: trainer={}, action={}, date={}, duration={}min",
                 req.getTrainerUsername(), req.getActionType(),
                 req.getTrainingDate(), req.getTrainingDuration());
        workloadService.processWorkload(req);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{trainerUsername}")
    public ResponseEntity<WorkloadSummaryResponse> getSummary(
            @PathVariable String trainerUsername) {
        log.info("Retrieving workload summary for trainer: {}", trainerUsername);
        return ResponseEntity.ok(workloadService.getSummary(trainerUsername));
    }
}
