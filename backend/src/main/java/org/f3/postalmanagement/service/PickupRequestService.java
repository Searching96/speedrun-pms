package org.f3.postalmanagement.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.f3.postalmanagement.dto.request.CreatePickupRequest;
import org.f3.postalmanagement.dto.response.PickupRequestResponse;
import org.f3.postalmanagement.entity.Order;
import org.f3.postalmanagement.entity.PickupRequest;
import org.f3.postalmanagement.entity.actor.Account;
import org.f3.postalmanagement.entity.actor.Customer;
import org.f3.postalmanagement.entity.actor.Employee;
import org.f3.postalmanagement.enums.PickupStatus;
import org.f3.postalmanagement.enums.Role;
import org.f3.postalmanagement.repository.CustomerRepository;
import org.f3.postalmanagement.repository.EmployeeRepository;
import org.f3.postalmanagement.repository.OrderRepository;
import org.f3.postalmanagement.repository.PickupRequestRepository;
import org.f3.postalmanagement.utils.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PickupRequestService {

    private final PickupRequestRepository pickupRequestRepository;
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final EmployeeRepository employeeRepository;
    // Circular dependency risk? Assignment calls DeliveryTaskService.
    // DeliveryTaskService creates task.
    // If DeliveryTaskService injects PickupRequestRepository, it is fine.
    // If it injects THIS service, it's circular.
    // DeliveryTaskService doesn't seem to need PickupRequestService. Good.
    private final DeliveryTaskService deliveryTaskService;

    @Transactional
    public PickupRequestResponse createPickupRequest(CreatePickupRequest request) {
        Account currentAccount = SecurityUtils.getCurrentAccount();
        if (currentAccount.getRole() != Role.CUSTOMER) {
            throw new IllegalArgumentException("Only customers can create pickup requests");
        }

        Customer customer = customerRepository.findByAccount(currentAccount)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (!order.getCustomer().getId().equals(customer.getId())) {
            throw new IllegalArgumentException("Order does not belong to this customer");
        }

        PickupRequest pickupRequest = PickupRequest.builder()
                .order(order)
                .customer(customer)
                .pickupAddress(request.getPickupAddress())
                .pickupWardCode(request.getPickupWardCode())
                .pickupContactName(request.getPickupContactName())
                .pickupContactPhone(request.getPickupContactPhone())
                .preferredDate(request.getPreferredDate())
                .preferredTimeSlot(request.getPreferredTimeSlot())
                .status(PickupStatus.PENDING)
                .build();

        PickupRequest saved = pickupRequestRepository.save(pickupRequest);
        return mapToResponse(saved);
    }

    @Transactional
    public Page<PickupRequestResponse> getMyRequests(Pageable pageable) {
        Account currentAccount = SecurityUtils.getCurrentAccount();
        Customer customer = customerRepository.findByAccount(currentAccount)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        
        return pickupRequestRepository.findByCustomerId(customer.getId(), pageable)
                .map(this::mapToResponse);
    }

    @Transactional
    public List<PickupRequestResponse> getPendingRequestsByWard(String wardCode) {
        return pickupRequestRepository.findByPickupWardCodeAndStatus(wardCode, PickupStatus.PENDING)
                .stream().map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public PickupRequestResponse assignShipper(UUID requestId, UUID shipperId) {
        PickupRequest request = pickupRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));
                
        Employee shipper = employeeRepository.findById(shipperId)
                .orElseThrow(() -> new IllegalArgumentException("Shipper not found"));

        // Update Request
        request.setAssignedShipper(shipper);
        request.setAssignedAt(LocalDateTime.now());
        request.setStatus(PickupStatus.ASSIGNED);
        PickupRequest saved = pickupRequestRepository.save(request);

        // Create Task for Shipper
        deliveryTaskService.createPickupTask(saved);

        return mapToResponse(saved);
    }

    private PickupRequestResponse mapToResponse(PickupRequest req) {
        return PickupRequestResponse.builder()
                .id(req.getId())
                .orderId(req.getOrder().getId())
                .orderTrackingNumber(req.getOrder().getTrackingNumber())
                .pickupAddress(req.getPickupAddress())
                .pickupWardCode(req.getPickupWardCode())
                .pickupContactName(req.getPickupContactName())
                .pickupContactPhone(req.getPickupContactPhone())
                .preferredDate(req.getPreferredDate())
                .preferredTimeSlot(req.getPreferredTimeSlot())
                .status(req.getStatus().name())
                .assignedShipperId(req.getAssignedShipper() != null ? req.getAssignedShipper().getId() : null)
                .assignedShipperName(req.getAssignedShipper() != null ? req.getAssignedShipper().getFullName() : null)
                .createdAt(req.getCreatedAt())
                .assignedAt(req.getAssignedAt())
                .completedAt(req.getCompletedAt())
                .build();
    }
}
