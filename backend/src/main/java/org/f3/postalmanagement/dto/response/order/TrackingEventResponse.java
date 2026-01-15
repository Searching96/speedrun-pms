package org.f3.postalmanagement.dto.response.order;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class TrackingEventResponse {
    private UUID id;
    private String status;
    private String description;
    private String locationName;
    private UUID officeId;
    private LocalDateTime eventTime;
}
