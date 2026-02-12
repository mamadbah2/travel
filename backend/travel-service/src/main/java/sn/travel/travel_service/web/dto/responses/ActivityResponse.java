package sn.travel.travel_service.web.dto.responses;

import java.util.UUID;

/**
 * Response DTO for an activity.
 */
public record ActivityResponse(
        UUID id,
        String name,
        String description,
        String location,
        Integer displayOrder
) {}
