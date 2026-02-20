package sn.travel.notification_service.web.mappers;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import sn.travel.notification_service.data.entities.Notification;
import sn.travel.notification_service.web.dto.responses.NotificationResponse;
import sn.travel.notification_service.web.dto.responses.PageResponse;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manual mapper for Notification entity <-> DTO conversions.
 * Replaces MapStruct to avoid Java 25 annotation processing incompatibility.
 */
@Component
public class NotificationMapper {

    public NotificationResponse toResponse(Notification notification) {
        if (notification == null) {
            return null;
        }
        return new NotificationResponse(
                notification.getId(),
                notification.getTravelerId(),
                notification.getTravelId(),
                notification.getSubscriptionId(),
                notification.getRecipientEmail(),
                notification.getSubject(),
                notification.getBody(),
                notification.getType(),
                notification.getStatus(),
                notification.getFailureReason(),
                notification.getCreatedAt(),
                notification.getUpdatedAt()
        );
    }

    public List<NotificationResponse> toResponseList(List<Notification> notifications) {
        if (notifications == null) {
            return Collections.emptyList();
        }
        return notifications.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public PageResponse<NotificationResponse> toPageResponse(Page<Notification> page) {
        if (page == null) {
            return null;
        }
        return new PageResponse<>(
                toResponseList(page.getContent()),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }
}
