package sn.travel.notification_service.services;

import org.springframework.data.domain.Pageable;
import sn.travel.notification_service.data.records.PaymentCompletedEvent;
import sn.travel.notification_service.data.records.SubscriptionCreatedEvent;
import sn.travel.notification_service.web.dto.responses.NotificationResponse;
import sn.travel.notification_service.web.dto.responses.PageResponse;

import java.util.UUID;

/**
 * Service interface for managing notifications.
 */
public interface NotificationService {

    /**
     * Handle a SubscriptionCreatedEvent: send "booking received" email.
     */
    void handleSubscriptionCreated(SubscriptionCreatedEvent event);

    /**
     * Handle a PaymentCompletedEvent: send payment result email.
     */
    void handlePaymentCompleted(PaymentCompletedEvent event);

    /**
     * Get a notification by ID.
     */
    NotificationResponse getNotificationById(UUID notificationId);

    /**
     * Get all notifications (Admin).
     */
    PageResponse<NotificationResponse> getAllNotifications(Pageable pageable);

    /**
     * Get notifications for a specific traveler.
     */
    PageResponse<NotificationResponse> getNotificationsByTraveler(UUID travelerId, Pageable pageable);

    /**
     * Get notifications for a specific travel.
     */
    PageResponse<NotificationResponse> getNotificationsByTravel(UUID travelId, Pageable pageable);

    /**
     * Get notifications for a specific subscription.
     */
    PageResponse<NotificationResponse> getNotificationsBySubscription(UUID subscriptionId, Pageable pageable);
}
