package org.f3.postalmanagement.entity.pricing;

import jakarta.persistence.*;
import lombok.*;
import org.f3.postalmanagement.entity.BaseEntity;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "shipping_rates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShippingRate extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_zone_id", nullable = false)
    private PricingZone fromZone;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_zone_id", nullable = false)
    private PricingZone toZone;
    
    @Column(name = "base_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal basePrice; // Minimum charge (VND)
    
    @Column(name = "price_per_kg", nullable = false, precision = 15, scale = 2)
    private BigDecimal pricePerKg; // Price per kg (VND)
    
    @Column(name = "valid_from")
    private LocalDateTime validFrom;
    
    @Column(name = "valid_to")
    private LocalDateTime validTo; // NULL = no expiration
    
    @Column(name = "is_active")
    private Boolean isActive = true;
}
