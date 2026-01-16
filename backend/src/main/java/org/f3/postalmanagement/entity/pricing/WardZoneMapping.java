package org.f3.postalmanagement.entity.pricing;

import jakarta.persistence.*;
import lombok.*;
import org.f3.postalmanagement.entity.BaseEntity;
import org.f3.postalmanagement.entity.administrative.Ward;

@Entity
@Table(name = "ward_zone_mappings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WardZoneMapping extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ward_code", referencedColumnName = "code", nullable = false)
    private Ward ward;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id", nullable = false)
    private PricingZone zone;
}
