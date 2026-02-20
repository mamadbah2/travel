package sn.travel.payment_service.web.dto.responses;

import sn.travel.payment_service.data.enums.PaymentMethod;
import sn.travel.payment_service.data.enums.PaymentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO response for a payment transaction.
 */
public record PaymentResponse(
        UUID id,
        UUID subscriptionId,
        UUID travelId,
        UUID travelerId,
        String travelTitle,
        Double amount,
        String currency,
        PaymentMethod method,
        String transactionId,
        PaymentStatus status,
        String failureReason,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
