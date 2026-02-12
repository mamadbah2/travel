package sn.travel.travel_service.web.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for a destination within a travel.
 */
public record DestinationRequest(
        @NotBlank(message = "Destination name is required")
        @Size(max = 255, message = "Destination name must not exceed 255 characters")
        String name,

        @NotBlank(message = "Country is required")
        @Size(max = 100, message = "Country must not exceed 100 characters")
        String country,

        @Size(max = 100, message = "City must not exceed 100 characters")
        String city,

        @Size(max = 5000, message = "Description must not exceed 5000 characters")
        String description,

        Integer displayOrder
) {}
