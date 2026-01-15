package org.f3.postalmanagement.dto.request.order;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.f3.postalmanagement.enums.TimeSlot;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class CreatePickupRequest {

    @NotNull(message = "Order ID is required")
    private UUID orderId;

    @NotBlank(message = "Pickup address is required")
    private String pickupAddress;

    @NotBlank(message = "Pickup ward code is required")
    private String pickupWardCode;

    @NotBlank(message = "Contact name is required")
    private String pickupContactName;
    
    @NotBlank(message = "Contact phone is required")
    private String pickupContactPhone;

    @NotNull(message = "Preferred date is required")
    @FutureOrPresent(message = "Date must be present or future")
    private LocalDate preferredDate;

    private TimeSlot preferredTimeSlot;
}
