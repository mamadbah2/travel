package sn.travel.travel_service.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import sn.travel.travel_service.web.dto.requests.CreateTravelRequest;
import sn.travel.travel_service.web.dto.requests.UpdateTravelRequest;
import sn.travel.travel_service.web.dto.responses.MessageResponse;
import sn.travel.travel_service.web.dto.responses.PageResponse;
import sn.travel.travel_service.web.dto.responses.SubscriptionResponse;
import sn.travel.travel_service.web.dto.responses.TravelResponse;

import java.security.Principal;
import java.util.UUID;

/**
 * Controller interface for travel management endpoints.
 */
@Tag(name = "Travels", description = "Travel offer management API")
public interface TravelController {

    @Operation(summary = "Get all available travels", description = "Get paginated list of published travels available for subscription (public)")
    ResponseEntity<PageResponse<TravelResponse>> getAvailableTravels(Pageable pageable);

    @Operation(summary = "Get travel by ID", description = "Get a specific travel by its ID (public)")
    ResponseEntity<TravelResponse> getTravelById(UUID travelId);

    @Operation(summary = "Search travels", description = "Search published travels by keyword (public)")
    ResponseEntity<PageResponse<TravelResponse>> searchTravels(String search, Pageable pageable);

    @Operation(summary = "Create a travel", description = "Create a new travel offer (Manager only)")
    ResponseEntity<TravelResponse> createTravel(@Valid CreateTravelRequest request, Principal principal);

    @Operation(summary = "Update a travel", description = "Update an existing travel offer (Manager - own travels only)")
    ResponseEntity<TravelResponse> updateTravel(UUID travelId, @Valid UpdateTravelRequest request, Principal principal);

    @Operation(summary = "Delete a travel", description = "Delete a travel and cascade-remove all subscriptions (Manager/Admin)")
    ResponseEntity<MessageResponse> deleteTravel(UUID travelId, Principal principal);

    @Operation(summary = "Publish a travel", description = "Publish a draft travel to make it available (Manager - own travels only)")
    ResponseEntity<TravelResponse> publishTravel(UUID travelId, Principal principal);

    @Operation(summary = "Cancel a travel", description = "Cancel a travel (Manager - own travels only)")
    ResponseEntity<TravelResponse> cancelTravel(UUID travelId, Principal principal);

    @Operation(summary = "Get manager's travels", description = "Get all travels for the authenticated manager")
    ResponseEntity<PageResponse<TravelResponse>> getManagerTravels(Principal principal, Pageable pageable);

    @Operation(summary = "Get travel subscribers", description = "Get paginated list of subscribers for a travel (Manager/Admin)")
    ResponseEntity<PageResponse<SubscriptionResponse>> getTravelSubscribers(UUID travelId, Pageable pageable);

    @Operation(summary = "Remove a subscriber", description = "Remove a subscriber from a travel (Manager/Admin)")
    ResponseEntity<MessageResponse> removeSubscriber(UUID travelId, UUID subscriptionId, Principal principal);
}
