package sn.travel.travel_service.web.dto.responses;

import sn.travel.travel_service.data.enums.AccommodationType;
import sn.travel.travel_service.data.enums.TransportationType;
import sn.travel.travel_service.data.enums.TravelStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for a travel offer.
 */
public record TravelResponse(
        UUID id,
        UUID managerId,
        String title,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        Integer duration,
        Double price,
        Integer maxCapacity,
        Integer currentBookings,
        Integer availableSpots,
        TravelStatus status,
        AccommodationType accommodationType,
        String accommodationName,
        TransportationType transportationType,
        String transportationDetails,
        List<DestinationResponse> destinations,
        List<ActivityResponse> activities,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
