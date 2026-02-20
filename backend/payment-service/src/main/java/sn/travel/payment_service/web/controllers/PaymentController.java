package sn.travel.payment_service.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import sn.travel.payment_service.web.dto.responses.PageResponse;
import sn.travel.payment_service.web.dto.responses.PaymentResponse;

import java.util.UUID;

/**
 * Controller interface for payment consultation endpoints.
 * Payments are created asynchronously via RabbitMQ events, not via REST.
 */
@Tag(name = "Payments", description = "Payment consultation API (read-only, payments are event-driven)")
public interface PaymentController {

    @Operation(summary = "Get payment by ID", description = "Get a specific payment transaction by its ID")
    ResponseEntity<PaymentResponse> getPaymentById(UUID paymentId);

    @Operation(summary = "Get payment by subscription ID", description = "Get the payment associated with a subscription")
    ResponseEntity<PaymentResponse> getPaymentBySubscriptionId(UUID subscriptionId);

    @Operation(summary = "Get payments by traveler", description = "Get all payments for a specific traveler")
    ResponseEntity<PageResponse<PaymentResponse>> getPaymentsByTraveler(UUID travelerId, Pageable pageable);

    @Operation(summary = "Get payments by travel", description = "Get all payments for a specific travel offer")
    ResponseEntity<PageResponse<PaymentResponse>> getPaymentsByTravel(UUID travelId, Pageable pageable);

    @Operation(summary = "Get all payments", description = "Get all payment transactions (Admin)")
    ResponseEntity<PageResponse<PaymentResponse>> getAllPayments(Pageable pageable);
}
