package sn.travel.notification_service.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import sn.travel.notification_service.data.records.PaymentCompletedEvent;
import sn.travel.notification_service.data.records.SubscriptionCreatedEvent;
import sn.travel.notification_service.services.NotificationService;

/**
 * RabbitMQ listener that consumes events from travel-service and payment-service
 * and triggers email notifications.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {

    private final NotificationService notificationService;

    /**
     * Listens for new subscription events and sends a "booking received" email.
     * Queue: notification.subscription.queue
     */
    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_SUBSCRIPTION_QUEUE, concurrency = "1-3")
    public void handleSubscriptionCreatedEvent(SubscriptionCreatedEvent event) {
        log.info("Received SubscriptionCreatedEvent: subscriptionId={}, travelId={}, travelerId={}",
                event.subscriptionId(), event.travelId(), event.travelerId());
        try {
            notificationService.handleSubscriptionCreated(event);
        } catch (Exception e) {
            log.error("Error processing subscription notification for subscription {}: {}",
                    event.subscriptionId(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Listens for payment completed events and sends a confirmation or failure email.
     * Queue: notification.payment.queue
     */
    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_PAYMENT_QUEUE, concurrency = "1-3")
    public void handlePaymentCompletedEvent(PaymentCompletedEvent event) {
        log.info("Received PaymentCompletedEvent: subscriptionId={}, status={}",
                event.subscriptionId(), event.status());
        try {
            notificationService.handlePaymentCompleted(event);
        } catch (Exception e) {
            log.error("Error processing payment notification for subscription {}: {}",
                    event.subscriptionId(), e.getMessage(), e);
            throw e;
        }
    }
}
