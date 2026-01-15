package org.f3.postalmanagement.repository.pricing;

import org.f3.postalmanagement.entity.pricing.PricingZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PricingZoneRepository extends JpaRepository<PricingZone, UUID> {
    Optional<PricingZone> findByCode(String code);
}
