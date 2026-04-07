package com.gymcrm.workload.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymcrm.workload.dto.WorkloadRequest;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.region.policy.IndividualDeadLetterStrategy;
import org.apache.activemq.broker.region.policy.PolicyEntry;
import org.apache.activemq.broker.region.policy.PolicyMap;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import java.util.List;
import java.util.Map;

/**
 * JMS consumer configuration.
 *
 * Configures ActiveMQ with retry + DLQ handling, JSON message conversion,
 * and concurrent message consumption. Also provides an embedded broker
 * for local development.
 */
@Configuration
@EnableJms
public class JmsConfig {

    @Value("${spring.activemq.broker-url}")
    private String brokerUrl;

    @Value("${spring.activemq.user:#{null}}")
    private String user;

    @Value("${spring.activemq.password:#{null}}")
    private String password;

    @Value("${workload.jms.concurrency:1-5}")
    private String concurrency;

    /**
     * ActiveMQ connection factory with custom redelivery policy.
     *
     * Defining this bean overrides Spring Boot auto-config and allows
     * control over retry behavior.
     */
    @Bean
    public ActiveMQConnectionFactory activeMQConnectionFactory() {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);
        if (user != null) factory.setUserName(user);
        if (password != null) factory.setPassword(password);
        factory.setRedeliveryPolicy(redeliveryPolicy());
        return factory;
    }

    /**
     * Retry policy with exponential backoff.
     *
     * Retries 6 times (1s → 2s → 4s up to 60s), then sends message to DLQ.
     */
    private RedeliveryPolicy redeliveryPolicy() {
        RedeliveryPolicy policy = new RedeliveryPolicy();
        policy.setInitialRedeliveryDelay(1_000L);
        policy.setUseExponentialBackOff(true);
        policy.setBackOffMultiplier(2.0);
        policy.setMaximumRedeliveries(6);
        policy.setMaximumRedeliveryDelay(60_000L);
        return policy;
    }

    /**
     * JSON converter mapping "workloadEvent" to {@link WorkloadRequest}.
     *
     * Uses "_type" property for type resolution and avoids sharing DTO classes
     * across services.
     */
    @Bean
    public MessageConverter jacksonJmsMessageConverter(ObjectMapper objectMapper) {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        converter.setTypeIdMappings(Map.of("workloadEvent", WorkloadRequest.class));
        converter.setObjectMapper(objectMapper);
        return converter;
    }

    /**
     * Main listener factory.
     *
     * - Transacted session → enables retry on failure
     * - Configurable concurrency → supports horizontal scaling
     * - Errors trigger redelivery or DLQ routing
     */
    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(
            ActiveMQConnectionFactory connectionFactory,
            MessageConverter messageConverter) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        factory.setSessionTransacted(true);
        factory.setConcurrency(concurrency);
        factory.setErrorHandler(t ->
                LoggerFactory.getLogger(JmsConfig.class)
                        .error("JMS container error — message will be redelivered or sent to DLQ", t));
        return factory;
    }

    /**
     * Secondary factory used by {@code DeadLetterQueueListener}.
     *
     * <p>Configured as non-transacted to prevent failed DLQ messages from being
     * redelivered indefinitely. DLQ messages have already exhausted retries, so
     * further failures should not trigger another retry cycle.
     *
     * <p>Runs single-threaded since DLQ processing is not performance-critical and
     * simpler, sequential handling makes debugging and inspection easier.
     */

    @Bean
    public DefaultJmsListenerContainerFactory dlqListenerContainerFactory(
            ActiveMQConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setConcurrency("1-1");
        factory.setSessionTransacted(false);
        return factory;
    }

    // ── Embedded broker (local profile only) ──────────────────────────────────

    /**
     * Starts an embedded ActiveMQ broker for local development, exposing a TCP endpoint
     * so multiple services can connect to the same instance.
     *
     * <p>Runs in the workload service to ensure the broker is available before producers connect.
     * Uses TCP (rather than vm://) to allow communication across JVMs, and configures per-queue DLQs
     * (e.g., DLQ.trainer.workload.queue) for easier monitoring and replay.
     */
    @Bean(initMethod = "start", destroyMethod = "stop")
    @Profile("local")
    public BrokerService embeddedBroker() throws Exception {
        BrokerService broker = new BrokerService();
        broker.setBrokerName("localhost");
        broker.setPersistent(false);
        broker.setUseJmx(false);
        broker.addConnector("tcp://localhost:61616");

        IndividualDeadLetterStrategy dlqStrategy = new IndividualDeadLetterStrategy();
        dlqStrategy.setQueuePrefix("DLQ.");
        dlqStrategy.setUseQueueForQueueMessages(true);

        PolicyEntry policyEntry = new PolicyEntry();
        policyEntry.setQueue(">");   // applies to all queues
        policyEntry.setDeadLetterStrategy(dlqStrategy);

        PolicyMap policyMap = new PolicyMap();
        policyMap.setPolicyEntries(List.of(policyEntry));

        broker.setDestinationPolicy(policyMap);
        return broker;
    }
}
