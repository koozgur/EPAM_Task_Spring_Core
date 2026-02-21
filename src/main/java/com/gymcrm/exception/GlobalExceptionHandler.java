package com.gymcrm.exception;

import com.gymcrm.dto.response.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Centralized REST exception handler that converts application and framework
 * exceptions to consistent HTTP responses using ErrorResponse.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 400 — Bean validation errors (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        Map<String, String> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        DefaultMessageSourceResolvable::getDefaultMessage,
                        (existing, replacement) -> existing
                ));

        logger.warn("[{}] Validation failed on {}",
                MDC.get("transactionId"), request.getRequestURI());

        return build(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                request,
                fieldErrors
        );
    }

    // 400 — Domain validation
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            ValidationException ex,
            HttpServletRequest request) {

        logger.warn("[{}] Validation error on {}: {}",
                MDC.get("transactionId"),
                request.getRequestURI(),
                ex.getMessage());

        return build(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request,
                null
        );
    }

    // 401 — Authentication failure
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(
            AuthenticationException ex,
            HttpServletRequest request) {

        logger.warn("[{}] Authentication failure on {}",
                MDC.get("transactionId"),
                request.getRequestURI());

        return build(
                HttpStatus.UNAUTHORIZED,
                ex.getMessage(),
                request,
                null
        );
    }

    // 404 — Not found
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            NotFoundException ex,
            HttpServletRequest request) {

        logger.warn("[{}] Resource not found on {}",
                MDC.get("transactionId"),
                request.getRequestURI());

        return build(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request,
                null
        );
    }

    // 405 — Method not allowed
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotAllowed(
            HttpRequestMethodNotSupportedException ex,
            HttpServletRequest request) {

        logger.warn("[{}] Method not allowed on {}",
                MDC.get("transactionId"),
                request.getRequestURI());

        return build(
                HttpStatus.METHOD_NOT_ALLOWED,
                ex.getMessage(),
                request,
                null
        );
    }

    // 409 — State conflict
    @ExceptionHandler(StateConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(
            StateConflictException ex,
            HttpServletRequest request) {

        logger.warn("[{}] State conflict on {}",
                MDC.get("transactionId"),
                request.getRequestURI());

        return build(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                request,
                null
        );
    }

    // 500 — Catch-all
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(
            Exception ex,
            HttpServletRequest request) {

        logger.error("[{}] Unhandled exception on {}",
                MDC.get("transactionId"),
                request.getRequestURI(),
                ex);

        return build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred.",
                request,
                null
        );
    }

    // Central builder
    private ResponseEntity<ErrorResponse> build(
            HttpStatus status,
            String message,
            HttpServletRequest request,
            Map<String, String> fieldErrors) {

        ErrorResponse body = new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI(),
                MDC.get("transactionId"),
                Instant.now(),
                fieldErrors
        );

        return ResponseEntity.status(status).body(body);
    }
}