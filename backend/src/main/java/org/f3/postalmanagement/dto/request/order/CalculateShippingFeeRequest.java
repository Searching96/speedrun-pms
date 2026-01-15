package org.f3.postalmanagement.dto.request.order;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CalculateShippingFeeRequest {
    
    @NotNull(message = "Sender ward code is required")
    private String senderWardCode;
    
    @NotNull(message = "Receiver ward code is required")
    private String receiverWardCode;
    
    @NotNull(message = "Weight is required")
    @Positive(message = "Weight must be positive")
    private BigDecimal weightKg;
    
    @Positive(message = "Length must be positive")
    private Integer lengthCm;
    
    @Positive(message = "Width must be positive")
    private Integer widthCm;
    
    @Positive(message = "Height must be positive")
    private Integer heightCm;
}
