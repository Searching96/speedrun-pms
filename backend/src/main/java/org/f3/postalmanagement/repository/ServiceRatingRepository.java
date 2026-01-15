package org.f3.postalmanagement.repository;

import org.f3.postalmanagement.entity.ServiceRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ServiceRatingRepository extends JpaRepository<ServiceRating, UUID> {
    Optional<ServiceRating> findByOrderId(UUID orderId);
}
