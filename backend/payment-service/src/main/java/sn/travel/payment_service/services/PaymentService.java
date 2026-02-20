package sn.travel.payment_service.services;

import org.springframework.data.domain.Pageable;
import sn.travel.payment_service.data.records.SubscriptionCreatedEvent;
import sn.travel.payment_service.web.dto.responses.PageResponse;
import sn.travel.payment_service.web.dto.responses.PaymentResponse;

import java.util.UUID;

/**
 * Service interface for payment processing.
 */
public interface PaymentService {

    /**
     * Process a payment for a subscription event from travel-service.
     * Simulates bank latency, validates amount, and publishes result back.
     */
    PaymentResponse processPayment(SubscriptionCreatedEvent event);

    /**
     * Get a payment by its ID.
     */
    PaymentResponse getPaymentById(UUID paymentId);

    /**
     * Get a payment by subscription ID.
     */
    PaymentResponse getPaymentBySubscriptionId(UUID subscriptionId);

    /**
     * Get all payments for a specific traveler.
     */
    PageResponse<PaymentResponse> getPaymentsByTraveler(UUID travelerId, Pageable pageable);

    /**
     * Get all payments for a specific travel.
     */
    PageResponse<PaymentResponse> getPaymentsByTravel(UUID travelId, Pageable pageable);

    /**
     * Get all payments (Admin).
     */
    PageResponse<PaymentResponse> getAllPayments(Pageable pageable);
}
