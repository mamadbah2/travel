package sn.travel.notification_service.web.dto.responses;

import sn.travel.notification_service.data.enums.NotificationStatus;
import sn.travel.notification_service.data.enums.NotificationType;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO response for a notification record.
 */
public record NotificationResponse(
        UUID id,
        UUID travelerId,
        UUID travelId,
        UUID subscriptionId,
        String recipientEmail,
        String subject,
        String body,
        NotificationType type,
        NotificationStatus status,
        String failureReason,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
