package sn.travel.travel_service.services;

import org.springframework.data.domain.Pageable;
import sn.travel.travel_service.web.dto.responses.PageResponse;
import sn.travel.travel_service.web.dto.responses.SubscriptionResponse;

import java.util.UUID;

/**
 * Service interface for managing subscriptions (traveler inscriptions to travels).
 */
public interface SubscriptionService {

    /**
     * Subscribe a traveler to a travel (Traveler only).
     * Enforces 3-day rule and capacity checks with optimistic locking.
     */
    SubscriptionResponse subscribeToTravel(UUID travelId, UUID travelerId);

    /**
     * Cancel a subscription (Traveler - own subscriptions only, respecting 3-day rule).
     */
    SubscriptionResponse cancelSubscription(UUID subscriptionId, UUID travelerId);

    /**
     * Get a subscription by ID.
     */
    SubscriptionResponse getSubscriptionById(UUID subscriptionId, UUID travelerId);

    /**
     * Get all subscriptions for a traveler.
     */
    PageResponse<SubscriptionResponse> getSubscriptionsByTraveler(UUID travelerId, Pageable pageable);

    /**
     * Get all subscribers for a specific travel (Manager/Admin).
     */
    PageResponse<SubscriptionResponse> getSubscribersByTravel(UUID travelId, Pageable pageable);

    /**
     * Remove a subscriber from a travel (Manager - own travels, or Admin).
     */
    void removeSubscriber(UUID travelId, UUID subscriptionId, UUID managerId, String role);

    /**
     * Handle payment result event from payment-service via RabbitMQ.
     */
    void handlePaymentResult(UUID subscriptionId, boolean success);
}
