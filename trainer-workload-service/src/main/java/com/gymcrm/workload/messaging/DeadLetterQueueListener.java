package com.gymcrm.workload.messaging;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * Listener for trainer workload dead-letter queue messages.
 *
 * <p>Consumes messages that failed all retries and were moved to the DLQ.
 * Uses raw {@link Message} since payloads may be invalid or unserializable.
 *
 * <p>Runs with a non-transacted, single-threaded listener to avoid reprocessing
 * and to simplify logging, debugging, and manual intervention.
 */
@Component
public class DeadLetterQueueListener {

    private static final Logger log = LoggerFactory.getLogger(DeadLetterQueueListener.class);

    private static final String TRANSACTION_ID_JMS_PROPERTY = "X-Transaction-Id";

    @JmsListener(destination = "${workload.jms.dlq-name:ActiveMQ.DLQ}",
                 containerFactory = "dlqListenerContainerFactory")
    public void onDeadLetter(Message rawMessage) {
        try {
            String messageId   = rawMessage.getJMSMessageID();
            String body        = extractBody(rawMessage);
            String transactionId = rawMessage.getStringProperty(TRANSACTION_ID_JMS_PROPERTY);

            log.error("DEAD LETTER — manual intervention required " +
                      "[JMSMessageID={}, transactionId={}, body={}]",
                      messageId, transactionId, body);

            //persist, metrics, alert

        } catch (JMSException e) {
            log.error("Failed to read dead-letter message", e);
        }
    }

    private String extractBody(Message message) throws JMSException {
        if (message instanceof TextMessage textMessage) {
            return textMessage.getText();
        }
        return "[non-text message: " + message.getClass().getSimpleName() + "]";
    }
}
