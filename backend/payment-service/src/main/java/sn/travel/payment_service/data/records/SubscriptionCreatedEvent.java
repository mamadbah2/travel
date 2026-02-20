package sn.travel.payment_service.data.records;

import java.util.UUID;

/**
 * Event received from travel-service when a traveler subscribes to a travel.
 * This triggers payment processing.
 */
public record SubscriptionCreatedEvent(
        UUID subscriptionId,
        UUID travelId,
        UUID travelerId,
        String travelTitle,
        Double amount,
        String currency
) {}
