package com.gymcrm.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymcrm.dto.request.TrainerWorkloadRequest;
import jakarta.jms.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import java.util.Map;

/**
 * JMS producer configuration.
 *
 * Configures a {@link JmsTemplate} that sends messages as JSON.
 *
 * Uses a logical type alias ("workloadEvent") instead of class names to keep
 * services loosely coupled (no shared DTO dependency required).
 */
@Configuration
public class JmsConfig {

    /**
     * JSON message converter.
     *
     * - Uses TEXT messages for readable JSON payloads
     * - Stores type info in "_type" property for deserialization
     * - Maps "workloadEvent"to {@link TrainerWorkloadRequest}
     *
     * Reuses Spring's {@link ObjectMapper} for consistent serialization
     */
    @Bean
    public MessageConverter jacksonJmsMessageConverter(ObjectMapper objectMapper) {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        converter.setTypeIdMappings(Map.of("workloadEvent", TrainerWorkloadRequest.class));
        converter.setObjectMapper(objectMapper);
        return converter;
    }

    /**
     * JmsTemplate wired with the JSON converter and persistent delivery mode.
     *
     * <p>{@code setDeliveryPersistent(true)} instructs the broker to persist
     * each message to disk before acknowledging the sending so that workload events
     * are not lost if the broker restarts between publication and consumption.
     *
     * <p>The {@link ConnectionFactory} injected here is Spring Boot's
     * autoconfigured {@code CachingConnectionFactory} (wrapping
     * {@code ActiveMQConnectionFactory}), which pools connections and sessions
     * for efficient producer-side reuse.
     */
    @Bean
    public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory,
                                   MessageConverter messageConverter) {
        JmsTemplate template = new JmsTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        template.setDeliveryPersistent(true);
        return template;
    }
}
