package org.f3.postalmanagement.repository;

import org.f3.postalmanagement.entity.order.Order;
import org.f3.postalmanagement.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    
    Optional<Order> findByTrackingNumber(String trackingNumber);
    
    // Find orders by customer (for My Orders)
    Page<Order> findByCustomerId(UUID customerId, Pageable pageable);
    
    Page<Order> findByCustomerIdAndStatus(UUID customerId, OrderStatus status, Pageable pageable);
    
    // Find orders by origin office (for PO Staff)
    Page<Order> findByOriginOfficeId(UUID officeId, Pageable pageable);
    
    Page<Order> findByOriginOfficeIdAndStatus(UUID officeId, OrderStatus status, Pageable pageable);
}
