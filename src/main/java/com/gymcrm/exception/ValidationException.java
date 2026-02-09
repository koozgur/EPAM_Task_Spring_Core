package com.gymcrm.exception;

/**
 * Thrown when input validation fails.
 */
public class ValidationException extends GymCrmException {

    public ValidationException(String message) {
        super(message);
    }
}
