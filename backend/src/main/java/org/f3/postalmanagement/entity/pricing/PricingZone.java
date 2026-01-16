package org.f3.postalmanagement.entity.pricing;

import jakarta.persistence.*;
import lombok.*;
import org.f3.postalmanagement.entity.BaseEntity;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Table(name = "pricing_zones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PricingZone extends BaseEntity {

    @Column(name = "code", unique = true, nullable = false, length = 50)
    private String code; // e.g., "NOI_THANH_HCM", "NGOAI_THANH_HCM", "LIEN_MIEN"
    
    @Column(name = "name", nullable = false, length = 100)
    private String name; // e.g., "Nội thành TP.HCM"
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}
