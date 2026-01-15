package org.f3.postalmanagement.dto.response.order;

import lombok.Builder;
import lombok.Data;
import org.f3.postalmanagement.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class OrderResponse {
    private UUID id;
    private String trackingNumber;
    private String status;
    
    private String senderName;
    private String senderPhone;
    private String senderAddress;
    private String senderWardCode;
    
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private String receiverWardCode;
    
    private BigDecimal weightKg;
    private String description;
    
    private BigDecimal shippingFee;
    private BigDecimal codAmount;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
