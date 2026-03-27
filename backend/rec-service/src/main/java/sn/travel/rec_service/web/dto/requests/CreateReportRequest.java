package sn.travel.rec_service.web.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Requete de creation d'un signalement contre un utilisateur.
 */
public record CreateReportRequest(
        @NotNull(message = "L'identifiant de l'utilisateur signale est obligatoire")
        UUID reportedUserId,

        @NotBlank(message = "La raison du signalement est obligatoire")
        String reason
) {}
