package sn.travel.rec_service.web.dto.responses;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Reponse contenant un feedback.
 */
public record FeedbackResponse(
        Long id,
        UUID travelerId,
        UUID travelId,
        String travelTitle,
        Integer rating,
        String comment,
        LocalDateTime createdAt
) {}
