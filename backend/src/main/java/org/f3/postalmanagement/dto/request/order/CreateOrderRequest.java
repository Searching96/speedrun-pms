package org.f3.postalmanagement.dto.request.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateOrderRequest {

    // -- Sender Info
    @NotBlank(message = "Sender name is required")
    private String senderName;

    @NotBlank(message = "Sender phone is required")
    private String senderPhone;

    @NotBlank(message = "Sender address is required")
    private String senderAddress;
    
    @NotBlank(message = "Sender ward code is required")
    private String senderWardCode;

    // -- Receiver Info
    @NotBlank(message = "Receiver name is required")
    private String receiverName;

    @NotBlank(message = "Receiver phone is required")
    private String receiverPhone;

    @NotBlank(message = "Receiver address is required")
    private String receiverAddress;

    @NotBlank(message = "Receiver ward code is required")
    private String receiverWardCode;

    // -- Package Info
    @NotNull(message = "Weight is required")
    @Positive(message = "Weight must be positive")
    private BigDecimal weightKg;

    @Positive(message = "Length must be positive")
    private Integer lengthCm;

    @Positive(message = "Width must be positive")
    private Integer widthCm;

    @Positive(message = "Height must be positive")
    private Integer heightCm;

    private String description;

    // -- Pricing (Backend calculates this automatically if not provided)
    // Frontend can optionally provide a pre-calculated fee for display purposes
    private BigDecimal shippingFee;

    private BigDecimal codAmount;
}
