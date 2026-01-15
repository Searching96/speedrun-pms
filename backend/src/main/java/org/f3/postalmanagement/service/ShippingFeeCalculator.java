package org.f3.postalmanagement.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.f3.postalmanagement.entity.pricing.PricingZone;
import org.f3.postalmanagement.entity.pricing.ShippingRate;
import org.f3.postalmanagement.entity.pricing.WardZoneMapping;
import org.f3.postalmanagement.repository.pricing.ShippingRateRepository;
import org.f3.postalmanagement.repository.pricing.WardZoneMappingRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShippingFeeCalculator {
    
    private final WardZoneMappingRepository wardZoneMappingRepository;
    private final ShippingRateRepository shippingRateRepository;
    
    /**
     * Calculate shipping fee based on weight, dimensions, and zones
     * 
     * @param senderWardCode Sender's ward code
     * @param receiverWardCode Receiver's ward code
     * @param weightKg Actual weight in kg
     * @param lengthCm Length in cm (optional)
     * @param widthCm Width in cm (optional)
     * @param heightCm Height in cm (optional)
     * @return Calculated shipping fee in VND
     */
    public BigDecimal calculateFee(
        String senderWardCode,
        String receiverWardCode,
        BigDecimal weightKg,
        Integer lengthCm,
        Integer widthCm,
        Integer heightCm
    ) {
        log.info("Calculating shipping fee: from={}, to={}, weight={}kg, dimensions={}x{}x{}", 
            senderWardCode, receiverWardCode, weightKg, lengthCm, widthCm, heightCm);
        
        // 1. Calculate dimensional weight
        BigDecimal dimensionalWeight = calculateDimensionalWeight(lengthCm, widthCm, heightCm);
        log.debug("Dimensional weight: {}kg", dimensionalWeight);
        
        // 2. Get chargeable weight (max of actual and dimensional)
        BigDecimal chargeableWeight = weightKg.max(dimensionalWeight);
        log.debug("Chargeable weight: {}kg (max of actual {} and dimensional {})", 
            chargeableWeight, weightKg, dimensionalWeight);
        
        // 3. Get zones for sender and receiver wards
        PricingZone fromZone = getZoneForWard(senderWardCode);
        PricingZone toZone = getZoneForWard(receiverWardCode);
        log.debug("Zones: from={}, to={}", fromZone.getCode(), toZone.getCode());
        
        // 4. Get rate from pricing matrix
        ShippingRate rate = getActiveRate(fromZone, toZone);
        log.debug("Rate: base={}, perKg={}", rate.getBasePrice(), rate.getPricePerKg());
        
        // 5. Calculate fee: basePrice + (chargeableWeight * pricePerKg)
        BigDecimal fee = rate.getBasePrice()
            .add(chargeableWeight.multiply(rate.getPricePerKg()))
            .setScale(0, RoundingMode.HALF_UP); // Round to nearest VND
        
        log.info("Calculated shipping fee: {} VND", fee);
        return fee;
    }
    
    /**
     * Calculate dimensional weight using formula: (L × W × H) / 5000
     * Result is in kg
     */
    private BigDecimal calculateDimensionalWeight(Integer lengthCm, Integer widthCm, Integer heightCm) {
        if (lengthCm == null || widthCm == null || heightCm == null) {
            return BigDecimal.ZERO;
        }
        
        if (lengthCm <= 0 || widthCm <= 0 || heightCm <= 0) {
            return BigDecimal.ZERO;
        }
        
        // (L × W × H) / 5000 = kg
        BigDecimal volume = BigDecimal.valueOf(lengthCm)
            .multiply(BigDecimal.valueOf(widthCm))
            .multiply(BigDecimal.valueOf(heightCm));
        
        return volume.divide(BigDecimal.valueOf(5000), 2, RoundingMode.HALF_UP);
    }
    
    /**
     * Get pricing zone for a ward
     */
    private PricingZone getZoneForWard(String wardCode) {
        WardZoneMapping mapping = wardZoneMappingRepository.findByWardCode(wardCode)
            .orElseThrow(() -> new IllegalArgumentException(
                "No pricing zone found for ward: " + wardCode + ". Please configure ward-zone mapping."));
        
        return mapping.getZone();
    }
    
    /**
     * Get active shipping rate for zone pair
     */
    private ShippingRate getActiveRate(PricingZone fromZone, PricingZone toZone) {
        return shippingRateRepository.findActiveRate(fromZone, toZone, LocalDateTime.now())
            .orElseThrow(() -> new IllegalArgumentException(
                String.format("No active shipping rate found for route: %s -> %s", 
                    fromZone.getCode(), toZone.getCode())));
    }
}
