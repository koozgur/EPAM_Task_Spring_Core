package com.gymcrm.service;

import com.gymcrm.dto.request.TrainerWorkloadRequest;
import com.gymcrm.feign.TrainerWorkloadClient;
import com.gymcrm.model.Training;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Notifies the trainer-workload-service when a training is created or deleted.
 */
@Service
public class WorkloadNotificationService {

    private static final Logger log = LoggerFactory.getLogger(WorkloadNotificationService.class);

    private final TrainerWorkloadClient workloadClient;

    public WorkloadNotificationService(TrainerWorkloadClient workloadClient) {
        this.workloadClient = workloadClient;
    }

    public void notifyAdd(Training training) {
        notify(training, TrainerWorkloadRequest.ActionType.ADD);
    }

    public void notifyDelete(Training training) {
        notify(training, TrainerWorkloadRequest.ActionType.DELETE);
    }

    private void notify(Training training, TrainerWorkloadRequest.ActionType actionType) {
        TrainerWorkloadRequest request = buildRequest(training, actionType);
        log.info("Notifying workload service: trainer={}, action={}",
                request.getTrainerUsername(), actionType);
        workloadClient.updateWorkload(request);
    }

    private TrainerWorkloadRequest buildRequest(Training training,
                                                TrainerWorkloadRequest.ActionType actionType) {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainerUsername(training.getTrainer().getUser().getUsername());
        request.setFirstName(training.getTrainer().getUser().getFirstName());
        request.setLastName(training.getTrainer().getUser().getLastName());
        request.setIsActive(training.getTrainer().getUser().getIsActive());
        request.setTrainingDate(training.getTrainingDate());
        request.setTrainingDuration(training.getTrainingDuration());
        request.setActionType(actionType);
        return request;
    }
}
