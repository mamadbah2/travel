package sn.travel.rec_service.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import sn.travel.rec_service.data.records.SubscriptionCreatedEvent;
import sn.travel.rec_service.data.records.TravelCreatedEvent;
import sn.travel.rec_service.data.records.TravelDeletedEvent;
import sn.travel.rec_service.data.records.TravelUpdatedEvent;
import sn.travel.rec_service.services.GraphSyncService;

/**
 * Consommateur d'evenements RabbitMQ pour la synchronisation du graphe Neo4j.
 * Ecoute les evenements du cycle de vie des voyages et des souscriptions.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RecEventListener {

    private final GraphSyncService graphSyncService;

    @RabbitListener(queues = RabbitMQConfig.TRAVEL_CREATED_QUEUE)
    public void handleTravelCreated(TravelCreatedEvent event) {
        log.info("Evenement recu : TravelCreatedEvent, travelId={}, title='{}'", event.travelId(), event.title());

        try {
            graphSyncService.syncTravel(event);
        } catch (Exception e) {
            log.error("Echec de la synchronisation du voyage {} : {}", event.travelId(), e.getMessage(), e);
        }
    }

    @RabbitListener(queues = RabbitMQConfig.TRAVEL_UPDATED_QUEUE)
    public void handleTravelUpdated(TravelUpdatedEvent event) {
        log.info("Evenement recu : TravelUpdatedEvent, travelId={}, title='{}'", event.travelId(), event.title());

        try {
            graphSyncService.updateTravel(event);
        } catch (Exception e) {
            log.error("Echec de la mise a jour du voyage {} : {}", event.travelId(), e.getMessage(), e);
        }
    }

    @RabbitListener(queues = RabbitMQConfig.TRAVEL_DELETED_QUEUE)
    public void handleTravelDeleted(TravelDeletedEvent event) {
        log.info("Evenement recu : TravelDeletedEvent, travelId={}", event.travelId());

        try {
            graphSyncService.deleteTravel(event.travelId());
        } catch (Exception e) {
            log.error("Echec de la suppression du voyage {} : {}", event.travelId(), e.getMessage(), e);
        }
    }

    @RabbitListener(queues = RabbitMQConfig.SUBSCRIPTION_CREATED_QUEUE)
    public void handleSubscriptionCreated(SubscriptionCreatedEvent event) {
        log.info("Evenement recu : SubscriptionCreatedEvent, travelerId={}, travelId={}",
                event.travelerId(), event.travelId());

        try {
            graphSyncService.syncSubscription(event);
        } catch (Exception e) {
            log.error("Echec de la synchronisation de la souscription traveler={}, travel={} : {}",
                    event.travelerId(), event.travelId(), e.getMessage(), e);
        }
    }
}
