package org.f3.postalmanagement.repository;

import org.f3.postalmanagement.entity.order.DeliveryTask;
import org.f3.postalmanagement.enums.TaskStatus;
import org.f3.postalmanagement.enums.TaskType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeliveryTaskRepository extends JpaRepository<DeliveryTask, UUID> {

    List<DeliveryTask> findByShipperId(UUID shipperId);

    List<DeliveryTask> findByShipperIdAndStatus(UUID shipperId, TaskStatus status);

    Optional<DeliveryTask> findByOrderIdAndTaskTypeAndStatus(UUID orderId, TaskType taskType, TaskStatus status);
}
