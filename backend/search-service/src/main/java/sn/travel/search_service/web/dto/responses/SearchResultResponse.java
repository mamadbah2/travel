package sn.travel.search_service.web.dto.responses;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for a search result.
 */
public record SearchResultResponse(
        String id,
        String managerId,
        String title,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        Integer duration,
        Double price,
        Integer maxCapacity,
        Integer currentBookings,
        Integer availableSpots,
        String status,
        String accommodationType,
        String accommodationName,
        String transportationType,
        String transportationDetails,
        List<DestinationInfo> destinations,
        List<ActivityInfo> activities,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public record DestinationInfo(String name, String country, String city, String description) {}
    public record ActivityInfo(String name, String description, String location) {}
}
