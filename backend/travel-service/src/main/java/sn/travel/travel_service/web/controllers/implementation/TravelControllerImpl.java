package sn.travel.travel_service.web.controllers.implementation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import sn.travel.travel_service.services.SubscriptionService;
import sn.travel.travel_service.services.TravelService;
import sn.travel.travel_service.web.controllers.TravelController;
import sn.travel.travel_service.web.dto.requests.CreateTravelRequest;
import sn.travel.travel_service.web.dto.requests.UpdateTravelRequest;
import sn.travel.travel_service.web.dto.responses.MessageResponse;
import sn.travel.travel_service.web.dto.responses.PageResponse;
import sn.travel.travel_service.web.dto.responses.SubscriptionResponse;
import sn.travel.travel_service.web.dto.responses.TravelResponse;

import java.security.Principal;
import java.util.UUID;

/**
 * REST Controller implementation for travel management.
 */
@RestController
@RequestMapping("/api/v1/travels")
@RequiredArgsConstructor
public class TravelControllerImpl implements TravelController {

    private final TravelService travelService;
    private final SubscriptionService subscriptionService;

    // ---- Public endpoints ----

    @Override
    @GetMapping
    public ResponseEntity<PageResponse<TravelResponse>> getAvailableTravels(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(travelService.getAvailableTravels(pageable));
    }

    @Override
    @GetMapping("/{travelId}")
    public ResponseEntity<TravelResponse> getTravelById(@PathVariable UUID travelId) {
        return ResponseEntity.ok(travelService.getTravelById(travelId));
    }

    @Override
    @GetMapping("/search")
    public ResponseEntity<PageResponse<TravelResponse>> searchTravels(
            @RequestParam String search,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(travelService.searchTravels(search, pageable));
    }

    // ---- Manager endpoints ----

    @Override
    @PostMapping
    public ResponseEntity<TravelResponse> createTravel(
            @RequestBody @Valid CreateTravelRequest request,
            Principal principal) {
        UUID managerId = extractUserId(principal);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(travelService.createTravel(request, managerId));
    }

    @Override
    @PutMapping("/{travelId}")
    public ResponseEntity<TravelResponse> updateTravel(
            @PathVariable UUID travelId,
            @RequestBody @Valid UpdateTravelRequest request,
            Principal principal) {
        UUID managerId = extractUserId(principal);
        return ResponseEntity.ok(travelService.updateTravel(travelId, request, managerId));
    }

    @Override
    @DeleteMapping("/{travelId}")
    public ResponseEntity<MessageResponse> deleteTravel(
            @PathVariable UUID travelId,
            Principal principal) {
        UUID userId = extractUserId(principal);
        String role = extractRole();
        travelService.deleteTravel(travelId, userId, role);
        return ResponseEntity.ok(new MessageResponse("Travel deleted successfully"));
    }

    @Override
    @PostMapping("/{travelId}/publish")
    public ResponseEntity<TravelResponse> publishTravel(
            @PathVariable UUID travelId,
            Principal principal) {
        UUID managerId = extractUserId(principal);
        return ResponseEntity.ok(travelService.publishTravel(travelId, managerId));
    }

    @Override
    @PostMapping("/{travelId}/cancel")
    public ResponseEntity<TravelResponse> cancelTravel(
            @PathVariable UUID travelId,
            Principal principal) {
        UUID managerId = extractUserId(principal);
        return ResponseEntity.ok(travelService.cancelTravel(travelId, managerId));
    }

    @Override
    @GetMapping("/manager/me")
    public ResponseEntity<PageResponse<TravelResponse>> getManagerTravels(
            Principal principal,
            @PageableDefault(size = 20) Pageable pageable) {
        UUID managerId = extractUserId(principal);
        return ResponseEntity.ok(travelService.getTravelsByManager(managerId, pageable));
    }

    // ---- Manager: subscriber management ----

    @Override
    @GetMapping("/{travelId}/subscribers")
    public ResponseEntity<PageResponse<SubscriptionResponse>> getTravelSubscribers(
            @PathVariable UUID travelId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(subscriptionService.getSubscribersByTravel(travelId, pageable));
    }

    @Override
    @DeleteMapping("/{travelId}/subscribers/{subscriptionId}")
    public ResponseEntity<MessageResponse> removeSubscriber(
            @PathVariable UUID travelId,
            @PathVariable UUID subscriptionId,
            Principal principal) {
        UUID userId = extractUserId(principal);
        String role = extractRole();
        subscriptionService.removeSubscriber(travelId, subscriptionId, userId, role);
        return ResponseEntity.ok(new MessageResponse("Subscriber removed successfully"));
    }

    // ---- Helper methods ----

    private UUID extractUserId(Principal principal) {
        return UUID.fromString(principal.getName());
    }

    private String extractRole() {
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> a.startsWith("ROLE_"))
                .map(a -> a.substring(5))
                .findFirst()
                .orElse("UNKNOWN");
    }
}
