package org.f3.postalmanagement.repository;

import org.f3.postalmanagement.entity.PickupRequest;
import org.f3.postalmanagement.enums.PickupStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PickupRequestRepository extends JpaRepository<PickupRequest, UUID> {

    // For Customer
    Page<PickupRequest> findByCustomerId(UUID customerId, Pageable pageable);

    // For Ward Manager (find unassigned requests in their ward)
    // Assuming ward manager manages a specific ward code. 
    // We filter by pickupWardCode and Status=PENDING
    List<PickupRequest> findByPickupWardCodeAndStatus(String wardCode, PickupStatus status);

    // For Shipper
    List<PickupRequest> findByAssignedShipperIdAndStatus(UUID shipperId, PickupStatus status);
}
