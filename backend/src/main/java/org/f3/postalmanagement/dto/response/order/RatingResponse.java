package org.f3.postalmanagement.dto.response.order;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class RatingResponse {
    private UUID id;
    private UUID orderId;
    // Simple response
    private Integer overallRating;
    private Integer deliverySpeedRating;
    private Integer shipperAttitudeRating;
    private String comment;
    private LocalDateTime createdAt;
}
