package sn.travel.travel_service.data.records;

import java.util.UUID;

/**
 * Event published when a new subscription is created (PENDING_PAYMENT).
 * Consumed by the payment-service to initiate payment processing.
 */
public record SubscriptionCreatedEvent(
        UUID subscriptionId,
        UUID travelId,
        UUID travelerId,
        String travelTitle,
        Double amount,
        String currency
) {}
