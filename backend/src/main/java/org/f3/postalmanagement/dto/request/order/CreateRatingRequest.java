package org.f3.postalmanagement.dto.request.order;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateRatingRequest {
    
    @NotNull(message = "Order ID is required")
    private UUID orderId;
    
    @NotNull(message = "Overall rating is required")
    @Min(1) @Max(5)
    private Integer overallRating;
    
    @Min(1) @Max(5)
    private Integer deliverySpeedRating;
    
    @Min(1) @Max(5)
    private Integer shipperAttitudeRating;
    
    private String comment;
}
