package com.gymcrm.service;

import com.gymcrm.dto.request.TrainerWorkloadRequest;
import com.gymcrm.model.Training;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Publishes workload events when a training is created or deleted.
 */
@Service
public class WorkloadNotificationService {

    private static final Logger log = LoggerFactory.getLogger(WorkloadNotificationService.class);

    public void notifyAdd(Training training) {
        notify(training, TrainerWorkloadRequest.ActionType.ADD);
    }

    public void notifyDelete(Training training) {
        notify(training, TrainerWorkloadRequest.ActionType.DELETE);
    }

    private void notify(Training training, TrainerWorkloadRequest.ActionType actionType) {
        // TODO (Phase 4): replace with JmsTemplate.convertAndSend
        log.warn("Workload notification is a no-op until Phase 4 JMS wiring is complete " +
                 "[trainer={}, action={}]",
                training.getTrainer().getUser().getUsername(), actionType);
    }
}
