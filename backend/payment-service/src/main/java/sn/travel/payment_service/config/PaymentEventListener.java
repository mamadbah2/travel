package sn.travel.payment_service.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import sn.travel.payment_service.data.records.SubscriptionCreatedEvent;
import sn.travel.payment_service.services.PaymentService;

/**
 * RabbitMQ listener that consumes SubscriptionCreatedEvent messages
 * from travel-service and triggers payment processing.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventListener {

    private final PaymentService paymentService;

    /**
     * Listens for new subscription events and processes the payment.
     * Queue: subscription.created.queue
     * Group: payment-group (via container concurrency)
     */
    @RabbitListener(queues = RabbitMQConfig.SUBSCRIPTION_CREATED_QUEUE, concurrency = "1-3")
    public void handleSubscriptionCreatedEvent(SubscriptionCreatedEvent event) {
        log.info("Received SubscriptionCreatedEvent: subscriptionId={}, travelId={}, amount={} {}",
                event.subscriptionId(), event.travelId(), event.amount(), event.currency());
        try {
            paymentService.processPayment(event);
        } catch (Exception e) {
            log.error("Error processing payment for subscription {}: {}",
                    event.subscriptionId(), e.getMessage(), e);
            // The message will be requeued by RabbitMQ default error handling
            // In production, configure a DLQ (Dead Letter Queue) for failed messages
            throw e;
        }
    }
}
