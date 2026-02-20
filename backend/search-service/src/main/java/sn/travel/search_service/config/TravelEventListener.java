package sn.travel.search_service.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import sn.travel.search_service.data.records.TravelCreatedEvent;
import sn.travel.search_service.data.records.TravelDeletedEvent;
import sn.travel.search_service.data.records.TravelUpdatedEvent;
import sn.travel.search_service.services.SearchService;

/**
 * RabbitMQ event consumer for travel lifecycle events.
 * Keeps the Elasticsearch index in sync with the travel-service (CQRS pattern).
 *
 * Events consumed:
 * - TravelCreatedEvent  → Index new travel document
 * - TravelUpdatedEvent  → Re-index updated travel document
 * - TravelDeletedEvent  → Remove travel document from index
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TravelEventListener {

    private final SearchService searchService;

    /**
     * Handles TravelCreatedEvent — indexes a newly published travel.
     */
    @RabbitListener(queues = RabbitMQConfig.TRAVEL_CREATED_QUEUE)
    public void handleTravelCreated(TravelCreatedEvent event) {
        log.info("Received TravelCreatedEvent: travelId={}, title='{}'", event.travelId(), event.title());

        try {
            searchService.indexTravel(event);
        } catch (Exception e) {
            log.error("Failed to index travel {}: {}", event.travelId(), e.getMessage(), e);
        }
    }

    /**
     * Handles TravelUpdatedEvent — re-indexes an updated travel.
     */
    @RabbitListener(queues = RabbitMQConfig.TRAVEL_UPDATED_QUEUE)
    public void handleTravelUpdated(TravelUpdatedEvent event) {
        log.info("Received TravelUpdatedEvent: travelId={}, title='{}'", event.travelId(), event.title());

        try {
            searchService.updateTravel(event);
        } catch (Exception e) {
            log.error("Failed to update travel index {}: {}", event.travelId(), e.getMessage(), e);
        }
    }

    /**
     * Handles TravelDeletedEvent — removes a travel from the index.
     */
    @RabbitListener(queues = RabbitMQConfig.TRAVEL_DELETED_QUEUE)
    public void handleTravelDeleted(TravelDeletedEvent event) {
        log.info("Received TravelDeletedEvent: travelId={}", event.travelId());

        try {
            searchService.deleteTravel(event.travelId().toString());
        } catch (Exception e) {
            log.error("Failed to delete travel from index {}: {}", event.travelId(), e.getMessage(), e);
        }
    }
}
