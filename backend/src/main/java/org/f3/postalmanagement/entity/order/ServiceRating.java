package org.f3.postalmanagement.entity.order;
import org.f3.postalmanagement.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.*;
import org.f3.postalmanagement.entity.actor.Customer;
import org.f3.postalmanagement.entity.actor.Employee;

import java.time.LocalDateTime;

@Entity
@Table(name = "service_ratings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceRating extends BaseEntity {

    @OneToOne(optional = false)
    @JoinColumn(name = "order_id", unique = true, nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipper_id")
    private Employee shipper; // The shipper who delivered/completed the order

    // -- Ratings (1-5)
    @Column(name = "overall_rating", nullable = false)
    private Integer overallRating;

    @Column(name = "delivery_speed_rating")
    private Integer deliverySpeedRating;

    @Column(name = "shipper_attitude_rating")
    private Integer shipperAttitudeRating;

    // -- Feedback
    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;
}
