package org.f3.postalmanagement.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.f3.postalmanagement.dto.response.order.TrackingEventResponse;
import org.f3.postalmanagement.dto.response.order.TrackingResponse;
import org.f3.postalmanagement.service.TrackingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/tracking")
@RequiredArgsConstructor
@Tag(name = "Tracking", description = "API for package tracking")
public class TrackingController {

    private final TrackingService trackingService;

    @GetMapping("/{trackingNumber}")
    @Operation(summary = "Track order", description = "Public API to track order by tracking number")
    public ResponseEntity<TrackingResponse> trackOrder(@PathVariable String trackingNumber) {
        return ResponseEntity.ok(trackingService.getTrackingInfo(trackingNumber));
    }

    @PostMapping("/events")
    @PreAuthorize("hasAnyRole('PO_STAFF', 'WH_STAFF', 'SHIPPER', 'PO_WARD_MANAGER', 'PO_PROVINCE_ADMIN', 'WH_PROVINCE_ADMIN')")
    @Operation(summary = "Add tracking event", description = "Staff/Shipper adds a tracking event (scan)")
    public ResponseEntity<TrackingEventResponse> addEvent(@RequestBody CreateEventRequest request) {
        return ResponseEntity.ok(trackingService.addTrackingEvent(
                request.getOrderId(), 
                request.getStatus(), 
                request.getDescription(), 
                request.getLocationName()
        ));
    }
    
    @lombok.Data
    static class CreateEventRequest {
        private UUID orderId;
        private String status;
        private String description;
        private String locationName;
    }
}
