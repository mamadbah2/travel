package sn.travel.payment_service.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Thrown when a payment is not found by its ID or subscription ID.
 */
public class PaymentNotFoundException extends PaymentServiceException {
    private static final String ERROR_CODE = "PAYMENT_001";

    public PaymentNotFoundException(String identifier) {
        super(
                String.format("Payment not found with identifier: %s", identifier),
                ERROR_CODE,
                HttpStatus.NOT_FOUND
        );
    }
}
