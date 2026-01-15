package org.f3.postalmanagement.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.f3.postalmanagement.dto.request.CreateRatingRequest;
import org.f3.postalmanagement.dto.response.RatingResponse;
import org.f3.postalmanagement.service.RatingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
@Tag(name = "Service Ratings", description = "API for customer ratings")
public class RatingController {

    private final RatingService ratingService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Submit rating", description = "Customer rates a delivered order")
    public ResponseEntity<RatingResponse> createRating(@Valid @RequestBody CreateRatingRequest request) {
        return ResponseEntity.ok(ratingService.createRating(request));
    }

    @GetMapping("/order/{orderId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'PO_PROVINCE_ADMIN', 'PO_WARD_MANAGER')") // Admin/Manager can see too
    @Operation(summary = "Get rating by order", description = "Get rating details for an order")
    public ResponseEntity<RatingResponse> getRatingByOrder(@PathVariable UUID orderId) {
        return ResponseEntity.ok(ratingService.getRatingByOrderId(orderId));
    }
}
