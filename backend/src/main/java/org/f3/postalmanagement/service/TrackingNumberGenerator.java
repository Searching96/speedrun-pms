package org.f3.postalmanagement.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Service for generating unique tracking numbers for orders.
 * Uses atomic counter to prevent race conditions in concurrent environments.
 */
@Slf4j
@Service
public class TrackingNumberGenerator {
    
    private static final String PREFIX = "VN";
    private static final int SEQUENCE_MODULO = 10000;
    
    private final AtomicLong counter = new AtomicLong(0);
    
    /**
     * Generates a unique tracking number.
     * Format: VN{timestamp}{4-digit-sequence}
     * Example: VN17369123450001
     * 
     * @return unique tracking number
     */
    public String generate() {
        long timestamp = System.currentTimeMillis();
        long sequence = counter.incrementAndGet() % SEQUENCE_MODULO;
        String trackingNumber = String.format("%s%d%04d", PREFIX, timestamp, sequence);
        
        log.debug("Generated tracking number: {}", trackingNumber);
        return trackingNumber;
    }
    
    /**
     * Validates tracking number format.
     * 
     * @param trackingNumber the tracking number to validate
     * @return true if valid format, false otherwise
     */
    public boolean isValidFormat(String trackingNumber) {
        if (trackingNumber == null || trackingNumber.isBlank()) {
            return false;
        }
        
        // Format: VN + 13 digits (timestamp) + 4 digits (sequence) = VN + 17 digits
        return trackingNumber.matches("^VN\\d{17}$");
    }
}
