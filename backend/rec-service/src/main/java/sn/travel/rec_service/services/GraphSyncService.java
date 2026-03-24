package sn.travel.rec_service.services;

import sn.travel.rec_service.data.records.SubscriptionCreatedEvent;
import sn.travel.rec_service.data.records.TravelCreatedEvent;
import sn.travel.rec_service.data.records.TravelUpdatedEvent;

import java.util.UUID;

/**
 * Service de synchronisation du graphe Neo4j a partir des evenements RabbitMQ.
 */
public interface GraphSyncService {

    void syncTravel(TravelCreatedEvent event);

    void updateTravel(TravelUpdatedEvent event);

    void deleteTravel(UUID travelId);

    void syncSubscription(SubscriptionCreatedEvent event);
}
