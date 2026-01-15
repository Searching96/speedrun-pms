package org.f3.postalmanagement.repository;

import org.f3.postalmanagement.entity.order.TrackingEvent;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TrackingEventRepository extends JpaRepository<TrackingEvent, UUID> {
    List<TrackingEvent> findByOrderId(UUID orderId, Sort sort);
}
