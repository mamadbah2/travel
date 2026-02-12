package sn.travel.travel_service.data.records;

import java.util.UUID;

/**
 * Event received when payment processing is completed.
 * Consumed by travel-service to update subscription status.
 */
public record PaymentResultEvent(
        UUID subscriptionId,
        UUID travelId,
        UUID travelerId,
        String status,
        String transactionId,
        String failureReason
) {}
