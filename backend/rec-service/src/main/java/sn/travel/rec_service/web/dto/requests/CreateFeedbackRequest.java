package sn.travel.rec_service.web.dto.requests;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Requete de creation d'un feedback (notation) sur un voyage.
 */
public record CreateFeedbackRequest(
        @NotNull(message = "L'identifiant du voyage est obligatoire")
        UUID travelId,

        @NotNull(message = "La note est obligatoire")
        @Min(value = 1, message = "La note minimale est 1")
        @Max(value = 5, message = "La note maximale est 5")
        Integer rating,

        String comment
) {}
