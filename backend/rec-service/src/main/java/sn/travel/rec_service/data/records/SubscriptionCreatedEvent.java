package sn.travel.rec_service.data.records;

import java.util.UUID;

/**
 * Evenement consomme depuis RabbitMQ lors de la creation d'une souscription.
 */
public record SubscriptionCreatedEvent(
        UUID subscriptionId,
        UUID travelId,
        UUID travelerId,
        String travelTitle,
        Double amount,
        String currency
) {}
