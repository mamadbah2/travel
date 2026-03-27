package sn.travel.rec_service.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import sn.travel.rec_service.web.dto.responses.RecommendationResponse;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

/**
 * Interface du controleur de recommandations.
 */
@Tag(name = "Recommandations", description = "API de recommandations de voyages")
public interface RecommendationController {

    @Operation(summary = "Recommandations personnalisees", description = "Obtenir des recommandations basees sur l'historique du voyageur connecte")
    ResponseEntity<List<RecommendationResponse>> getPersonalized(Principal principal, int limit);

    @Operation(summary = "Voyages populaires", description = "Obtenir les voyages les plus populaires (public)")
    ResponseEntity<List<RecommendationResponse>> getPopular(int limit);

    @Operation(summary = "Voyages similaires", description = "Obtenir des voyages similaires a un voyage donne (public)")
    ResponseEntity<List<RecommendationResponse>> getSimilar(UUID travelId, int limit);
}
