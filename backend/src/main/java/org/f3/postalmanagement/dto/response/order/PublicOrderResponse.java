package org.f3.postalmanagement.dto.response.order;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Publicly accessible order information with sensitive PII masked.
 */
@Data
@Builder
public class PublicOrderResponse {
    private UUID id;
    private String trackingNumber;
    private String status;
    private String senderName; // Usually okay to show name, but we can mask if needed
    private String receiverName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
