package sn.travel.rec_service.web.dto.requests;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * Requete de mise a jour d'un feedback.
 */
public record UpdateFeedbackRequest(
        @Min(value = 1, message = "La note minimale est 1")
        @Max(value = 5, message = "La note maximale est 5")
        Integer rating,

        String comment
) {}
