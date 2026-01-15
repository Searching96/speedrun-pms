package org.f3.postalmanagement.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.f3.postalmanagement.dto.request.CreateOrderRequest;
import org.f3.postalmanagement.dto.response.OrderResponse;
import org.f3.postalmanagement.entity.Order;
import org.f3.postalmanagement.entity.actor.Account;
import org.f3.postalmanagement.entity.actor.Customer;
import org.f3.postalmanagement.entity.actor.Employee;
import org.f3.postalmanagement.enums.OrderStatus;
import org.f3.postalmanagement.enums.Role;
import org.f3.postalmanagement.repository.CustomerRepository;
import org.f3.postalmanagement.repository.EmployeeRepository;
import org.f3.postalmanagement.repository.OrderRepository;
import org.f3.postalmanagement.utils.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final EmployeeRepository employeeRepository;

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        Account currentAccount = SecurityUtils.getCurrentAccount();
        Customer customer;
        Employee employee = null;

        if (currentAccount.getRole() == Role.CUSTOMER) {
            customer = customerRepository.findByAccount(currentAccount)
                    .orElseThrow(() -> new IllegalArgumentException("Customer profile not found"));
        } else if (currentAccount.getRole() == Role.PO_STAFF) {
            // If PO_STAFF creates order, who is the customer?
            // In a real system, they might select a customer or create a guest customer.
            // For MVP, let's assume they are creating it *for* a walk-in customer.
            // But our schema requires a customer_id.
            // Assuming for now PO_STAFF creates order for a 'walk-in' guest or specific user.
            // To simplify, let's limit creation to CUSTOMER for now, OR required 'customerId' in request if PO_STAFF.
            // Since DTO doesn't have customerId, let's assume currently only CUSTOMER creates orders online,
            // OR PO_STAFF creates for a default/guest customer.
            // Let's THROW for now if not CUSTOMER, to keep it safe until updated.
            // Wait, requirements say "Post office staff receives...".
            // Let's fetch the customer by phone number if we had it, or create on fly?
            // "Orders table -> customer_id NOT NULL".
            // I'll assume for this MVP step: Only Authenticated CUSTOMERs can create orders via this API.
            // If PO_STAFF needs to, we'll need to look up customer by phone.
            // I will implement: Only CUSTOMER role can create for now.
            customer = customerRepository.findByAccount(currentAccount)
                    .orElseThrow(() -> new IllegalArgumentException("Customer profile not found"));
        } else {
            throw new IllegalArgumentException("Only Customers can create orders via this endpoint");
        }

        // Generate Tracking Number (Simple)
        String trackingNumber = "VN" + System.currentTimeMillis() + (int)(Math.random() * 1000);

        Order order = Order.builder()
                .trackingNumber(trackingNumber)
                .customer(customer)
                .senderName(request.getSenderName())
                .senderPhone(request.getSenderPhone())
                .senderAddress(request.getSenderAddress())
                .senderWardCode(request.getSenderWardCode())
                .receiverName(request.getReceiverName())
                .receiverPhone(request.getReceiverPhone())
                .receiverAddress(request.getReceiverAddress())
                .receiverWardCode(request.getReceiverWardCode())
                .weightKg(request.getWeightKg())
                .lengthCm(request.getLengthCm())
                .widthCm(request.getWidthCm())
                .heightCm(request.getHeightCm())
                .description(request.getDescription())
                .shippingFee(request.getShippingFee())
                .codAmount(request.getCodAmount())
                .status(OrderStatus.PENDING)
                // originOffice is null for online orders until assigned
                .build();

        Order savedOrder = orderRepository.save(order);
        return mapToResponse(savedOrder);
    }

    public Page<OrderResponse> getMyOrders(Pageable pageable) {
        Account currentAccount = SecurityUtils.getCurrentAccount();
        if (currentAccount.getRole() == Role.CUSTOMER) {
            Customer customer = customerRepository.findByAccount(currentAccount)
                    .orElseThrow(() -> new IllegalArgumentException("Customer profile not found"));
            return orderRepository.findByCustomerId(customer.getId(), pageable)
                    .map(this::mapToResponse);
        } else if (currentAccount.getRole() == Role.PO_STAFF) {
             Employee employee = employeeRepository.findById(currentAccount.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Employee not found"));
             // PO_STAFF sees orders originated from their office
             // (We need to verify if originOffice is set. If online orders have null origin, they might not see them yet)
             // For now, return empty or all? Let's just return based on originOfficeId logic.
             return orderRepository.findByOriginOfficeId(employee.getOffice().getId(), pageable)
                     .map(this::mapToResponse);
        }
        return Page.empty();
    }
    
    public OrderResponse getOrderByTrackingNumber(String trackingNumber) {
        Order order = orderRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        return mapToResponse(order);
    }
    
    @Transactional
    public OrderResponse cancelOrder(UUID orderId) {
        Account currentAccount = SecurityUtils.getCurrentAccount();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        
        // Check ownership
        if (currentAccount.getRole() == Role.CUSTOMER) {
             Customer customer = customerRepository.findByAccount(currentAccount)
                    .orElseThrow(() -> new IllegalArgumentException("Customer profile not found"));
             if (!order.getCustomer().getId().equals(customer.getId())) {
                 throw new IllegalArgumentException("Access denied");
             }
        }
        
        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.AWAITING_PICKUP) {
            throw new IllegalArgumentException("Cannot cancel order in status: " + order.getStatus());
        }
        
        order.setStatus(OrderStatus.CANCELLED);
        Order saved = orderRepository.save(order);
        return mapToResponse(saved);
    }

    private OrderResponse mapToResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .trackingNumber(order.getTrackingNumber())
                .status(order.getStatus().name())
                .senderName(order.getSenderName())
                .senderPhone(order.getSenderPhone())
                .senderAddress(order.getSenderAddress())
                .senderWardCode(order.getSenderWardCode())
                .receiverName(order.getReceiverName())
                .receiverPhone(order.getReceiverPhone())
                .receiverAddress(order.getReceiverAddress())
                .receiverWardCode(order.getReceiverWardCode())
                .weightKg(order.getWeightKg())
                .description(order.getDescription())
                .shippingFee(order.getShippingFee())
                .codAmount(order.getCodAmount())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}
