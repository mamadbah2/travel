package sn.travel.travel_service.web.dto.responses;

import java.util.UUID;

/**
 * Response DTO for a destination.
 */
public record DestinationResponse(
        UUID id,
        String name,
        String country,
        String city,
        String description,
        Integer displayOrder
) {}
