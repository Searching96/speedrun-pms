package org.f3.postalmanagement.exception;

/**
 * Exception thrown when a requested resource is not found.
 * This will be mapped to HTTP 404 Not Found.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
