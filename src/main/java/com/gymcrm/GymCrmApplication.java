package com.gymcrm;

import com.gymcrm.config.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class GymCrmApplication {
    
    private static final Logger logger = LoggerFactory.getLogger(GymCrmApplication.class);
    
    public static void main(String[] args) {
        logger.info("Starting Gym CRM Application...");
        
        // Initialize Spring Application Context
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        
        logger.info("Spring Application Context initialized successfully");
        logger.info("Loaded beans: {}", context.getBeanDefinitionCount());
        
        // Display all registered beans
        String[] beanNames = context.getBeanDefinitionNames();
        logger.info("Available beans:");
        for (String beanName : beanNames) {
            logger.info("  - {}", beanName);
        }
        
        logger.info("Gym CRM Application is ready!");
        
        // Close the context
        ((AnnotationConfigApplicationContext) context).close();
        logger.info("Application context closed");
    }
}
