package com.gymcrm.service;

import com.gymcrm.dto.request.TrainerWorkloadRequest;
import com.gymcrm.model.Trainer;
import com.gymcrm.model.Training;
import com.gymcrm.model.User;
import jakarta.jms.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.jms.UncategorizedJmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("WorkloadNotificationService")
class WorkloadNotificationServiceTest {

    private static final String QUEUE = "trainer.workload.queue";

    @Mock
    private JmsTemplate jmsTemplate;

    private WorkloadNotificationService service;

    @BeforeEach
    void setUp() {
        service = new WorkloadNotificationService(jmsTemplate, QUEUE);
    }

    @Test
    @DisplayName("notifyAdd publishes ADD action to the configured queue")
    void notifyAdd_publishesAddAction() {
        service.notifyAdd(training());

        ArgumentCaptor<TrainerWorkloadRequest> captor =
                ArgumentCaptor.forClass(TrainerWorkloadRequest.class);
        verify(jmsTemplate).convertAndSend(eq(QUEUE), captor.capture(), any(MessagePostProcessor.class));
        assertEquals(TrainerWorkloadRequest.ActionType.ADD, captor.getValue().getActionType());
    }

    @Test
    @DisplayName("notifyDelete publishes DELETE action to the configured queue")
    void notifyDelete_publishesDeleteAction() {
        service.notifyDelete(training());

        ArgumentCaptor<TrainerWorkloadRequest> captor =
                ArgumentCaptor.forClass(TrainerWorkloadRequest.class);
        verify(jmsTemplate).convertAndSend(eq(QUEUE), captor.capture(), any(MessagePostProcessor.class));
        assertEquals(TrainerWorkloadRequest.ActionType.DELETE, captor.getValue().getActionType());
    }

    @Test
    @DisplayName("notifyAdd maps training fields onto the published request correctly")
    void notifyAdd_mapsTrainingFields() {
        service.notifyAdd(training());

        ArgumentCaptor<TrainerWorkloadRequest> captor =
                ArgumentCaptor.forClass(TrainerWorkloadRequest.class);
        verify(jmsTemplate).convertAndSend(eq(QUEUE), captor.capture(), any(MessagePostProcessor.class));

        TrainerWorkloadRequest req = captor.getValue();
        assertAll(
            () -> assertEquals("john.doe",              req.getTrainerUsername()),
            () -> assertEquals("John",                  req.getFirstName()),
            () -> assertEquals("Doe",                   req.getLastName()),
            () -> assertEquals(LocalDate.of(2024, 6, 1), req.getTrainingDate()),
            () -> assertEquals(60,                       req.getTrainingDuration())
        );
    }

    @Test
    @DisplayName("notifyAdd sets X-Transaction-Id JMS property when MDC contains a transaction ID")
    void notifyAdd_withMdcTransactionId_setsJmsProperty() throws Exception {
        MDC.put("transactionId", "tx-abc-123");
        try {
            ArgumentCaptor<MessagePostProcessor> captor =
                    ArgumentCaptor.forClass(MessagePostProcessor.class);
            service.notifyAdd(training());
            verify(jmsTemplate).convertAndSend(eq(QUEUE), any(), captor.capture());

            Message msg = mock(Message.class);
            captor.getValue().postProcessMessage(msg);

            verify(msg).setStringProperty("X-Transaction-Id", "tx-abc-123");
        } finally {
            MDC.remove("transactionId");
        }
    }

    @Test
    @DisplayName("notifyAdd omits X-Transaction-Id JMS property when MDC has no transaction ID")
    void notifyAdd_withoutMdcTransactionId_omitsJmsProperty() throws Exception {
        MDC.remove("transactionId");
        ArgumentCaptor<MessagePostProcessor> captor =
                ArgumentCaptor.forClass(MessagePostProcessor.class);
        service.notifyAdd(training());
        verify(jmsTemplate).convertAndSend(eq(QUEUE), any(), captor.capture());

        Message msg = mock(Message.class);
        captor.getValue().postProcessMessage(msg);

        verify(msg, never()).setStringProperty(anyString(), anyString());
    }

    @Test
    @DisplayName("notifyAdd does not propagate JmsException — fire-and-forget semantics")
    void notifyAdd_whenJmsThrows_doesNotPropagate() {
        doThrow(new UncategorizedJmsException("broker down", null))
                .when(jmsTemplate).convertAndSend(anyString(), any(Object.class), any(MessagePostProcessor.class));

        assertDoesNotThrow(() -> service.notifyAdd(training()));
    }


    private Training training() {
        User user = new User();
        user.setUsername("john.doe");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setIsActive(true);
        user.setPassword("pw");

        Trainer trainer = new Trainer();
        trainer.setUser(user);

        Training t = new Training();
        t.setTrainer(trainer);
        t.setTrainingDate(LocalDate.of(2024, 6, 1));
        t.setTrainingDuration(60);
        return t;
    }
}
