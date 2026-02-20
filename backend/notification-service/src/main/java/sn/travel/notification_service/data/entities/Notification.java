package sn.travel.notification_service.data.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import sn.travel.notification_service.data.enums.NotificationStatus;
import sn.travel.notification_service.data.enums.NotificationType;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing a notification record.
 * Tracks every email sent (or attempted) for auditing and consultation.
 */
@Entity
@Table(name = "notifications", indexes = {
        @Index(name = "idx_notification_traveler", columnList = "traveler_id"),
        @Index(name = "idx_notification_travel", columnList = "travel_id"),
        @Index(name = "idx_notification_subscription", columnList = "subscription_id"),
        @Index(name = "idx_notification_type", columnList = "type"),
        @Index(name = "idx_notification_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "traveler_id", nullable = false)
    private UUID travelerId;

    @Column(name = "travel_id", nullable = false)
    private UUID travelId;

    @Column(name = "subscription_id", nullable = false)
    private UUID subscriptionId;

    @Column(name = "recipient_email", nullable = false)
    private String recipientEmail;

    @Column(nullable = false)
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private NotificationStatus status = NotificationStatus.PENDING;

    @Column(name = "failure_reason", length = 500)
    private String failureReason;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
