package sn.travel.travel_service.web.controllers.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.travel.travel_service.services.SubscriptionService;
import sn.travel.travel_service.web.controllers.SubscriptionController;
import sn.travel.travel_service.web.dto.responses.MessageResponse;
import sn.travel.travel_service.web.dto.responses.PageResponse;
import sn.travel.travel_service.web.dto.responses.SubscriptionResponse;

import java.security.Principal;
import java.util.UUID;

/**
 * REST Controller implementation for subscription management (Traveler).
 */
@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
public class SubscriptionControllerImpl implements SubscriptionController {

    private final SubscriptionService subscriptionService;

    @Override
    @PostMapping("/travel/{travelId}")
    public ResponseEntity<SubscriptionResponse> subscribeToTravel(
            @PathVariable UUID travelId,
            Principal principal) {
        UUID travelerId = extractUserId(principal);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(subscriptionService.subscribeToTravel(travelId, travelerId));
    }

    @Override
    @PostMapping("/{subscriptionId}/cancel")
    public ResponseEntity<SubscriptionResponse> cancelSubscription(
            @PathVariable UUID subscriptionId,
            Principal principal) {
        UUID travelerId = extractUserId(principal);
        return ResponseEntity.ok(subscriptionService.cancelSubscription(subscriptionId, travelerId));
    }

    @Override
    @GetMapping("/{subscriptionId}")
    public ResponseEntity<SubscriptionResponse> getSubscriptionById(
            @PathVariable UUID subscriptionId,
            Principal principal) {
        UUID travelerId = extractUserId(principal);
        return ResponseEntity.ok(subscriptionService.getSubscriptionById(subscriptionId, travelerId));
    }

    @Override
    @GetMapping
    public ResponseEntity<PageResponse<SubscriptionResponse>> getMySubscriptions(
            Principal principal,
            @PageableDefault(size = 20) Pageable pageable) {
        UUID travelerId = extractUserId(principal);
        return ResponseEntity.ok(subscriptionService.getSubscriptionsByTraveler(travelerId, pageable));
    }

    // ---- Helper ----

    private UUID extractUserId(Principal principal) {
        return UUID.fromString(principal.getName());
    }
}
