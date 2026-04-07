package com.gymcrm.service;

import com.gymcrm.dto.request.TrainerWorkloadRequest;
import com.gymcrm.model.Training;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

/**
 * Publishes workload events to a JMS queue when trainings are created or deleted.
 *
 * Uses a fire-and-forget approach: failures during message publishing are logged
 * but do not affect the already committed database transaction.
 *
 * Adds a transaction ID from MDC to each message for cross-service traceability.
 */
@Service
public class WorkloadNotificationService {

    private static final Logger log = LoggerFactory.getLogger(WorkloadNotificationService.class);

    /** Must match {@code TransactionLoggingFilter.TRANSACTION_ID_KEY}. */
    private static final String MDC_TRANSACTION_ID = "transactionId";

    /** JMS property name — mirrors the HTTP header used by {@code TransactionLoggingFilter}. */
    private static final String JMS_TRANSACTION_ID_PROPERTY = "X-Transaction-Id";

    private final JmsTemplate jmsTemplate;
    private final String workloadQueue;

    public WorkloadNotificationService(JmsTemplate jmsTemplate,
                                       @Value("${jms.queue.workload}") String workloadQueue) {
        this.jmsTemplate = jmsTemplate;
        this.workloadQueue = workloadQueue;
    }

    public void notifyAdd(Training training) {
        notify(training, TrainerWorkloadRequest.ActionType.ADD);
    }

    public void notifyDelete(Training training) {
        notify(training, TrainerWorkloadRequest.ActionType.DELETE);
    }

    private void notify(Training training, TrainerWorkloadRequest.ActionType actionType) {
        TrainerWorkloadRequest request = buildRequest(training, actionType);
        String transactionId = MDC.get(MDC_TRANSACTION_ID);

        log.info("Publishing workload event: trainer={}, action={}",
                request.getTrainerUsername(), actionType);
        try {
            jmsTemplate.convertAndSend(workloadQueue, request, message -> {
                if (transactionId != null) {
                    message.setStringProperty(JMS_TRANSACTION_ID_PROPERTY, transactionId);
                }
                return message;
            });
        } catch (JmsException e) {
            /*
            * The training has already been persisted in a separate flow, so rolling back is not an option here. 
            * Log the failure so it can be monitored and investigated.
             */
            log.error("Failed to publish workload event — workload summary may be stale " +
                      "[trainer={}, action={}]", request.getTrainerUsername(), actionType, e);
        }
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
