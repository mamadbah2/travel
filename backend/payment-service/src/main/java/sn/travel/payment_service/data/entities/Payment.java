package sn.travel.payment_service.data.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import sn.travel.payment_service.data.enums.PaymentMethod;
import sn.travel.payment_service.data.enums.PaymentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing a payment transaction.
 * Each payment corresponds to exactly one subscription.
 */
@Entity
@Table(name = "payments", indexes = {
        @Index(name = "idx_payment_subscription", columnList = "subscription_id"),
        @Index(name = "idx_payment_traveler", columnList = "traveler_id"),
        @Index(name = "idx_payment_travel", columnList = "travel_id"),
        @Index(name = "idx_payment_status", columnList = "status"),
        @Index(name = "idx_payment_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "subscription_id", nullable = false, unique = true)
    private UUID subscriptionId;

    @Column(name = "travel_id", nullable = false)
    private UUID travelId;

    @Column(name = "traveler_id", nullable = false)
    private UUID travelerId;

    @Column(name = "travel_title", length = 255)
    private String travelTitle;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "currency", nullable = false, length = 10)
    @Builder.Default
    private String currency = "XOF";

    @Enumerated(EnumType.STRING)
    @Column(name = "method", nullable = false, length = 20)
    @Builder.Default
    private PaymentMethod method = PaymentMethod.SIMULATED;

    @Column(name = "transaction_id", length = 255)
    private String transactionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "failure_reason", length = 500)
    private String failureReason;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
