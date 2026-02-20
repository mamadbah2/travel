package sn.travel.payment_service.services.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.travel.payment_service.config.RabbitMQConfig;
import sn.travel.payment_service.data.entities.Payment;
import sn.travel.payment_service.data.enums.PaymentMethod;
import sn.travel.payment_service.data.enums.PaymentStatus;
import sn.travel.payment_service.data.records.PaymentCompletedEvent;
import sn.travel.payment_service.data.records.SubscriptionCreatedEvent;
import sn.travel.payment_service.data.repositories.PaymentRepository;
import sn.travel.payment_service.exceptions.DuplicatePaymentException;
import sn.travel.payment_service.exceptions.PaymentNotFoundException;
import sn.travel.payment_service.exceptions.PaymentProcessingException;
import sn.travel.payment_service.services.PaymentService;
import sn.travel.payment_service.web.dto.responses.PageResponse;
import sn.travel.payment_service.web.dto.responses.PaymentResponse;
import sn.travel.payment_service.web.mappers.PaymentMapper;

import java.util.UUID;

/**
 * Implementation of PaymentService.
 * Simulates payment processing with configurable delay.
 * <p>
 * Flow:
 * 1. Receive SubscriptionCreatedEvent
 * 2. Create Payment entity (PENDING)
 * 3. Simulate bank latency (Thread.sleep)
 * 4. Validate: amount > 0 → SUCCESS, otherwise → FAILED
 * 5. Save and publish PaymentCompletedEvent back to travel-service
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final RabbitTemplate rabbitTemplate;

    @Value("${payment.simulation.processing-delay-ms:2000}")
    private long processingDelayMs;

    @Override
    public PaymentResponse processPayment(SubscriptionCreatedEvent event) {
        log.info("Processing payment for subscription {} (travel: {}, amount: {} {})",
                event.subscriptionId(), event.travelTitle(), event.amount(), event.currency());

        // Guard: prevent duplicate payment for same subscription
        if (paymentRepository.existsBySubscriptionId(event.subscriptionId())) {
            throw new DuplicatePaymentException(event.subscriptionId().toString());
        }

        // Step 1: Create Payment entity (PENDING)
        Payment payment = Payment.builder()
                .subscriptionId(event.subscriptionId())
                .travelId(event.travelId())
                .travelerId(event.travelerId())
                .travelTitle(event.travelTitle())
                .amount(event.amount())
                .currency(event.currency() != null ? event.currency() : "XOF")
                .method(PaymentMethod.SIMULATED)
                .status(PaymentStatus.PENDING)
                .build();

        payment = paymentRepository.save(payment);
        log.info("Payment created: {} (PENDING)", payment.getId());

        // Step 2: Simulate bank processing latency
        simulateBankLatency();

        // Step 3: Validate amount and determine result
        String transactionId = "SIM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String failureReason = null;

        if (event.amount() != null && event.amount() > 0) {
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setTransactionId(transactionId);
            log.info("Payment {} succeeded (txn: {})", payment.getId(), transactionId);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            failureReason = "Invalid amount: amount must be greater than 0";
            payment.setFailureReason(failureReason);
            log.warn("Payment {} failed: {}", payment.getId(), failureReason);
        }

        // Step 4: Save final state
        payment = paymentRepository.save(payment);

        // Step 5: Publish PaymentCompletedEvent back to travel-service
        publishPaymentCompletedEvent(payment, failureReason);

        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId.toString()));
        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentBySubscriptionId(UUID subscriptionId) {
        Payment payment = paymentRepository.findBySubscriptionId(subscriptionId)
                .orElseThrow(() -> new PaymentNotFoundException("subscription:" + subscriptionId));
        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PaymentResponse> getPaymentsByTraveler(UUID travelerId, Pageable pageable) {
        return paymentMapper.toPageResponse(paymentRepository.findByTravelerId(travelerId, pageable));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PaymentResponse> getPaymentsByTravel(UUID travelId, Pageable pageable) {
        return paymentMapper.toPageResponse(paymentRepository.findByTravelId(travelId, pageable));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PaymentResponse> getAllPayments(Pageable pageable) {
        return paymentMapper.toPageResponse(paymentRepository.findAll(pageable));
    }

    // ---- Private helpers ----

    /**
     * Simulates bank processing latency.
     */
    private void simulateBankLatency() {
        try {
            log.debug("Simulating bank processing delay ({}ms)...", processingDelayMs);
            Thread.sleep(processingDelayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PaymentProcessingException("Payment processing was interrupted", e);
        }
    }

    /**
     * Publishes a PaymentCompletedEvent to RabbitMQ for the travel-service to consume.
     */
    private void publishPaymentCompletedEvent(Payment payment, String failureReason) {
        String routingKey = (payment.getStatus() == PaymentStatus.SUCCESS)
                ? RabbitMQConfig.PAYMENT_SUCCESS_KEY
                : RabbitMQConfig.PAYMENT_FAILED_KEY;

        PaymentCompletedEvent event = new PaymentCompletedEvent(
                payment.getSubscriptionId(),
                payment.getTravelId(),
                payment.getTravelerId(),
                payment.getStatus().name(),
                payment.getTransactionId(),
                failureReason
        );

        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.PAYMENT_EXCHANGE, routingKey, event);
            log.info("Published PaymentCompletedEvent [{}] for subscription {} (routing: {})",
                    payment.getStatus(), payment.getSubscriptionId(), routingKey);
        } catch (Exception e) {
            log.error("Failed to publish PaymentCompletedEvent for subscription {}: {}",
                    payment.getSubscriptionId(), e.getMessage());
        }
    }
}
