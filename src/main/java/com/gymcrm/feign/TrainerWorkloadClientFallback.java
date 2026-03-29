package com.gymcrm.feign;

import com.gymcrm.dto.request.TrainerWorkloadRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * Fallback for TrainerWorkloadClient — invoked when the workload service is
 * unreachable or the circuit is open. Returns 200 silently because the training
 * is already committed; the workload summary will be temporarily out of sync.
 */
@Component
public class TrainerWorkloadClientFallback implements TrainerWorkloadClient {

    private static final Logger log = LoggerFactory.getLogger(TrainerWorkloadClientFallback.class);

    @Override
    public ResponseEntity<Void> updateWorkload(TrainerWorkloadRequest req) {
        log.warn("Workload service unavailable — fallback triggered: trainer={}, action={}",
                req.getTrainerUsername(), req.getActionType());
        return ResponseEntity.ok().build();
    }
}
