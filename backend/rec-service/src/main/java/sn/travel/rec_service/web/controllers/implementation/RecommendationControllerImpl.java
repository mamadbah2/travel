package sn.travel.rec_service.web.controllers.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.travel.rec_service.services.RecommendationService;
import sn.travel.rec_service.web.controllers.RecommendationController;
import sn.travel.rec_service.web.dto.responses.RecommendationResponse;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

/**
 * Implementation du controleur de recommandations.
 */
@RestController
@RequestMapping("/api/v1/recommendations")
@RequiredArgsConstructor
public class RecommendationControllerImpl implements RecommendationController {

    private final RecommendationService recommendationService;

    @Override
    @GetMapping("/personalized")
    public ResponseEntity<List<RecommendationResponse>> getPersonalized(
            Principal principal,
            @RequestParam(defaultValue = "10") int limit) {
        UUID travelerId = UUID.fromString(principal.getName());
        return ResponseEntity.ok(recommendationService.getPersonalized(travelerId, limit));
    }

    @Override
    @GetMapping("/popular")
    public ResponseEntity<List<RecommendationResponse>> getPopular(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(recommendationService.getPopular(limit));
    }

    @Override
    @GetMapping("/similar/{travelId}")
    public ResponseEntity<List<RecommendationResponse>> getSimilar(
            @PathVariable UUID travelId,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(recommendationService.getSimilar(travelId, limit));
    }
}
