package com.gymcrm.exception;

/**
 * Base unchecked exception for Gym CRM domain errors.
 */
public class GymCrmException extends RuntimeException {

    public GymCrmException(String message) {
        super(message);
    }

    public GymCrmException(String message, Throwable cause) {
        super(message, cause);
    }
}
