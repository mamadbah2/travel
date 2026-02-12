package sn.travel.travel_service.web.dto.requests;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import sn.travel.travel_service.data.enums.AccommodationType;
import sn.travel.travel_service.data.enums.TransportationType;
import sn.travel.travel_service.data.enums.TravelStatus;

import java.time.LocalDate;
import java.util.List;

/**
 * Request DTO for updating an existing travel offer.
 * All fields are optional (null = no change).
 */
public record UpdateTravelRequest(
        @Size(max = 255, message = "Title must not exceed 255 characters")
        String title,

        @Size(max = 5000, message = "Description must not exceed 5000 characters")
        String description,

        @Future(message = "Start date must be in the future")
        LocalDate startDate,

        @Future(message = "End date must be in the future")
        LocalDate endDate,

        @Positive(message = "Price must be positive")
        Double price,

        @Min(value = 1, message = "Max capacity must be at least 1")
        Integer maxCapacity,

        TravelStatus status,

        AccommodationType accommodationType,

        @Size(max = 255, message = "Accommodation name must not exceed 255 characters")
        String accommodationName,

        TransportationType transportationType,

        @Size(max = 500, message = "Transportation details must not exceed 500 characters")
        String transportationDetails,

        @Valid
        List<DestinationRequest> destinations,

        @Valid
        List<ActivityRequest> activities
) {}
