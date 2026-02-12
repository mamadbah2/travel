package sn.travel.travel_service.services;

import org.springframework.data.domain.Pageable;
import sn.travel.travel_service.web.dto.requests.CreateTravelRequest;
import sn.travel.travel_service.web.dto.requests.UpdateTravelRequest;
import sn.travel.travel_service.web.dto.responses.PageResponse;
import sn.travel.travel_service.web.dto.responses.TravelResponse;

import java.util.UUID;

/**
 * Service interface for managing travel offers.
 */
public interface TravelService {

    /**
     * Create a new travel offer (Manager only).
     */
    TravelResponse createTravel(CreateTravelRequest request, UUID managerId);

    /**
     * Update an existing travel offer (Manager - own travels only).
     */
    TravelResponse updateTravel(UUID travelId, UpdateTravelRequest request, UUID managerId);

    /**
     * Get a travel by ID (public).
     */
    TravelResponse getTravelById(UUID travelId);

    /**
     * Get all published travels available for subscription (public).
     */
    PageResponse<TravelResponse> getAvailableTravels(Pageable pageable);

    /**
     * Search published travels by keyword (public).
     */
    PageResponse<TravelResponse> searchTravels(String search, Pageable pageable);

    /**
     * Get all travels by a specific manager (Manager only).
     */
    PageResponse<TravelResponse> getTravelsByManager(UUID managerId, Pageable pageable);

    /**
     * Delete a travel (Manager - own travels, or Admin).
     */
    void deleteTravel(UUID travelId, UUID userId, String role);

    /**
     * Publish a draft travel (Manager - own travels only).
     */
    TravelResponse publishTravel(UUID travelId, UUID managerId);

    /**
     * Cancel a travel (Manager - own travels only).
     */
    TravelResponse cancelTravel(UUID travelId, UUID managerId);
}
