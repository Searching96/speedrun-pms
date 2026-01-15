package org.f3.postalmanagement.dto.response;

import lombok.Builder;
import lombok.Data;
import org.f3.postalmanagement.enums.TimeSlot;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class PickupRequestResponse {
    private UUID id;
    private UUID orderId;
    private String orderTrackingNumber;
    
    private String pickupAddress;
    private String pickupWardCode;
    private String pickupContactName;
    private String pickupContactPhone;
    
    private LocalDate preferredDate;
    private TimeSlot preferredTimeSlot;
    
    private String status;
    private UUID assignedShipperId;
    private String assignedShipperName;
    
    private LocalDateTime createdAt;
    private LocalDateTime assignedAt;
    private LocalDateTime completedAt;
}
