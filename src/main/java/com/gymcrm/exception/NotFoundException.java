package com.gymcrm.exception;

/**
 * Thrown when a requested resource cannot be found.
 */
public class NotFoundException extends GymCrmException {

    public NotFoundException(String message) {
        super(message);
    }
}
