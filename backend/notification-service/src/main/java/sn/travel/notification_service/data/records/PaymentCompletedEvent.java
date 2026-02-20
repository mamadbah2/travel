package sn.travel.notification_service.data.records;

import java.util.UUID;

/**
 * Event consumed from payment-service when a payment is processed.
 * Published on: payment.exchange with routing key payment.success or payment.failed
 */
public record PaymentCompletedEvent(
        UUID subscriptionId,
        UUID travelId,
        UUID travelerId,
        String status,
        String transactionId,
        String failureReason
) {
}
