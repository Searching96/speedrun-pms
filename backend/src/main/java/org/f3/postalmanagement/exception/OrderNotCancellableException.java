package org.f3.postalmanagement.exception;

/**
 * Exception thrown when an order cannot be cancelled due to its current status.
 */
public class OrderNotCancellableException extends RuntimeException {
    
    public OrderNotCancellableException(String message) {
        super(message);
    }
    
    public OrderNotCancellableException(String message, Throwable cause) {
        super(message, cause);
    }
}
