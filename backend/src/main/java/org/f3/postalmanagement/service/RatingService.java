package org.f3.postalmanagement.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.f3.postalmanagement.dto.request.CreateRatingRequest;
import org.f3.postalmanagement.dto.response.RatingResponse;
import org.f3.postalmanagement.entity.DeliveryTask;
import org.f3.postalmanagement.entity.Order;
import org.f3.postalmanagement.entity.ServiceRating;
import org.f3.postalmanagement.entity.actor.Account;
import org.f3.postalmanagement.entity.actor.Customer;
import org.f3.postalmanagement.entity.actor.Employee;
import org.f3.postalmanagement.enums.OrderStatus;
import org.f3.postalmanagement.enums.Role;
import org.f3.postalmanagement.enums.TaskStatus;
import org.f3.postalmanagement.enums.TaskType;
import org.f3.postalmanagement.exception.ResourceNotFoundException;
import org.f3.postalmanagement.repository.CustomerRepository;
import org.f3.postalmanagement.repository.DeliveryTaskRepository;
import org.f3.postalmanagement.repository.OrderRepository;
import org.f3.postalmanagement.repository.ServiceRatingRepository;
import org.f3.postalmanagement.utils.SecurityUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final ServiceRatingRepository serviceRatingRepository;
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final DeliveryTaskRepository deliveryTaskRepository;

    private static final Set<Role> ADMIN_ROLES = Set.of(
            Role.SYSTEM_ADMIN,
            Role.PO_PROVINCE_ADMIN,
            Role.PO_WARD_MANAGER,
            Role.WH_PROVINCE_ADMIN,
            Role.WH_WARD_MANAGER
    );

    @Transactional
    public RatingResponse createRating(CreateRatingRequest request) {
        Account currentAccount = SecurityUtils.getCurrentAccount();
        if (currentAccount.getRole() != Role.CUSTOMER) {
            throw new IllegalArgumentException("Only customers can rate services");
        }

        Customer customer = customerRepository.findByAccount(currentAccount)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!order.getCustomer().getId().equals(customer.getId())) {
            throw new IllegalArgumentException("Order does not belong to this customer");
        }

        // Ensure order is delivered
        if (order.getStatus() != OrderStatus.DELIVERED) {
            throw new IllegalArgumentException("Cannot rate an order that is not DELIVERED");
        }

        // Check if already rated
        if (serviceRatingRepository.findByOrderId(order.getId()).isPresent()) {
            throw new IllegalArgumentException("Order has already been rated");
        }

        // Find the shipper who completed the delivery
        Employee shipper = null;
        Optional<DeliveryTask> deliveryTask = deliveryTaskRepository.findByOrderIdAndTaskTypeAndStatus(
                order.getId(), TaskType.DELIVERY, TaskStatus.COMPLETED
        );
        if (deliveryTask.isPresent()) {
            shipper = deliveryTask.get().getShipper();
        }

        ServiceRating rating = ServiceRating.builder()
                .order(order)
                .customer(customer)
                .shipper(shipper)
                .overallRating(request.getOverallRating())
                .deliverySpeedRating(request.getDeliverySpeedRating())
                .shipperAttitudeRating(request.getShipperAttitudeRating())
                .comment(request.getComment())
                .build();

        ServiceRating saved = serviceRatingRepository.save(rating);
        return mapToResponse(saved);
    }

    public RatingResponse getRatingByOrderId(UUID orderId) {
        ServiceRating rating = serviceRatingRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Rating not found for this order"));

        // Security check: only show if owner OR admin
        Account currentAccount = SecurityUtils.getCurrentAccount();
        boolean isAdmin = ADMIN_ROLES.contains(currentAccount.getRole());

        if (!isAdmin) {
            // Must be the order owner
            if (currentAccount.getRole() == Role.CUSTOMER) {
                Customer customer = customerRepository.findByAccount(currentAccount)
                        .orElseThrow(() -> new AccessDeniedException("Customer not found"));
                if (!rating.getOrder().getCustomer().getId().equals(customer.getId())) {
                    throw new AccessDeniedException("You do not have permission to view this rating");
                }
            } else {
                throw new AccessDeniedException("You do not have permission to view this rating");
            }
        }

        return mapToResponse(rating);
    }

    private RatingResponse mapToResponse(ServiceRating rating) {
        return RatingResponse.builder()
                .id(rating.getId())
                .orderId(rating.getOrder().getId())
                .overallRating(rating.getOverallRating())
                .deliverySpeedRating(rating.getDeliverySpeedRating())
                .shipperAttitudeRating(rating.getShipperAttitudeRating())
                .comment(rating.getComment())
                .createdAt(rating.getCreatedAt())
                .build();
    }
}
