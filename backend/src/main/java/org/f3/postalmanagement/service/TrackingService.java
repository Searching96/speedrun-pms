package org.f3.postalmanagement.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.f3.postalmanagement.dto.response.order.OrderResponse;
import org.f3.postalmanagement.dto.response.order.TrackingEventResponse;
import org.f3.postalmanagement.dto.response.order.TrackingResponse;
import org.f3.postalmanagement.entity.order.Order;
import org.f3.postalmanagement.entity.order.TrackingEvent;
import org.f3.postalmanagement.entity.actor.Account;
import org.f3.postalmanagement.entity.actor.Employee;
import org.f3.postalmanagement.repository.EmployeeRepository;
import org.f3.postalmanagement.repository.OrderRepository;
import org.f3.postalmanagement.repository.TrackingEventRepository;
import org.f3.postalmanagement.utils.SecurityUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrackingService {

    private final TrackingEventRepository trackingEventRepository;
    private final OrderRepository orderRepository;
    private final EmployeeRepository employeeRepository;
    // We reuse OrderService mapper if possible, or duplicate/inject it.
    // Ideally we duplicate mapper logic or make it utility to avoid circular dep if OrderService uses TrackingService later.
    // For now I'll implement mapping logic here to be safe and fast.
    
    public TrackingResponse getTrackingInfo(String trackingNumber) {
        Order order = orderRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new RuntimeException("Order not found with tracking number: " + trackingNumber));
        
        List<TrackingEvent> events = trackingEventRepository.findByOrderId(order.getId(), Sort.by("eventTime").descending());
        
        return TrackingResponse.builder()
                .order(mapOrderToResponse(order))
                .events(events.stream().map(this::mapEventToResponse).collect(Collectors.toList()))
                .build();
    }

    @Transactional
    public TrackingEventResponse addTrackingEvent(UUID orderId, String status, String description, String locationName) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        // Auto-detect office if user is employee
        // Ideally we fetch current user's office.
        // For SHIPPER, they might not be 'at' an office, or we use their assigned office.
        // For PO_STAFF / WH_STAFF, we use their office.
        Account current = SecurityUtils.getCurrentAccount();
        // Employee employee = employeeRepository.findByAccount(current)...
        // Skipping strict office check for now, can be null
        
        TrackingEvent event = TrackingEvent.builder()
                .order(order)
                .status(status)
                .description(description)
                .locationName(locationName)
                .eventTime(LocalDateTime.now())
                .build();
        
        TrackingEvent saved = trackingEventRepository.save(event);
        return mapEventToResponse(saved);
    }
    
    // Mapper methods
    private TrackingEventResponse mapEventToResponse(TrackingEvent event) {
         return TrackingEventResponse.builder()
                 .id(event.getId())
                 .status(event.getStatus())
                 .description(event.getDescription())
                 .locationName(event.getLocationName())
                 .officeId(event.getOffice() != null ? event.getOffice().getId() : null)
                 .eventTime(event.getEventTime())
                 .build();
    }
    
    private OrderResponse mapOrderToResponse(Order order) {
        // Simplified mapping or duplicate from OrderService
        return OrderResponse.builder()
                .id(order.getId())
                .trackingNumber(order.getTrackingNumber())
                .status(order.getStatus().name())
                .senderName(order.getSenderName())
                .receiverName(order.getReceiverName())
                // ... map other fields as needed for public tracking ...
                // Usually tracking page shows: Dates, Status, Locations. Maybe not full address/phone for privacy?
                // But simplified MVP: show what we have.
                .senderWardCode(order.getSenderWardCode())
                .receiverWardCode(order.getReceiverWardCode())
                .build();
    }
}
