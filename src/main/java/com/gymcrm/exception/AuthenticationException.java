package com.gymcrm.exception;

/**
 * Domain authentication failure (wrong password, locked account, etc.).
 */
public class AuthenticationException extends GymCrmException {

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
