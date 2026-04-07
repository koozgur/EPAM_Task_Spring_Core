package com.gymcrm.workload.messaging;

import com.gymcrm.workload.dto.WorkloadRequest;
import com.gymcrm.workload.service.WorkloadService;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("WorkloadMessageListener")
class WorkloadMessageListenerTest {

    @Mock
    private WorkloadService workloadService;
    @Mock
    private Validator validator;
    @Mock
    private Message rawMessage;

    private WorkloadMessageListener listener;

    @BeforeEach
    void setUp() throws JMSException {
        listener = new WorkloadMessageListener(workloadService, validator);
        // default: no transaction ID in JMS property
        when(rawMessage.getStringProperty(anyString())).thenReturn(null);
    }

    @Test
    @DisplayName("valid message is delegated to WorkloadService")
    void onMessage_validRequest_delegatesToWorkloadService() {
        when(validator.validate(any(WorkloadRequest.class))).thenReturn(Set.of());

        listener.onMessage(validRequest(), rawMessage);

        verify(workloadService).processWorkload(any(WorkloadRequest.class));
    }

    @Test
    @DisplayName("invalid message is discarded — WorkloadService is never called")
    void onMessage_invalidRequest_discardsWithoutProcessing() {
        ConstraintViolation<WorkloadRequest> v = violation();
        when(validator.validate(any(WorkloadRequest.class))).thenReturn(Set.of(v));

        listener.onMessage(invalidRequest(), rawMessage);

        verify(workloadService, never()).processWorkload(any());
    }

    @Test
    @DisplayName("invalid message does not throw — session commits, message is consumed once")
    void onMessage_invalidRequest_doesNotThrow() {
        ConstraintViolation<WorkloadRequest> v = violation();
        when(validator.validate(any(WorkloadRequest.class))).thenReturn(Set.of(v));

        assertDoesNotThrow(() -> listener.onMessage(invalidRequest(), rawMessage));
    }

    @Test
    @DisplayName("infrastructure failure rethrows to trigger session rollback and broker redelivery")
    void onMessage_serviceThrows_rethrowsForRedelivery() {
        when(validator.validate(any(WorkloadRequest.class))).thenReturn(Set.of());
        doThrow(new RuntimeException("DB unavailable")).when(workloadService).processWorkload(any());

        assertThrows(RuntimeException.class, () -> listener.onMessage(validRequest(), rawMessage));
    }

    @Test
    @DisplayName("X-Transaction-Id JMS property is put into MDC during processing")
    void onMessage_withTransactionId_setsMdcDuringProcessing() throws JMSException {
        when(rawMessage.getStringProperty("X-Transaction-Id")).thenReturn("tx-abc");
        when(validator.validate(any())).thenReturn(Set.of());

        String[] capturedMdc = new String[1];
        doAnswer(inv -> { capturedMdc[0] = MDC.get("transactionId"); return null; })
                .when(workloadService).processWorkload(any());

        listener.onMessage(validRequest(), rawMessage);

        assertEquals("tx-abc", capturedMdc[0], "MDC must be set with the JMS transaction ID during processWorkload");
    }

    @Test
    @DisplayName("MDC transaction ID is cleared after successful processing")
    void onMessage_clearsMdcAfterSuccess() {
        when(validator.validate(any())).thenReturn(Set.of());

        listener.onMessage(validRequest(), rawMessage);

        assertNull(MDC.get("transactionId"), "MDC must be cleared after onMessage returns");
    }

    @Test
    @DisplayName("MDC transaction ID is cleared even when processing throws")
    void onMessage_clearsMdcAfterException() throws JMSException {
        when(rawMessage.getStringProperty("X-Transaction-Id")).thenReturn("tx-xyz");
        when(validator.validate(any())).thenReturn(Set.of());
        doThrow(new RuntimeException("forced failure")).when(workloadService).processWorkload(any());

        assertThrows(RuntimeException.class, () -> listener.onMessage(validRequest(), rawMessage));

        assertNull(MDC.get("transactionId"), "MDC must be cleared even when processing throws");
    }


    private WorkloadRequest validRequest() {
        WorkloadRequest req = new WorkloadRequest();
        req.setTrainerUsername("john.doe");
        req.setFirstName("John");
        req.setLastName("Doe");
        req.setIsActive(true);
        req.setTrainingDate(LocalDate.of(2024, 6, 1));
        req.setTrainingDuration(60);
        req.setActionType(WorkloadRequest.ActionType.ADD);
        return req;
    }

    private WorkloadRequest invalidRequest() {
        return new WorkloadRequest();  // all fields null — fails @NotBlank / @NotNull
    }

    @SuppressWarnings("unchecked")
    private ConstraintViolation<WorkloadRequest> violation() {
        ConstraintViolation<WorkloadRequest> v = mock(ConstraintViolation.class);
        Path path = mock(Path.class);
        when(path.toString()).thenReturn("trainerUsername");
        when(v.getPropertyPath()).thenReturn(path);
        when(v.getMessage()).thenReturn("must not be blank");
        return v;
    }
}
