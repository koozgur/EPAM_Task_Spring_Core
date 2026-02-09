package com.gymcrm.exception;

/**
 * Thrown when an action conflicts with current entity state.
 */
public class StateConflictException extends GymCrmException {

    public StateConflictException(String message) {
        super(message);
    }
}
