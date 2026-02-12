package sn.travel.travel_service.services.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.travel.travel_service.config.RabbitMQConfig;
import sn.travel.travel_service.data.entities.Subscription;
import sn.travel.travel_service.data.entities.Travel;
import sn.travel.travel_service.data.enums.SubscriptionStatus;
import sn.travel.travel_service.data.enums.TravelStatus;
import sn.travel.travel_service.data.records.SubscriptionCreatedEvent;
import sn.travel.travel_service.data.repositories.SubscriptionRepository;
import sn.travel.travel_service.data.repositories.TravelRepository;
import sn.travel.travel_service.exceptions.*;
import sn.travel.travel_service.services.SubscriptionService;
import sn.travel.travel_service.web.dto.responses.PageResponse;
import sn.travel.travel_service.web.dto.responses.SubscriptionResponse;
import sn.travel.travel_service.web.mappers.SubscriptionMapper;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * Implementation of SubscriptionService.
 * Handles the subscription lifecycle with concurrency control (optimistic locking)
 * and the 3-day business rule.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SubscriptionServiceImpl implements SubscriptionService {

    private static final int MINIMUM_DAYS_BEFORE_DEPARTURE = 3;

    private final SubscriptionRepository subscriptionRepository;
    private final TravelRepository travelRepository;
    private final SubscriptionMapper subscriptionMapper;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public SubscriptionResponse subscribeToTravel(UUID travelId, UUID travelerId) {
        log.info("Traveler {} subscribing to travel {}", travelerId, travelId);

        Travel travel = findTravelOrThrow(travelId);

        // Validate travel is published
        if (travel.getStatus() != TravelStatus.PUBLISHED) {
            throw new UnauthorizedAccessException("Cannot subscribe to a travel that is not published");
        }

        // Enforce 3-day rule
        enforceThreeDayRule(travel);

        // Check for existing active subscription
        boolean hasActiveSubscription = subscriptionRepository
                .existsByTravelerIdAndTravelIdAndStatusNot(travelerId, travelId, SubscriptionStatus.CANCELLED);
        if (hasActiveSubscription) {
            throw new DuplicateSubscriptionException(travel.getTitle());
        }

        // Check capacity and increment with optimistic locking
        if (!travel.hasAvailableCapacity()) {
            throw new TravelFullException(travel.getTitle());
        }

        try {
            travel.incrementBookings();
            travelRepository.save(travel);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new ConcurrentBookingException(travel.getTitle());
        }

        // Create subscription with PENDING_PAYMENT status
        Subscription subscription = Subscription.builder()
                .travelerId(travelerId)
                .travel(travel)
                .status(SubscriptionStatus.PENDING_PAYMENT)
                .build();

        Subscription savedSubscription = subscriptionRepository.save(subscription);
        log.info("Subscription created: {} (PENDING_PAYMENT)", savedSubscription.getId());

        // Publish event to payment-service via RabbitMQ
        publishSubscriptionCreatedEvent(savedSubscription, travel);

        return subscriptionMapper.toResponse(savedSubscription);
    }

    @Override
    public SubscriptionResponse cancelSubscription(UUID subscriptionId, UUID travelerId) {
        Subscription subscription = findSubscriptionOrThrow(subscriptionId);

        // Verify ownership
        if (!subscription.getTravelerId().equals(travelerId)) {
            throw new UnauthorizedAccessException("You are not the owner of this subscription");
        }

        // Cannot cancel an already cancelled subscription
        if (subscription.getStatus() == SubscriptionStatus.CANCELLED) {
            throw new SubscriptionCancellationException("This subscription is already cancelled");
        }

        // Enforce 3-day rule for cancellation
        enforceThreeDayRule(subscription.getTravel());

        // Cancel subscription and release the spot
        subscription.setStatus(SubscriptionStatus.CANCELLED);
        subscription.getTravel().decrementBookings();

        travelRepository.save(subscription.getTravel());
        Subscription cancelledSubscription = subscriptionRepository.save(subscription);

        log.info("Subscription {} cancelled by traveler {}", subscriptionId, travelerId);

        return subscriptionMapper.toResponse(cancelledSubscription);
    }

    @Override
    @Transactional(readOnly = true)
    public SubscriptionResponse getSubscriptionById(UUID subscriptionId, UUID travelerId) {
        Subscription subscription = findSubscriptionOrThrow(subscriptionId);

        if (!subscription.getTravelerId().equals(travelerId)) {
            throw new UnauthorizedAccessException("You are not the owner of this subscription");
        }

        return subscriptionMapper.toResponse(subscription);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<SubscriptionResponse> getSubscriptionsByTraveler(UUID travelerId, Pageable pageable) {
        Page<Subscription> subscriptions = subscriptionRepository.findByTravelerId(travelerId, pageable);
        return subscriptionMapper.toPageResponse(subscriptions);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<SubscriptionResponse> getSubscribersByTravel(UUID travelId, Pageable pageable) {
        Page<Subscription> subscriptions = subscriptionRepository.findByTravelId(travelId, pageable);
        return subscriptionMapper.toPageResponse(subscriptions);
    }

    @Override
    public void removeSubscriber(UUID travelId, UUID subscriptionId, UUID managerId, String role) {
        Travel travel = findTravelOrThrow(travelId);

        if (!"ADMIN".equals(role) && !travel.getManagerId().equals(managerId)) {
            throw new UnauthorizedAccessException("You are not the owner of this travel");
        }

        Subscription subscription = findSubscriptionOrThrow(subscriptionId);

        if (!subscription.getTravel().getId().equals(travelId)) {
            throw new SubscriptionNotFoundException(subscriptionId.toString());
        }

        if (subscription.getStatus() != SubscriptionStatus.CANCELLED) {
            subscription.setStatus(SubscriptionStatus.CANCELLED);
            travel.decrementBookings();
            travelRepository.save(travel);
        }

        subscriptionRepository.save(subscription);
        log.info("Subscriber {} removed from travel {} by {}", subscriptionId, travelId, managerId);
    }

    @Override
    public void handlePaymentResult(UUID subscriptionId, boolean success) {
        Subscription subscription = findSubscriptionOrThrow(subscriptionId);

        if (subscription.getStatus() != SubscriptionStatus.PENDING_PAYMENT) {
            log.warn("Payment result received for subscription {} but status is {}",
                    subscriptionId, subscription.getStatus());
            return;
        }

        if (success) {
            subscription.setStatus(SubscriptionStatus.CONFIRMED);
            log.info("Subscription {} confirmed after successful payment", subscriptionId);
        } else {
            subscription.setStatus(SubscriptionStatus.CANCELLED);
            subscription.getTravel().decrementBookings();
            travelRepository.save(subscription.getTravel());
            log.info("Subscription {} cancelled after failed payment", subscriptionId);
        }

        subscriptionRepository.save(subscription);
    }

    // ---- Private helpers ----

    private Travel findTravelOrThrow(UUID travelId) {
        return travelRepository.findById(travelId)
                .orElseThrow(() -> new TravelNotFoundException(travelId.toString()));
    }

    private Subscription findSubscriptionOrThrow(UUID subscriptionId) {
        return subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new SubscriptionNotFoundException(subscriptionId.toString()));
    }

    /**
     * Enforces the 3-day rule: (travel.startDate - now) must be >= 3 days.
     */
    private void enforceThreeDayRule(Travel travel) {
        long daysUntilDeparture = ChronoUnit.DAYS.between(LocalDate.now(), travel.getStartDate());
        if (daysUntilDeparture < MINIMUM_DAYS_BEFORE_DEPARTURE) {
            throw new SubscriptionTooLateException(travel.getTitle());
        }
    }

    /**
     * Publishes a SubscriptionCreatedEvent to RabbitMQ for the payment-service.
     */
    private void publishSubscriptionCreatedEvent(Subscription subscription, Travel travel) {
        SubscriptionCreatedEvent event = new SubscriptionCreatedEvent(
                subscription.getId(),
                travel.getId(),
                subscription.getTravelerId(),
                travel.getTitle(),
                travel.getPrice(),
                "XOF"
        );

        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.SUBSCRIPTION_EXCHANGE,
                    RabbitMQConfig.SUBSCRIPTION_CREATED_KEY,
                    event
            );
            log.info("Published SubscriptionCreatedEvent for subscription {}", subscription.getId());
        } catch (Exception e) {
            log.error("Failed to publish SubscriptionCreatedEvent for subscription {}: {}",
                    subscription.getId(), e.getMessage());
        }
    }
}
