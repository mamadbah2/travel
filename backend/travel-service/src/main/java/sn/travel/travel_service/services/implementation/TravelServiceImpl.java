package sn.travel.travel_service.services.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.travel.travel_service.config.RabbitMQConfig;
import sn.travel.travel_service.data.entities.Activity;
import sn.travel.travel_service.data.entities.Destination;
import sn.travel.travel_service.data.entities.Travel;
import sn.travel.travel_service.data.enums.TravelStatus;
import sn.travel.travel_service.data.records.TravelCreatedEvent;
import sn.travel.travel_service.data.records.TravelDeletedEvent;
import sn.travel.travel_service.data.records.TravelUpdatedEvent;
import sn.travel.travel_service.data.repositories.TravelRepository;
import sn.travel.travel_service.exceptions.TravelNotFoundException;
import sn.travel.travel_service.exceptions.UnauthorizedAccessException;
import sn.travel.travel_service.services.TravelService;
import sn.travel.travel_service.web.dto.requests.CreateTravelRequest;
import sn.travel.travel_service.web.dto.requests.UpdateTravelRequest;
import sn.travel.travel_service.web.dto.responses.PageResponse;
import sn.travel.travel_service.web.dto.responses.TravelResponse;
import sn.travel.travel_service.web.mappers.TravelMapper;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of TravelService.
 * Manages the lifecycle of travel offers.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TravelServiceImpl implements TravelService {

    private final TravelRepository travelRepository;
    private final TravelMapper travelMapper;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public TravelResponse createTravel(CreateTravelRequest request, UUID managerId) {
        log.info("Creating travel '{}' for manager {}", request.title(), managerId);

        Travel travel = travelMapper.toEntity(request);
        travel.setManagerId(managerId);
        travel.setStatus(TravelStatus.DRAFT);
        travel.setCurrentBookings(0);
        travel.setDuration((int) ChronoUnit.DAYS.between(request.startDate(), request.endDate()));

        // Map and associate destinations
        if (request.destinations() != null && !request.destinations().isEmpty()) {
            List<Destination> destinations = travelMapper.toDestinationEntityList(request.destinations());
            destinations.forEach(d -> d.setTravel(travel));
            travel.setDestinations(destinations);
        }

        // Map and associate activities
        if (request.activities() != null && !request.activities().isEmpty()) {
            List<Activity> activities = travelMapper.toActivityEntityList(request.activities());
            activities.forEach(a -> a.setTravel(travel));
            travel.setActivities(activities);
        }

        Travel savedTravel = travelRepository.save(travel);
        log.info("Travel created with ID: {}", savedTravel.getId());

        return travelMapper.toResponse(savedTravel);
    }

    @Override
    public TravelResponse updateTravel(UUID travelId, UpdateTravelRequest request, UUID managerId) {
        Travel travel = findTravelOrThrow(travelId);
        verifyTravelOwnership(travel, managerId);

        log.info("Updating travel {} by manager {}", travelId, managerId);

        travelMapper.updateEntity(request, travel);

        // Recalculate duration if dates changed
        if (request.startDate() != null || request.endDate() != null) {
            travel.setDuration((int) ChronoUnit.DAYS.between(travel.getStartDate(), travel.getEndDate()));
        }

        // Update destinations if provided
        if (request.destinations() != null) {
            travel.getDestinations().clear();
            List<Destination> newDestinations = travelMapper.toDestinationEntityList(request.destinations());
            newDestinations.forEach(d -> d.setTravel(travel));
            travel.getDestinations().addAll(newDestinations);
        }

        // Update activities if provided
        if (request.activities() != null) {
            travel.getActivities().clear();
            List<Activity> newActivities = travelMapper.toActivityEntityList(request.activities());
            newActivities.forEach(a -> a.setTravel(travel));
            travel.getActivities().addAll(newActivities);
        }

        Travel updatedTravel = travelRepository.save(travel);

        // Publish update event for search indexing (only if travel is PUBLISHED)
        if (updatedTravel.getStatus() == TravelStatus.PUBLISHED) {
            publishTravelUpdatedEvent(updatedTravel);
        }

        return travelMapper.toResponse(updatedTravel);
    }

    @Override
    @Transactional(readOnly = true)
    public TravelResponse getTravelById(UUID travelId) {
        Travel travel = findTravelOrThrow(travelId);
        return travelMapper.toResponse(travel);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<TravelResponse> getAvailableTravels(Pageable pageable) {
        Page<Travel> travels = travelRepository.findAvailableTravels(LocalDate.now(), pageable);
        return travelMapper.toPageResponse(travels);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<TravelResponse> searchTravels(String search, Pageable pageable) {
        Page<Travel> travels = travelRepository.searchPublishedTravels(search, LocalDate.now(), pageable);
        return travelMapper.toPageResponse(travels);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<TravelResponse> getTravelsByManager(UUID managerId, Pageable pageable) {
        Page<Travel> travels = travelRepository.findByManagerId(managerId, pageable);
        return travelMapper.toPageResponse(travels);
    }

    @Override
    public void deleteTravel(UUID travelId, UUID userId, String role) {
        Travel travel = findTravelOrThrow(travelId);

        if (!"ADMIN".equals(role)) {
            verifyTravelOwnership(travel, userId);
        }

        log.warn("Deleting travel {} (cascade will remove subscriptions)", travelId);

        // Publish delete event for search index removal
        publishTravelDeletedEvent(travel.getId());

        travelRepository.delete(travel);
    }

    @Override
    public TravelResponse publishTravel(UUID travelId, UUID managerId) {
        Travel travel = findTravelOrThrow(travelId);
        verifyTravelOwnership(travel, managerId);

        if (travel.getStatus() != TravelStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT travels can be published. Current status: " + travel.getStatus());
        }

        travel.setStatus(TravelStatus.PUBLISHED);
        Travel publishedTravel = travelRepository.save(travel);
        log.info("Travel {} published by manager {}", travelId, managerId);

        // Publish event for search indexing
        publishTravelCreatedEvent(publishedTravel);

        return travelMapper.toResponse(publishedTravel);
    }

    @Override
    public TravelResponse cancelTravel(UUID travelId, UUID managerId) {
        Travel travel = findTravelOrThrow(travelId);
        verifyTravelOwnership(travel, managerId);

        travel.setStatus(TravelStatus.CANCELLED);
        Travel cancelledTravel = travelRepository.save(travel);
        log.info("Travel {} cancelled by manager {}", travelId, managerId);

        // Publish delete event to remove from search index
        publishTravelDeletedEvent(cancelledTravel.getId());

        return travelMapper.toResponse(cancelledTravel);
    }

    // ---- Private helpers ----

    private Travel findTravelOrThrow(UUID travelId) {
        return travelRepository.findById(travelId)
                .orElseThrow(() -> new TravelNotFoundException(travelId.toString()));
    }

    private void verifyTravelOwnership(Travel travel, UUID managerId) {
        if (!travel.getManagerId().equals(managerId)) {
            throw new UnauthorizedAccessException("You are not the owner of this travel");
        }
    }

    // ---- Event publishing helpers ----

    private void publishTravelCreatedEvent(Travel travel) {
        try {
            TravelCreatedEvent event = buildTravelCreatedEvent(travel);
            rabbitTemplate.convertAndSend(RabbitMQConfig.TRAVEL_EXCHANGE, RabbitMQConfig.TRAVEL_CREATED_KEY, event);
            log.info("Published TravelCreatedEvent for travel {}", travel.getId());
        } catch (Exception e) {
            log.error("Failed to publish TravelCreatedEvent for travel {}: {}", travel.getId(), e.getMessage(), e);
        }
    }

    private void publishTravelUpdatedEvent(Travel travel) {
        try {
            TravelUpdatedEvent event = buildTravelUpdatedEvent(travel);
            rabbitTemplate.convertAndSend(RabbitMQConfig.TRAVEL_EXCHANGE, RabbitMQConfig.TRAVEL_UPDATED_KEY, event);
            log.info("Published TravelUpdatedEvent for travel {}", travel.getId());
        } catch (Exception e) {
            log.error("Failed to publish TravelUpdatedEvent for travel {}: {}", travel.getId(), e.getMessage(), e);
        }
    }

    private void publishTravelDeletedEvent(UUID travelId) {
        try {
            TravelDeletedEvent event = new TravelDeletedEvent(travelId);
            rabbitTemplate.convertAndSend(RabbitMQConfig.TRAVEL_EXCHANGE, RabbitMQConfig.TRAVEL_DELETED_KEY, event);
            log.info("Published TravelDeletedEvent for travel {}", travelId);
        } catch (Exception e) {
            log.error("Failed to publish TravelDeletedEvent for travel {}: {}", travelId, e.getMessage(), e);
        }
    }

    private TravelCreatedEvent buildTravelCreatedEvent(Travel travel) {
        return new TravelCreatedEvent(
                travel.getId(),
                travel.getManagerId(),
                travel.getTitle(),
                travel.getDescription(),
                travel.getStartDate(),
                travel.getEndDate(),
                travel.getDuration(),
                travel.getPrice(),
                travel.getMaxCapacity(),
                travel.getCurrentBookings(),
                travel.getStatus().name(),
                travel.getAccommodationType() != null ? travel.getAccommodationType().name() : null,
                travel.getAccommodationName(),
                travel.getTransportationType() != null ? travel.getTransportationType().name() : null,
                travel.getTransportationDetails(),
                travel.getDestinations() != null ? travel.getDestinations().stream()
                        .map(d -> new TravelCreatedEvent.DestinationData(d.getName(), d.getCountry(), d.getCity(), d.getDescription()))
                        .toList() : List.of(),
                travel.getActivities() != null ? travel.getActivities().stream()
                        .map(a -> new TravelCreatedEvent.ActivityData(a.getName(), a.getDescription(), a.getLocation()))
                        .toList() : List.of(),
                travel.getCreatedAt(),
                travel.getUpdatedAt()
        );
    }

    private TravelUpdatedEvent buildTravelUpdatedEvent(Travel travel) {
        return new TravelUpdatedEvent(
                travel.getId(),
                travel.getManagerId(),
                travel.getTitle(),
                travel.getDescription(),
                travel.getStartDate(),
                travel.getEndDate(),
                travel.getDuration(),
                travel.getPrice(),
                travel.getMaxCapacity(),
                travel.getCurrentBookings(),
                travel.getStatus().name(),
                travel.getAccommodationType() != null ? travel.getAccommodationType().name() : null,
                travel.getAccommodationName(),
                travel.getTransportationType() != null ? travel.getTransportationType().name() : null,
                travel.getTransportationDetails(),
                travel.getDestinations() != null ? travel.getDestinations().stream()
                        .map(d -> new TravelUpdatedEvent.DestinationData(d.getName(), d.getCountry(), d.getCity(), d.getDescription()))
                        .toList() : List.of(),
                travel.getActivities() != null ? travel.getActivities().stream()
                        .map(a -> new TravelUpdatedEvent.ActivityData(a.getName(), a.getDescription(), a.getLocation()))
                        .toList() : List.of(),
                travel.getCreatedAt(),
                travel.getUpdatedAt()
        );
    }
}
