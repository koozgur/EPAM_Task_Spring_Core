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
     * - Maps "workloadEvent" to {@link TrainerWorkloadRequest}
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
     * JmsTemplate wired with the JSON converter and {@code DeliveryMode.PERSISTENT}.
     *
     * <p>Actual durability depends on the broker: the embedded local broker runs with
     * {@code persistent=false}, so messages are memory-only locally; external brokers
     * in dev/stg/prod with a persistent store (e.g. KahaDB) will honour the flag.
     *
     * <p>Injects Spring Boot's autoconfigured {@code CachingConnectionFactory} for
     * connection/session pooling on the producer side.
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
