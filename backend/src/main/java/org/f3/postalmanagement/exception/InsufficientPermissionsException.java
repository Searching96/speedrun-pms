package org.f3.postalmanagement.exception;

/**
 * Exception thrown when a user attempts to access a resource they don't have permission for.
 */
public class InsufficientPermissionsException extends RuntimeException {
    
    public InsufficientPermissionsException(String message) {
        super(message);
    }
    
    public InsufficientPermissionsException(String message, Throwable cause) {
        super(message, cause);
    }
}
