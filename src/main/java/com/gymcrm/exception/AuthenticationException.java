package com.gymcrm.exception;

/**
 * Thrown when authentication or credential validation fails.
 */
public class AuthenticationException extends GymCrmException {

    public AuthenticationException(String message) {
        super(message);
    }
}
