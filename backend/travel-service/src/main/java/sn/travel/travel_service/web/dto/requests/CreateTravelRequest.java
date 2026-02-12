package sn.travel.travel_service.web.dto.requests;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import sn.travel.travel_service.data.enums.AccommodationType;
import sn.travel.travel_service.data.enums.TransportationType;

import java.time.LocalDate;
import java.util.List;

/**
 * Request DTO for creating a new travel offer.
 */
public record CreateTravelRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 255, message = "Title must not exceed 255 characters")
        String title,

        @Size(max = 5000, message = "Description must not exceed 5000 characters")
        String description,

        @NotNull(message = "Start date is required")
        @Future(message = "Start date must be in the future")
        LocalDate startDate,

        @NotNull(message = "End date is required")
        @Future(message = "End date must be in the future")
        LocalDate endDate,

        @NotNull(message = "Price is required")
        @Positive(message = "Price must be positive")
        Double price,

        @NotNull(message = "Max capacity is required")
        @Min(value = 1, message = "Max capacity must be at least 1")
        Integer maxCapacity,

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
