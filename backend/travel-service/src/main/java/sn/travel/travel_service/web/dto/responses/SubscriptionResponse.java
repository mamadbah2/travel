package sn.travel.travel_service.web.dto.responses;

import sn.travel.travel_service.data.enums.SubscriptionStatus;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for a subscription.
 */
public record SubscriptionResponse(
        UUID id,
        UUID travelerId,
        UUID travelId,
        String travelTitle,
        SubscriptionStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
