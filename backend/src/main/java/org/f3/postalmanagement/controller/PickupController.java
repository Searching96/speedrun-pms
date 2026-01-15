package org.f3.postalmanagement.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.f3.postalmanagement.dto.request.order.CreatePickupRequest;
import org.f3.postalmanagement.dto.response.order.PickupRequestResponse;
import org.f3.postalmanagement.service.PickupRequestService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/pickup-requests")
@RequiredArgsConstructor
@Tag(name = "Pickup Management", description = "API for managing pickup requests")
public class PickupController {

    private final PickupRequestService pickupRequestService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Create pickup request", description = "Customer requests a pickup for an order")
    public ResponseEntity<PickupRequestResponse> createPickupRequest(@Valid @RequestBody CreatePickupRequest request) {
        return ResponseEntity.ok(pickupRequestService.createPickupRequest(request));
    }

    @GetMapping("/my-requests")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Get my pickup requests", description = "Get pickup requests for current customer")
    public ResponseEntity<Page<PickupRequestResponse>> getMyRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(pickupRequestService.getMyRequests(PageRequest.of(page, size, Sort.by("createdAt").descending())));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('PO_PROVINCE_ADMIN', 'PO_WARD_MANAGER', 'PO_STAFF')")
    @Operation(summary = "Get pending requests", description = "Get pending pickup requests by ward code")
    public ResponseEntity<List<PickupRequestResponse>> getPendingRequests(@RequestParam String wardCode) {
        // In real app, verify manager belongs to this ward
        return ResponseEntity.ok(pickupRequestService.getPendingRequestsByWard(wardCode));
    }

    @PutMapping("/{id}/assign")
    @PreAuthorize("hasRole('PO_WARD_MANAGER')")
    @Operation(summary = "Assign shipper", description = "Assign a shipper to a pickup request")
    public ResponseEntity<PickupRequestResponse> assignShipper(@PathVariable UUID id, @RequestBody AssignShipperRequest request) {
        return ResponseEntity.ok(pickupRequestService.assignShipper(id, request.getShipperId()));
    }

    @lombok.Data
    static class AssignShipperRequest {
        private UUID shipperId;
    }
}
