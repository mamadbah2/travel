package sn.travel.rec_service.web.dto.responses;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Reponse contenant un voyage recommande.
 */
public record RecommendationResponse(
        UUID travelId,
        String title,
        String description,
        Double price,
        LocalDate startDate,
        LocalDate endDate,
        String status,
        String accommodationType,
        String transportationType,
        List<String> destinations,
        List<String> activities,
        Double averageRating,
        Long subscriptionCount
) {}
