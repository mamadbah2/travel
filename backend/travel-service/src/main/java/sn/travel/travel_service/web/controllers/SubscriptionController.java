package sn.travel.travel_service.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import sn.travel.travel_service.web.dto.responses.MessageResponse;
import sn.travel.travel_service.web.dto.responses.PageResponse;
import sn.travel.travel_service.web.dto.responses.SubscriptionResponse;

import java.security.Principal;
import java.util.UUID;

/**
 * Controller interface for subscription management endpoints (Traveler).
 */
@Tag(name = "Subscriptions", description = "Traveler subscription management API")
public interface SubscriptionController {

    @Operation(summary = "Subscribe to a travel", description = "Subscribe the authenticated traveler to a travel (enforces 3-day rule and capacity)")
    ResponseEntity<SubscriptionResponse> subscribeToTravel(UUID travelId, Principal principal);

    @Operation(summary = "Cancel a subscription", description = "Cancel an existing subscription (respects 3-day rule)")
    ResponseEntity<SubscriptionResponse> cancelSubscription(UUID subscriptionId, Principal principal);

    @Operation(summary = "Get subscription by ID", description = "Get a specific subscription by its ID")
    ResponseEntity<SubscriptionResponse> getSubscriptionById(UUID subscriptionId, Principal principal);

    @Operation(summary = "Get my subscriptions", description = "Get all subscriptions for the authenticated traveler")
    ResponseEntity<PageResponse<SubscriptionResponse>> getMySubscriptions(Principal principal, Pageable pageable);
}
