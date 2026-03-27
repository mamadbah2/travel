package sn.travel.rec_service.data.records;

import java.util.UUID;

/**
 * Evenement consomme depuis RabbitMQ lors de la suppression d'un voyage.
 */
public record TravelDeletedEvent(
        UUID travelId
) {}
