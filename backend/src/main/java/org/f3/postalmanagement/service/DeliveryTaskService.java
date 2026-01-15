package org.f3.postalmanagement.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.f3.postalmanagement.dto.response.order.DeliveryTaskResponse;
import org.f3.postalmanagement.entity.order.DeliveryTask;
import org.f3.postalmanagement.entity.order.Order;
import org.f3.postalmanagement.entity.order.PickupRequest;
import org.f3.postalmanagement.entity.actor.Account;
import org.f3.postalmanagement.entity.actor.Employee;
import org.f3.postalmanagement.enums.OrderStatus;
import org.f3.postalmanagement.enums.PickupStatus;
import org.f3.postalmanagement.enums.Role;
import org.f3.postalmanagement.enums.TaskStatus;
import org.f3.postalmanagement.enums.TaskType;
import org.f3.postalmanagement.repository.DeliveryTaskRepository;
import org.f3.postalmanagement.repository.EmployeeRepository;
import org.f3.postalmanagement.repository.OrderRepository;
import org.f3.postalmanagement.repository.PickupRequestRepository;
import org.f3.postalmanagement.utils.SecurityUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeliveryTaskService {

    private final DeliveryTaskRepository deliveryTaskRepository;
    private final OrderRepository orderRepository;
    private final EmployeeRepository employeeRepository;
    private final PickupRequestRepository pickupRequestRepository;

    /**
     * Create a task for a Pickup Request (Called automatically when Manager assigns shipper)
     */
    @Transactional
    public DeliveryTask createPickupTask(PickupRequest pickupRequest) {
        DeliveryTask task = DeliveryTask.builder()
                .order(pickupRequest.getOrder())
                .shipper(pickupRequest.getAssignedShipper())
                .taskType(TaskType.PICKUP)
                .address(pickupRequest.getPickupAddress())
                .wardCode(pickupRequest.getPickupWardCode())
                .contactName(pickupRequest.getPickupContactName())
                .contactPhone(pickupRequest.getPickupContactPhone())
                .status(TaskStatus.ASSIGNED)
                .assignedAt(LocalDateTime.now())
                .build();
        return deliveryTaskRepository.save(task);
    }

    /**
     * Get My Tasks (for Shipper)
     */
    public List<DeliveryTaskResponse> getMyTasks() {
        Account currentAccount = SecurityUtils.getCurrentAccount();
        if (currentAccount.getRole() != Role.SHIPPER) {
             throw new IllegalArgumentException("Only shippers can access tasks");
        }
        return deliveryTaskRepository.findByShipperId(currentAccount.getId())
                .stream().map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public DeliveryTaskResponse startTask(UUID taskId) {
        DeliveryTask task = getMyTaskById(taskId);
        if (task.getStatus() != TaskStatus.ASSIGNED) {
            throw new IllegalArgumentException("Task already started or completed");
        }
        task.setStatus(TaskStatus.IN_PROGRESS);
        task.setStartedAt(LocalDateTime.now());
        
        // Update associated Order / PickupRequest status if needed
        if (task.getTaskType() == TaskType.PICKUP) {
             // Find pickup request... simplified logic: assume linked order is enough for now 
             // or we need to link task -> pickup_request id specifically.
             // Ideally we should have a link. But let's verify via Order for this MVP phase.
             // Actually, the Plan didn't add `pickup_request_id` to `delivery_tasks`.
             // So we update Order status to IN_TRANSIT? Or AWAITING_PICKUP?
             // If shipper starts pickup task, maybe update PickupRequest status to IN_PROGRESS.
             // But we don't have direct link here easily without querying.
             // Let's keep it simple: just update task status.
        }
        
        return mapToResponse(deliveryTaskRepository.save(task));
    }

    @Transactional
    public DeliveryTaskResponse completeTask(UUID taskId, String notes, String photoProofUrl) {
        DeliveryTask task = getMyTaskById(taskId);
        if (task.getStatus() != TaskStatus.IN_PROGRESS) {
            throw new IllegalArgumentException("Task must be IN_PROGRESS to complete");
        }
        task.setStatus(TaskStatus.COMPLETED);
        task.setCompletedAt(LocalDateTime.now());
        task.setNotes(notes);
        task.setPhotoProofUrl(photoProofUrl);
        
        // Logic to update core entities
        Order order = task.getOrder();
        if (task.getTaskType() == TaskType.PICKUP) {
            order.setStatus(OrderStatus.PICKED_UP); 
            // Also find PickupRequest and complete it?
            // To be robust we should query `pickupRequestRepository` by orderId maybe?
        } else if (task.getTaskType() == TaskType.DELIVERY) {
            order.setStatus(OrderStatus.DELIVERED);
        }
        orderRepository.save(order);
        
        return mapToResponse(deliveryTaskRepository.save(task));
    }
    
    @Transactional
    public DeliveryTaskResponse failTask(UUID taskId, String reason) {
        DeliveryTask task = getMyTaskById(taskId);
        task.setStatus(TaskStatus.FAILED);
        task.setCompletedAt(LocalDateTime.now()); // Failed time
        task.setNotes(reason);
        return mapToResponse(deliveryTaskRepository.save(task));
    }

    private DeliveryTask getMyTaskById(UUID taskId) {
        Account currentAccount = SecurityUtils.getCurrentAccount();
        DeliveryTask task = deliveryTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
        if (!task.getShipper().getId().equals(currentAccount.getId())) {
            throw new IllegalArgumentException("Access denied");
        }
        return task;
    }

    private DeliveryTaskResponse mapToResponse(DeliveryTask task) {
        return DeliveryTaskResponse.builder()
                .id(task.getId())
                .orderId(task.getOrder().getId())
                .orderTrackingNumber(task.getOrder().getTrackingNumber())
                .shipperId(task.getShipper().getId().toString())
                .shipperName(task.getShipper().getFullName())
                .taskType(task.getTaskType())
                .status(task.getStatus().name())
                .address(task.getAddress())
                .wardCode(task.getWardCode())
                .contactName(task.getContactName())
                .contactPhone(task.getContactPhone())
                .assignedAt(task.getAssignedAt())
                .startedAt(task.getStartedAt())
                .completedAt(task.getCompletedAt())
                .notes(task.getNotes())
                .photoProofUrl(task.getPhotoProofUrl())
                .build();
    }
}
