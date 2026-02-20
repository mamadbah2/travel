package sn.travel.payment_service.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Thrown when a payment already exists for a given subscription.
 */
public class DuplicatePaymentException extends PaymentServiceException {
    private static final String ERROR_CODE = "PAYMENT_002";

    public DuplicatePaymentException(String subscriptionId) {
        super(
                String.format("A payment already exists for subscription: %s", subscriptionId),
                ERROR_CODE,
                HttpStatus.CONFLICT
        );
    }
}
