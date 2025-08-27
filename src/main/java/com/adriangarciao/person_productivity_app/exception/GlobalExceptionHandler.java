package com.adriangarciao.person_productivity_app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Centralized exception handler for the REST API.
 *
 * Handles application-specific exceptions and common Spring exceptions,
 * returning appropriate HTTP responses with status codes and messages.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles {@link PersonNotFoundException}.
     *
     * @param ex the thrown exception
     * @return a NOT_FOUND (404) response with the exception message
     */
    @ExceptionHandler(PersonNotFoundException.class)
    public ResponseEntity<String> handlePersonNotFound(PersonNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    /**
     * Handles validation errors from {@link MethodArgumentNotValidException}.
     *
     * @param ex the thrown exception
     * @return a BAD_REQUEST (400) response with the first validation error message
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationErrors(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles {@link TaskNotFoundException}.
     *
     * @param ex the thrown exception
     * @return a NOT_FOUND (404) response with the exception message
     */
    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<String> handleTaskNotFound(TaskNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    /**
     * Handles {@link TaskOwnershipException}.
     *
     * @param ex the thrown exception
     * @return a FORBIDDEN (403) response with the exception message
     */
    @ExceptionHandler(TaskOwnershipException.class)
    public ResponseEntity<String> handleTaskOwnershipException(TaskOwnershipException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    /**
     * Handles invalid enum binding in request parameters ({@link MethodArgumentTypeMismatchException}).
     *
     * @param ex the thrown exception
     * @return a BAD_REQUEST (400) response with a message listing allowed enum values
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleEnumBindingErrors(MethodArgumentTypeMismatchException ex) {
        if (ex.getRequiredType() != null && ex.getRequiredType().isEnum()) {
            String enumValues = Arrays.stream(ex.getRequiredType().getEnumConstants())
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));

            String message = String.format("Invalid value '%s'. Allowed values are: %s",
                    ex.getValue(), enumValues);

            return ResponseEntity.badRequest().body(message);
        }
        return ResponseEntity.badRequest().body("Invalid parameter: " + ex.getValue());
    }

    /**
     * Handles {@link ResourceNotFoundException}.
     *
     * @param ex the thrown exception
     * @return a NOT_FOUND (404) response with the exception message
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
