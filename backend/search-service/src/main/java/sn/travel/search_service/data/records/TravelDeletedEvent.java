package sn.travel.search_service.data.records;

import java.util.UUID;

/**
 * Event consumed from RabbitMQ when a travel is deleted or cancelled.
 * Published by travel-service on the travel.exchange with routing key "travel.deleted".
 */
public record TravelDeletedEvent(
        UUID travelId
) {}
