package sn.travel.travel_service.data.records;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Event published to RabbitMQ when a published travel is updated.
 * Consumed by the search-service to re-index the travel in Elasticsearch.
 */
public record TravelUpdatedEvent(
        UUID travelId,
        UUID managerId,
        String title,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        Integer duration,
        Double price,
        Integer maxCapacity,
        Integer currentBookings,
        String status,
        String accommodationType,
        String accommodationName,
        String transportationType,
        String transportationDetails,
        List<DestinationData> destinations,
        List<ActivityData> activities,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public record DestinationData(String name, String country, String city, String description) {}
    public record ActivityData(String name, String description, String location) {}
}
