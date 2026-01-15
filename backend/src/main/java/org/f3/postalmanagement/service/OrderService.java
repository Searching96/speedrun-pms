package org.f3.postalmanagement.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.f3.postalmanagement.dto.request.order.CreateOrderRequest;
import org.f3.postalmanagement.dto.response.order.OrderResponse;
import org.f3.postalmanagement.dto.response.order.PublicOrderResponse;
import org.f3.postalmanagement.entity.ApiResponse;
import org.f3.postalmanagement.entity.order.Order;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final EmployeeRepository employeeRepository;
    private final TrackingNumberGenerator trackingNumberGenerator;
    private final ShippingFeeCalculator shippingFeeCalculator;

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        Account currentAccount = SecurityUtils.getCurrentAccount();
        Customer customer = getCurrentCustomer(currentAccount);

        // Generate unique tracking number
        String trackingNumber = trackingNumberGenerator.generate();
        
        log.info("Creating order for customer: {}, tracking: {}", customer.getId(), trackingNumber);

        // Auto-calculate shipping fee if not provided
        java.math.BigDecimal shippingFee = request.getShippingFee();
        if (shippingFee == null || shippingFee.compareTo(java.math.BigDecimal.ZERO) == 0) {
            log.info("Calculating shipping fee automatically");
            shippingFee = shippingFeeCalculator.calculateFee(
                request.getSenderWardCode(),
                request.getReceiverWardCode(),
                request.getWeightKg(),
                request.getLengthCm(),
                request.getWidthCm(),
                request.getHeightCm()
            );
            log.info("Calculated shipping fee: {}", shippingFee);
        }

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
                .shippingFee(shippingFee)
                .codAmount(request.getCodAmount())
                .status(OrderStatus.PENDING)
                // originOffice is null for online orders until assigned
                .build();

        Order savedOrder = orderRepository.save(order);
        log.info("Order created successfully: {}", trackingNumber);
        return mapToResponse(savedOrder);
    }
    
    /**
     * Gets the current customer from the authenticated account.
     * Only CUSTOMER role is allowed to create orders via this endpoint.
     */
    private Customer getCurrentCustomer(Account account) {
        if (account.getRole() != Role.CUSTOMER) {
            log.warn("Non-customer role attempted to create order: {}", account.getRole());
            throw new IllegalArgumentException("Only customers can create orders via this endpoint");
        }
        
        return customerRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("Customer profile not found"));
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
    
    public PublicOrderResponse getPublicOrderByTrackingNumber(String trackingNumber) {
        Order order = orderRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        return PublicOrderResponse.builder()
                .id(order.getId())
                .trackingNumber(order.getTrackingNumber())
                .status(order.getStatus().name())
                .senderName(order.getSenderName())
                .receiverName(order.getReceiverName())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
    
    @Transactional
    public OrderResponse cancelOrder(UUID orderId) {
        Account currentAccount = SecurityUtils.getCurrentAccount();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        
        log.info("Cancellation requested for order: {} by user: {}", orderId, currentAccount.getId());
        
        // Check ownership
        if (currentAccount.getRole() == Role.CUSTOMER) {
             Customer customer = customerRepository.findByAccount(currentAccount)
                    .orElseThrow(() -> new IllegalArgumentException("Customer profile not found"));
             if (!order.getCustomer().getId().equals(customer.getId())) {
                 log.warn("Access denied: User {} attempted to cancel order {} owned by {}", 
                     currentAccount.getId(), orderId, order.getCustomer().getId());
                 throw new IllegalArgumentException("Access denied: You can only cancel your own orders");
             }
        }
        
        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.AWAITING_PICKUP) {
            log.warn("Cannot cancel order {} in status: {}", orderId, order.getStatus());
            throw new IllegalArgumentException("Cannot cancel order in status: " + order.getStatus());
        }
        
        order.setStatus(OrderStatus.CANCELLED);
        Order saved = orderRepository.save(order);
        log.info("Order {} cancelled successfully", orderId);
        return mapToResponse(saved);
    }

    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(this::mapToResponse);
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
