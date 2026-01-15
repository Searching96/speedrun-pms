package org.f3.postalmanagement.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.f3.postalmanagement.dto.request.order.CalculateShippingFeeRequest;
import org.f3.postalmanagement.dto.request.order.CreateOrderRequest;
import org.f3.postalmanagement.entity.ApiResponse;
import org.f3.postalmanagement.dto.response.order.OrderResponse;
import org.f3.postalmanagement.service.OrderService;
import org.f3.postalmanagement.service.ShippingFeeCalculator;

import java.math.BigDecimal;
import org.f3.postalmanagement.dto.response.order.PublicOrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Order Management", description = "API for managing shipping orders")
public class OrderController {

    private final OrderService orderService;
    private final ShippingFeeCalculator shippingFeeCalculator;

    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'PO_STAFF')")
    @Operation(summary = "Create a new order", description = "Create a new shipping order. Customers create for themselves.")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        return ResponseEntity.ok(orderService.createOrder(request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'PO_STAFF')")
    @Operation(summary = "Get my orders", description = "Get orders for current customer or office")
    public ResponseEntity<Page<OrderResponse>> getMyOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String order
    ) {
        Sort sort = order.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(orderService.getMyOrders(pageable));
    }

    @GetMapping("/{trackingNumber}")
    @Operation(summary = "Get order by tracking number", description = "Public access to track basic order info")
    public ResponseEntity<PublicOrderResponse> getOrderByTrackingNumber(@PathVariable String trackingNumber) {
        return ResponseEntity.ok(orderService.getPublicOrderByTrackingNumber(trackingNumber));
    }

    @PostMapping("/calculate-fee")
    @Operation(summary = "Calculate shipping fee", description = "Calculate shipping fee based on weight, dimensions, and zones")
    public ResponseEntity<ApiResponse<BigDecimal>> calculateShippingFee(
            @Valid @RequestBody CalculateShippingFeeRequest request
    ) {
        BigDecimal fee = shippingFeeCalculator.calculateFee(
            request.getSenderWardCode(),
            request.getReceiverWardCode(),
            request.getWeightKg(),
            request.getLengthCm(),
            request.getWidthCm(),
            request.getHeightCm()
        );
        return ResponseEntity.ok(ApiResponse.<BigDecimal>builder()
                .success(true)
                .data(fee)
                .build());
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    @Operation(summary = "Get all orders", description = "Admin access to all orders in the system")
    public ResponseEntity<Page<OrderResponse>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String order
    ) {
        Sort sort = order.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(orderService.getAllOrders(pageable));
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'PO_STAFF')")
    @Operation(summary = "Cancel order", description = "Cancel a pending order")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.cancelOrder(id));
    }
}
