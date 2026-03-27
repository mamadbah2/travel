package sn.travel.rec_service.services;

import sn.travel.rec_service.web.dto.responses.RecommendationResponse;

import java.util.List;
import java.util.UUID;

/**
 * Service de recommandations personnalisees.
 */
public interface RecommendationService {

    List<RecommendationResponse> getPersonalized(UUID travelerId, int limit);

    List<RecommendationResponse> getPopular(int limit);

    List<RecommendationResponse> getSimilar(UUID travelId, int limit);
}
