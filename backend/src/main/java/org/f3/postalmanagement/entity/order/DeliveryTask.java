package org.f3.postalmanagement.entity;

import jakarta.persistence.*;
import lombok.*;
import org.f3.postalmanagement.entity.actor.Employee;
import org.f3.postalmanagement.enums.TaskStatus;
import org.f3.postalmanagement.enums.TaskType;

import java.time.LocalDateTime;

@Entity
@Table(name = "delivery_tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryTask extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipper_id", nullable = false)
    private Employee shipper;

    @Enumerated(EnumType.STRING)
    @Column(name = "task_type", nullable = false, length = 20)
    private TaskType taskType;

    // -- Location snapshot (as per plan)
    @Column(name = "address", nullable = false, columnDefinition = "TEXT")
    private String address;

    @Column(name = "ward_code", nullable = false, length = 10)
    private String wardCode;

    @Column(name = "contact_name", nullable = false, length = 100)
    private String contactName;

    @Column(name = "contact_phone", nullable = false, length = 15)
    private String contactPhone;

    // -- Status
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private TaskStatus status;

    // -- Timestamps
    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    // -- Completion details
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "photo_proof_url", length = 500)
    private String photoProofUrl;
}
