package sn.travel.travel_service.web.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for an activity within a travel.
 */
public record ActivityRequest(
        @NotBlank(message = "Activity name is required")
        @Size(max = 255, message = "Activity name must not exceed 255 characters")
        String name,

        @Size(max = 5000, message = "Description must not exceed 5000 characters")
        String description,

        @Size(max = 255, message = "Location must not exceed 255 characters")
        String location,

        Integer displayOrder
) {}
