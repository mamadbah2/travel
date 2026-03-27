package sn.travel.rec_service.services.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.travel.rec_service.data.nodes.TravelNode;
import sn.travel.rec_service.data.repositories.TravelNodeRepository;
import sn.travel.rec_service.data.repositories.TravelerNodeRepository;
import sn.travel.rec_service.services.RecommendationService;
import sn.travel.rec_service.web.dto.responses.RecommendationResponse;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Implementation du service de recommandations.
 * Combine trois strategies : collaborative, destinations, activites.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RecommendationServiceImpl implements RecommendationService {

    private final TravelerNodeRepository travelerNodeRepository;
    private final TravelNodeRepository travelNodeRepository;

    @Override
    public List<RecommendationResponse> getPersonalized(UUID travelerId, int limit) {
        log.debug("Generation des recommandations personnalisees pour le voyageur : {}", travelerId);

        Set<TravelNode> recommendations = new LinkedHashSet<>();
        recommendations.addAll(travelerNodeRepository.findCollaborativeRecommendations(travelerId, limit));
        recommendations.addAll(travelerNodeRepository.findDestinationBasedRecommendations(travelerId, limit));
        recommendations.addAll(travelerNodeRepository.findActivityBasedRecommendations(travelerId, limit));

        return recommendations.stream()
                .limit(limit)
                .map(this::toRecommendationResponse)
                .toList();
    }

    @Override
    public List<RecommendationResponse> getPopular(int limit) {
        log.debug("Recuperation des voyages populaires (limit={})", limit);

        return travelNodeRepository.findPopular(limit).stream()
                .map(this::toRecommendationResponse)
                .toList();
    }

    @Override
    public List<RecommendationResponse> getSimilar(UUID travelId, int limit) {
        log.debug("Recherche de voyages similaires a : {}", travelId);

        return travelNodeRepository.findSimilar(travelId, limit).stream()
                .map(this::toRecommendationResponse)
                .toList();
    }

    private RecommendationResponse toRecommendationResponse(TravelNode travel) {
        Double avgRating = travelNodeRepository.findAverageRating(travel.getId());
        Long subCount = travelNodeRepository.countSubscriptions(travel.getId());

        return new RecommendationResponse(
                travel.getId(),
                travel.getTitle(),
                travel.getDescription(),
                travel.getPrice(),
                travel.getStartDate(),
                travel.getEndDate(),
                travel.getStatus(),
                travel.getAccommodationType(),
                travel.getTransportationType(),
                travel.getDestinations() != null
                        ? travel.getDestinations().stream().map(d -> d.getName()).toList()
                        : List.of(),
                travel.getActivities() != null
                        ? travel.getActivities().stream().map(a -> a.getName()).toList()
                        : List.of(),
                avgRating,
                subCount != null ? subCount : 0L
        );
    }
}
