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

    // -- Pricing (Calculated by backend normally, but maybe provided by frontend if they have a calc? 
    // Plan: "Pricing... shipping_fee... NOT NULL". 
    // Usually backend calculates this. But to keep it simple, let's accept it from request OR calculate it.
    // Let's assume frontend sends calculated fee or 0 for now, or we define a simple rule.
    // I'll make it optional in request and calculate defaults if missing, OR require it.
    // Let's require it to match the schema "NOT NULL" immediately, assuming frontend estimates it.
    @NotNull(message = "Shipping fee is required") // In real app, backend calculates.
    private BigDecimal shippingFee;

    private BigDecimal codAmount;
}
