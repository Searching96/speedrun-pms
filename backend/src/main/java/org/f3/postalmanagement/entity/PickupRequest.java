package org.f3.postalmanagement.entity;

import jakarta.persistence.*;
import lombok.*;
import org.f3.postalmanagement.entity.actor.Customer;
import org.f3.postalmanagement.entity.actor.Employee;
import org.f3.postalmanagement.enums.PickupStatus;
import org.f3.postalmanagement.enums.TimeSlot;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "pickup_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PickupRequest extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    // -- Pickup Location
    @Column(name = "pickup_address", nullable = false, columnDefinition = "TEXT")
    private String pickupAddress;

    @Column(name = "pickup_ward_code", nullable = false, length = 10)
    private String pickupWardCode;

    @Column(name = "pickup_contact_name", nullable = false, length = 100)
    private String pickupContactName;

    @Column(name = "pickup_contact_phone", nullable = false, length = 15)
    private String pickupContactPhone;

    // -- Preference
    @Column(name = "preferred_date", nullable = false)
    private LocalDate preferredDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_time_slot")
    private TimeSlot preferredTimeSlot;

    // -- Assignment
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_shipper_id")
    private Employee assignedShipper;

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

    // -- Status
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private PickupStatus status;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;
}
