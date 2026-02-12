package sn.travel.travel_service.data.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import sn.travel.travel_service.data.enums.SubscriptionStatus;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing a traveler's subscription (inscription) to a travel.
 * Tracks the lifecycle: PENDING_PAYMENT → CONFIRMED / CANCELLED.
 */
@Entity
@Table(name = "subscriptions", indexes = {
        @Index(name = "idx_subscription_traveler", columnList = "traveler_id"),
        @Index(name = "idx_subscription_travel", columnList = "travel_id"),
        @Index(name = "idx_subscription_status", columnList = "status")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_traveler_travel", columnNames = {"traveler_id", "travel_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "traveler_id", nullable = false)
    private UUID travelerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_id", nullable = false)
    private Travel travel;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private SubscriptionStatus status = SubscriptionStatus.PENDING_PAYMENT;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
