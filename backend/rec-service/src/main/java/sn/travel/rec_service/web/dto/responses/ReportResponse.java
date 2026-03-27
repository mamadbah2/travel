package sn.travel.rec_service.web.dto.responses;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Reponse contenant un signalement.
 */
public record ReportResponse(
        Long id,
        UUID reporterId,
        UUID reportedUserId,
        String reason,
        String status,
        LocalDateTime createdAt,
        LocalDateTime resolvedAt
) {}
