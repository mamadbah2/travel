package sn.travel.notification_service.data.records;

import java.util.UUID;

/**
 * Event consumed from travel-service when a traveler subscribes to a travel.
 * Published on: subscription.exchange with routing key subscription.created
 */
public record SubscriptionCreatedEvent(
        UUID subscriptionId,
        UUID travelId,
        UUID travelerId,
        String travelTitle,
        Double amount,
        String currency
) {
}
