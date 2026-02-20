package sn.travel.payment_service.web.controllers.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.travel.payment_service.services.PaymentService;
import sn.travel.payment_service.web.controllers.PaymentController;
import sn.travel.payment_service.web.dto.responses.PageResponse;
import sn.travel.payment_service.web.dto.responses.PaymentResponse;

import java.util.UUID;

/**
 * REST Controller implementation for payment consultation.
 * Payments are created asynchronously via RabbitMQ, not through REST endpoints.
 * This controller provides read-only access to payment data.
 */
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentControllerImpl implements PaymentController {

    private final PaymentService paymentService;

    @Override
    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable UUID paymentId) {
        return ResponseEntity.ok(paymentService.getPaymentById(paymentId));
    }

    @Override
    @GetMapping("/subscription/{subscriptionId}")
    public ResponseEntity<PaymentResponse> getPaymentBySubscriptionId(@PathVariable UUID subscriptionId) {
        return ResponseEntity.ok(paymentService.getPaymentBySubscriptionId(subscriptionId));
    }

    @Override
    @GetMapping("/traveler/{travelerId}")
    public ResponseEntity<PageResponse<PaymentResponse>> getPaymentsByTraveler(
            @PathVariable UUID travelerId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(paymentService.getPaymentsByTraveler(travelerId, pageable));
    }

    @Override
    @GetMapping("/travel/{travelId}")
    public ResponseEntity<PageResponse<PaymentResponse>> getPaymentsByTravel(
            @PathVariable UUID travelId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(paymentService.getPaymentsByTravel(travelId, pageable));
    }

    @Override
    @GetMapping
    public ResponseEntity<PageResponse<PaymentResponse>> getAllPayments(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(paymentService.getAllPayments(pageable));
    }
}
