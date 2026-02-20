package sn.travel.payment_service.web.mappers;

import org.springframework.stereotype.Component;
import org.springframework.data.domain.Page;
import sn.travel.payment_service.data.entities.Payment;
import sn.travel.payment_service.web.dto.responses.PageResponse;
import sn.travel.payment_service.web.dto.responses.PaymentResponse;

import java.util.List;
import java.util.Collections;

/**
 * Manual mapper implementation for Payment entity <-> PaymentResponse DTO.
 * Replaces MapStruct to avoid annotation processing issues.
 */
@Component
public class PaymentMapper {

    public PaymentResponse toResponse(Payment payment) {
        if (payment == null) {
            return null;
        }

        return new PaymentResponse(
            payment.getId(),
            payment.getSubscriptionId(),
            payment.getTravelId(),
            payment.getTravelerId(),
            payment.getTravelTitle(),
            payment.getAmount(),
            payment.getCurrency(),
            payment.getMethod(),
            payment.getTransactionId(),
            payment.getStatus(),
            payment.getFailureReason(),
            payment.getCreatedAt(),
            payment.getUpdatedAt()
        );
    }

    public List<PaymentResponse> toResponseList(List<Payment> payments) {
        if (payments == null) {
            return Collections.emptyList();
        }
        return payments.stream()
                .map(this::toResponse)
                .toList();
    }

    public PageResponse<PaymentResponse> toPageResponse(Page<Payment> page) {
        if (page == null) {
            return null;
        }
        return new PageResponse<>(
                toResponseList(page.getContent()),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }
}
