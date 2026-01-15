package org.f3.postalmanagement.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.f3.postalmanagement.dto.response.DeliveryTaskResponse;
import org.f3.postalmanagement.service.DeliveryTaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/shipper/tasks")
@RequiredArgsConstructor
@Tag(name = "Shipper Management", description = "API for shippers to manage tasks")
public class ShipperController {

    private final DeliveryTaskService deliveryTaskService;

    @GetMapping
    @PreAuthorize("hasRole('SHIPPER')")
    @Operation(summary = "Get my tasks", description = "Get all tasks assigned to current shipper")
    public ResponseEntity<List<DeliveryTaskResponse>> getMyTasks() {
        return ResponseEntity.ok(deliveryTaskService.getMyTasks());
    }

    @PutMapping("/{id}/start")
    @PreAuthorize("hasRole('SHIPPER')")
    @Operation(summary = "Start task", description = "Mark a task as IN_PROGRESS")
    public ResponseEntity<DeliveryTaskResponse> startTask(@PathVariable UUID id) {
        return ResponseEntity.ok(deliveryTaskService.startTask(id));
    }

    @PutMapping("/{id}/complete")
    @PreAuthorize("hasRole('SHIPPER')")
    @Operation(summary = "Complete task", description = "Mark a task as COMPLETED")
    public ResponseEntity<DeliveryTaskResponse> completeTask(@PathVariable UUID id, @RequestBody CompleteTaskRequest request) {
        return ResponseEntity.ok(deliveryTaskService.completeTask(id, request.getNotes(), request.getPhotoProofUrl()));
    }
    
    @PutMapping("/{id}/fail")
    @PreAuthorize("hasRole('SHIPPER')")
    @Operation(summary = "Fail task", description = "Mark a task as FAILED")
    public ResponseEntity<DeliveryTaskResponse> failTask(@PathVariable UUID id, @RequestBody FailTaskRequest request) {
        return ResponseEntity.ok(deliveryTaskService.failTask(id, request.getReason()));
    }

    @lombok.Data
    static class CompleteTaskRequest {
        private String notes;
        private String photoProofUrl;
    }
    
    @lombok.Data
    static class FailTaskRequest {
        private String reason;
    }
}
