package sn.travel.notification_service.web.controllers.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.travel.notification_service.services.NotificationService;
import sn.travel.notification_service.web.controllers.NotificationController;
import sn.travel.notification_service.web.dto.responses.NotificationResponse;
import sn.travel.notification_service.web.dto.responses.PageResponse;

import java.util.UUID;

/**
 * REST controller implementation for notification consultation endpoints.
 */
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationControllerImpl implements NotificationController {

    private final NotificationService notificationService;

    @Override
    @GetMapping("/{notificationId}")
    public ResponseEntity<NotificationResponse> getNotificationById(@PathVariable UUID notificationId) {
        return ResponseEntity.ok(notificationService.getNotificationById(notificationId));
    }

    @Override
    @GetMapping
    public ResponseEntity<PageResponse<NotificationResponse>> getAllNotifications(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(notificationService.getAllNotifications(pageable));
    }

    @Override
    @GetMapping("/traveler/{travelerId}")
    public ResponseEntity<PageResponse<NotificationResponse>> getNotificationsByTraveler(
            @PathVariable UUID travelerId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(notificationService.getNotificationsByTraveler(travelerId, pageable));
    }

    @Override
    @GetMapping("/travel/{travelId}")
    public ResponseEntity<PageResponse<NotificationResponse>> getNotificationsByTravel(
            @PathVariable UUID travelId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(notificationService.getNotificationsByTravel(travelId, pageable));
    }

    @Override
    @GetMapping("/subscription/{subscriptionId}")
    public ResponseEntity<PageResponse<NotificationResponse>> getNotificationsBySubscription(
            @PathVariable UUID subscriptionId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(notificationService.getNotificationsBySubscription(subscriptionId, pageable));
    }
}
