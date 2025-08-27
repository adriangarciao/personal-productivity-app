package com.adriangarciao.person_productivity_app.exception;

/**
 * Exception thrown when a requested resource cannot be found.
 * This is a generic exception that can be used for any entity or resource type.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructs a new {@code ResourceNotFoundException} with the specified detail message.
     *
     * @param message the detail message explaining which resource was not found
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}