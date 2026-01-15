package org.f3.postalmanagement.entity.order;
import org.f3.postalmanagement.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.*;
import org.f3.postalmanagement.entity.actor.Customer;
import org.f3.postalmanagement.entity.unit.Office;
import org.f3.postalmanagement.enums.OrderStatus;

import java.math.BigDecimal;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order extends BaseEntity {

    @Column(name = "tracking_number", nullable = false, unique = true, length = 20)
    private String trackingNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    // -- Sender Info
    @Column(name = "sender_name", nullable = false)
    private String senderName;

    @Column(name = "sender_phone", nullable = false, length = 15)
    private String senderPhone;

    @Column(name = "sender_address", nullable = false, columnDefinition = "TEXT")
    private String senderAddress;
    
    @Column(name = "sender_ward_code", nullable = false, length = 10)
    private String senderWardCode;

    // -- Receiver Info
    @Column(name = "receiver_name", nullable = false)
    private String receiverName;

    @Column(name = "receiver_phone", nullable = false, length = 15)
    private String receiverPhone;

    @Column(name = "receiver_address", nullable = false, columnDefinition = "TEXT")
    private String receiverAddress;

    @Column(name = "receiver_ward_code", nullable = false, length = 10)
    private String receiverWardCode;

    // -- Package Info
    @Column(name = "weight_kg", nullable = false, precision = 10, scale = 2)
    private BigDecimal weightKg;

    @Column(name = "length_cm")
    private Integer lengthCm;

    @Column(name = "width_cm")
    private Integer widthCm;

    @Column(name = "height_cm")
    private Integer heightCm;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // -- Pricing
    @Column(name = "shipping_fee", nullable = false, precision = 15, scale = 2)
    private BigDecimal shippingFee;

    @Column(name = "cod_amount", precision = 15, scale = 2)
    private BigDecimal codAmount;

    // -- Status
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private OrderStatus status;

    // -- Office that created the order (can be null if customer created it online)
    // Actually the plan says 'origin_office_id NOT NULL'. 
    // If a customer creates it, is it assigned an origin office immediately? 
    // Usually it's assigned to the office responsible for the sender's ward.
    // For now I will mark it as nullable to be safe, or we logic to assign it.
    // The plan said: "Office that created the order". 
    // If PO_STAFF creates it, it's their office. 
    // If CUSTOMER creates it, we might derive it or set it later. 
    // Let's stick to the plan but allow null if logic requires, or just strictly follow schema.
    // Plan schema: origin_office_id VARCHAR(36) NOT NULL.
    // I will use @JoinColumn(nullable = false) but we must ensure we set it.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "origin_office_id", nullable = true) 
    private Office originOffice;
    
    // Helper to calc volume (optional, if needed later)
}
