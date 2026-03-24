package sn.travel.rec_service.services.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.travel.rec_service.data.nodes.ActivityNode;
import sn.travel.rec_service.data.nodes.DestinationNode;
import sn.travel.rec_service.data.nodes.TravelNode;
import sn.travel.rec_service.data.nodes.TravelerNode;
import sn.travel.rec_service.data.records.SubscriptionCreatedEvent;
import sn.travel.rec_service.data.records.TravelCreatedEvent;
import sn.travel.rec_service.data.records.TravelUpdatedEvent;
import sn.travel.rec_service.data.relationships.SubscribedToRelationship;
import sn.travel.rec_service.data.repositories.ActivityNodeRepository;
import sn.travel.rec_service.data.repositories.DestinationNodeRepository;
import sn.travel.rec_service.data.repositories.TravelNodeRepository;
import sn.travel.rec_service.data.repositories.TravelerNodeRepository;
import sn.travel.rec_service.services.GraphSyncService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Implementation du service de synchronisation du graphe Neo4j.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class GraphSyncServiceImpl implements GraphSyncService {

    private final TravelNodeRepository travelNodeRepository;
    private final TravelerNodeRepository travelerNodeRepository;
    private final DestinationNodeRepository destinationNodeRepository;
    private final ActivityNodeRepository activityNodeRepository;

    @Override
    public void syncTravel(TravelCreatedEvent event) {
        log.info("Synchronisation du voyage dans le graphe : {}", event.travelId());

        List<DestinationNode> destinations = resolveDestinations(event.destinations());
        List<ActivityNode> activities = resolveActivities(event.activities());

        TravelNode travelNode = TravelNode.builder()
                .id(event.travelId())
                .title(event.title())
                .description(event.description())
                .price(event.price())
                .startDate(event.startDate())
                .endDate(event.endDate())
                .status(event.status())
                .accommodationType(event.accommodationType())
                .transportationType(event.transportationType())
                .destinations(destinations)
                .activities(activities)
                .build();

        travelNodeRepository.save(travelNode);
        log.info("Voyage synchronise avec succes : {}", event.travelId());
    }

    @Override
    public void updateTravel(TravelUpdatedEvent event) {
        log.info("Mise a jour du voyage dans le graphe : {}", event.travelId());

        TravelNode travelNode = travelNodeRepository.findById(event.travelId())
                .orElse(TravelNode.builder().id(event.travelId()).build());

        travelNode.setTitle(event.title());
        travelNode.setDescription(event.description());
        travelNode.setPrice(event.price());
        travelNode.setStartDate(event.startDate());
        travelNode.setEndDate(event.endDate());
        travelNode.setStatus(event.status());
        travelNode.setAccommodationType(event.accommodationType());
        travelNode.setTransportationType(event.transportationType());
        travelNode.setDestinations(resolveUpdatedDestinations(event.destinations()));
        travelNode.setActivities(resolveUpdatedActivities(event.activities()));

        travelNodeRepository.save(travelNode);
        log.info("Voyage mis a jour avec succes : {}", event.travelId());
    }

    @Override
    public void deleteTravel(UUID travelId) {
        log.info("Suppression du voyage du graphe : {}", travelId);
        travelNodeRepository.deleteById(travelId);
        log.info("Voyage supprime avec succes : {}", travelId);
    }

    @Override
    public void syncSubscription(SubscriptionCreatedEvent event) {
        log.info("Synchronisation de la souscription dans le graphe : traveler={}, travel={}",
                event.travelerId(), event.travelId());

        TravelerNode traveler = travelerNodeRepository.findById(event.travelerId())
                .orElseGet(() -> TravelerNode.builder()
                        .id(event.travelerId())
                        .subscriptions(new ArrayList<>())
                        .ratings(new ArrayList<>())
                        .reports(new ArrayList<>())
                        .build());

        TravelNode travel = travelNodeRepository.findById(event.travelId()).orElse(null);
        if (travel == null) {
            log.warn("Voyage non trouve dans le graphe pour la souscription : {}", event.travelId());
            return;
        }

        SubscribedToRelationship subscription = SubscribedToRelationship.builder()
                .travel(travel)
                .createdAt(LocalDateTime.now())
                .build();

        traveler.getSubscriptions().add(subscription);
        travelerNodeRepository.save(traveler);
        log.info("Souscription synchronisee avec succes : traveler={}, travel={}",
                event.travelerId(), event.travelId());
    }

    private List<DestinationNode> resolveDestinations(List<TravelCreatedEvent.DestinationData> destinations) {
        if (destinations == null) return new ArrayList<>();

        return destinations.stream()
                .map(d -> resolveDestination(d.name(), d.country(), d.city()))
                .toList();
    }

    private List<DestinationNode> resolveUpdatedDestinations(List<TravelUpdatedEvent.DestinationData> destinations) {
        if (destinations == null) return new ArrayList<>();

        return destinations.stream()
                .map(d -> resolveDestination(d.name(), d.country(), d.city()))
                .toList();
    }

    private DestinationNode resolveDestination(String name, String country, String city) {
        return destinationNodeRepository
                .findByNameAndCountryAndCity(name, country, city)
                .orElseGet(() -> destinationNodeRepository.save(
                        DestinationNode.builder()
                                .name(name)
                                .country(country)
                                .city(city)
                                .build()
                ));
    }

    private List<ActivityNode> resolveActivities(List<TravelCreatedEvent.ActivityData> activities) {
        if (activities == null) return new ArrayList<>();

        return activities.stream()
                .map(a -> resolveActivity(a.name(), a.description()))
                .toList();
    }

    private List<ActivityNode> resolveUpdatedActivities(List<TravelUpdatedEvent.ActivityData> activities) {
        if (activities == null) return new ArrayList<>();

        return activities.stream()
                .map(a -> resolveActivity(a.name(), a.description()))
                .toList();
    }

    private ActivityNode resolveActivity(String name, String description) {
        return activityNodeRepository
                .findByName(name)
                .orElseGet(() -> activityNodeRepository.save(
                        ActivityNode.builder()
                                .name(name)
                                .description(description)
                                .build()
                ));
    }
}
