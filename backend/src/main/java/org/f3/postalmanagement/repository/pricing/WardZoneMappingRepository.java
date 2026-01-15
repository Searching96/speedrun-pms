package org.f3.postalmanagement.repository.pricing;

import org.f3.postalmanagement.entity.pricing.WardZoneMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WardZoneMappingRepository extends JpaRepository<WardZoneMapping, UUID> {
    
    @Query("SELECT wzm FROM WardZoneMapping wzm WHERE wzm.ward.code = :wardCode")
    Optional<WardZoneMapping> findByWardCode(@Param("wardCode") String wardCode);
}
