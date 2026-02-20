package sn.travel.payment_service.data.records;

import java.util.UUID;

/**
 * Event published to travel-service after payment processing completes.
 * Used to confirm or cancel the subscription.
 */
public record PaymentCompletedEvent(
        UUID subscriptionId,
        UUID travelId,
        UUID travelerId,
        String status,
        String transactionId,
        String failureReason
) {}
