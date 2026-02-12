package sn.travel.travel_service.services.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.travel.travel_service.data.entities.Activity;
import sn.travel.travel_service.data.entities.Destination;
import sn.travel.travel_service.data.entities.Travel;
import sn.travel.travel_service.data.enums.TravelStatus;
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

        return travelMapper.toResponse(publishedTravel);
    }

    @Override
    public TravelResponse cancelTravel(UUID travelId, UUID managerId) {
        Travel travel = findTravelOrThrow(travelId);
        verifyTravelOwnership(travel, managerId);

        travel.setStatus(TravelStatus.CANCELLED);
        Travel cancelledTravel = travelRepository.save(travel);
        log.info("Travel {} cancelled by manager {}", travelId, managerId);

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
}
