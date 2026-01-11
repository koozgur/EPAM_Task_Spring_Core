package com.gymcrm.config;

import com.gymcrm.model.Trainee;
import com.gymcrm.model.Trainer;
import com.gymcrm.model.Training;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Main Spring configuration class.
 * Defines storage beans as separate Spring beans for each entity type.
 * Each storage map is a separate Spring bean as per requirement.
 */
@Configuration
@ComponentScan(basePackages = "com.gymcrm")
@PropertySource("classpath:application.properties")
public class AppConfig {
    
    /**
     * Trainee storage bean - ConcurrentHashMap for thread-safe operations
     * @return Map storing Trainee entities by their ID
     */
    @Bean
    public Map<Long, Trainee> traineeStorage() {
        return new ConcurrentHashMap<>();
    }
    
    /**
     * Trainer storage bean - ConcurrentHashMap for thread-safe operations
     * @return Map storing Trainer entities by their ID
     */
    @Bean
    public Map<Long, Trainer> trainerStorage() {
        return new ConcurrentHashMap<>();
    }
    
    /**
     * Training storage bean - ConcurrentHashMap for thread-safe operations
     * @return Map storing Training entities by their ID
     */
    @Bean
    public Map<Long, Training> trainingStorage() {
        return new ConcurrentHashMap<>();
    }
}
