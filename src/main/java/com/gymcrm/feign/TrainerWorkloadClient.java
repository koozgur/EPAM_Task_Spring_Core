package com.gymcrm.feign;

import com.gymcrm.dto.request.TrainerWorkloadRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Feign client for the trainer-workload-service.
 * Service name is resolved via Eureka; fallback fires when the circuit is open.
 */
@FeignClient(
        name = "trainer-workload-service",
        path = "/api/workload",
        fallback = TrainerWorkloadClientFallback.class
)
public interface TrainerWorkloadClient {

    @PostMapping
    ResponseEntity<Void> updateWorkload(@RequestBody TrainerWorkloadRequest request);
}
