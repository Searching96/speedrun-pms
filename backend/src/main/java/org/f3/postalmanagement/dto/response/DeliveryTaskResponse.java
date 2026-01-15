package org.f3.postalmanagement.dto.response;

import lombok.Builder;
import lombok.Data;
import org.f3.postalmanagement.enums.TaskStatus;
import org.f3.postalmanagement.enums.TaskType;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class DeliveryTaskResponse {
    private UUID id;
    private UUID orderId;
    private String orderTrackingNumber;
    private String shipperId;
    private String shipperName;
    
    private TaskType taskType;
    private String status;
    
    private String address;
    private String wardCode;
    private String contactName;
    private String contactPhone;
    
    private LocalDateTime assignedAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    
    private String notes;
    private String photoProofUrl;
}
