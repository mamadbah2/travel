package sn.travel.travel_service.services.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import sn.travel.travel_service.config.RabbitMQConfig;
import sn.travel.travel_service.data.records.PaymentResultEvent;
import sn.travel.travel_service.exceptions.SubscriptionNotFoundException;
import sn.travel.travel_service.services.SubscriptionService;

/**
 * RabbitMQ listener for payment result events from the payment-service.
 * Updates subscription status based on payment outcome.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentResultListener {

    private final SubscriptionService subscriptionService;

    @RabbitListener(queues = RabbitMQConfig.PAYMENT_RESULT_QUEUE)
    public void handlePaymentResult(PaymentResultEvent event) {
        log.info("Received payment result for subscription {}: {}", event.subscriptionId(), event.status());
        try {
            boolean success = "SUCCESS".equalsIgnoreCase(event.status());
            subscriptionService.handlePaymentResult(event.subscriptionId(), success);
        } catch (SubscriptionNotFoundException e) {
            // CRITICAL: We received a payment for a subscription that doesn't exist.
            // We catch this to acknowledge the message and stop the retry loop, 
            // but we log it as an error for investigation.
            log.error("CRITICAL: Orphaned payment result received. Subscription ID {} not found. Event: {}", 
                      event.subscriptionId(), event);
        } catch (Exception e) {
            // Other exceptions (DB down, etc.) should probably be thrown to trigger retry/DLQ
            log.error("Error processing payment result for subscription {}", event.subscriptionId(), e);
            throw e; 
        }
    }
}
