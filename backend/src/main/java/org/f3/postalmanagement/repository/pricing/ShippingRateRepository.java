package org.f3.postalmanagement.repository.pricing;

import org.f3.postalmanagement.entity.pricing.PricingZone;
import org.f3.postalmanagement.entity.pricing.ShippingRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShippingRateRepository extends JpaRepository<ShippingRate, UUID> {
    
    @Query("SELECT sr FROM ShippingRate sr " +
           "WHERE sr.fromZone = :fromZone " +
           "AND sr.toZone = :toZone " +
           "AND sr.isActive = true " +
           "AND (sr.validFrom IS NULL OR sr.validFrom <= :now) " +
           "AND (sr.validTo IS NULL OR sr.validTo >= :now)")
    Optional<ShippingRate> findActiveRate(
        @Param("fromZone") PricingZone fromZone,
        @Param("toZone") PricingZone toZone,
        @Param("now") LocalDateTime now
    );
}
