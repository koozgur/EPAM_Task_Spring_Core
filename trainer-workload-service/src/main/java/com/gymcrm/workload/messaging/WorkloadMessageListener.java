package com.gymcrm.workload.messaging;

import com.gymcrm.workload.dto.WorkloadRequest;
import com.gymcrm.workload.service.WorkloadService;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * JMS consumer for trainer workload events.
 *
 * <p>Implements a two-tier error handling strategy:
 * invalid messages are logged and discarded (no retry), while processing
 * failures trigger rollback and redelivery (eventually routed to DLQ).
 *
 * <p>Propagates the {@code X-Transaction-Id} JMS property into MDC to enable
 * end-to-end log correlation across services.
 */
@Component
public class WorkloadMessageListener {

    private static final Logger log = LoggerFactory.getLogger(WorkloadMessageListener.class);

    // Must match TransactionIdPropagationFilter.TRANSACTION_ID_MDC_KEY
    private static final String TRANSACTION_ID_MDC_KEY = "transactionId";
    // Must match the property set by WorkloadNotificationService MessagePostProcessor
    private static final String TRANSACTION_ID_JMS_PROPERTY = "X-Transaction-Id";

    private final WorkloadService workloadService;
    private final Validator validator;

    public WorkloadMessageListener(WorkloadService workloadService, Validator validator) {
        this.workloadService = workloadService;
        this.validator = validator;
    }

    @JmsListener(destination = "${workload.jms.queue-name}",
                 containerFactory = "jmsListenerContainerFactory")
    public void onMessage(WorkloadRequest request, Message rawMessage) {
        setupMdc(rawMessage);
        try {
            log.info("Received workload event: trainer={}, action={}",
                    request.getTrainerUsername(), request.getActionType());

            Set<ConstraintViolation<WorkloadRequest>> violations = validator.validate(request);
            if (!violations.isEmpty()) {
                // Tier 1: payload is permanently invalid — discard without retry.
                log.error("Invalid workload message discarded [trainer={}, action={}]: {}",
                        request.getTrainerUsername(), request.getActionType(),
                        formatViolations(violations));
                return;
            }

            workloadService.processWorkload(request);

            log.info("Workload event processed successfully: trainer={}, action={}",
                    request.getTrainerUsername(), request.getActionType());

        } catch (Exception e) {
            // Tier 2: transient/infrastructure failure.
            // Rethrow so the transacted JMS session rolls back → broker redelivers
            log.error("Workload processing failed — session will roll back for redelivery " +
                      "[trainer={}, action={}]",
                      request.getTrainerUsername(), request.getActionType(), e);
            throw new RuntimeException("Workload processing failure — triggering redelivery", e);
        } finally {
            MDC.remove(TRANSACTION_ID_MDC_KEY);
        }
    }

    private void setupMdc(Message rawMessage) {
        try {
            String transactionId = rawMessage.getStringProperty(TRANSACTION_ID_JMS_PROPERTY);
            if (transactionId != null) {
                MDC.put(TRANSACTION_ID_MDC_KEY, transactionId);
            }
        } catch (JMSException e) {
            log.warn("Could not read {} JMS property — correlated logging unavailable",
                    TRANSACTION_ID_JMS_PROPERTY);
        }
    }

    private String formatViolations(Set<ConstraintViolation<WorkloadRequest>> violations) {
        return violations.stream()
                .map(v -> v.getPropertyPath() + " " + v.getMessage())
                .collect(Collectors.joining(", "));
    }
}
