package sn.travel.travel_service.data.records;

import java.util.UUID;

/**
 * Event published to RabbitMQ when a travel is deleted or cancelled.
 * Consumed by the search-service to remove the travel from the Elasticsearch index.
 */
public record TravelDeletedEvent(
        UUID travelId
) {}
