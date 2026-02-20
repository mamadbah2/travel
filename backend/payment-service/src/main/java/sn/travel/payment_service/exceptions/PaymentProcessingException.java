package sn.travel.payment_service.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Thrown when payment processing fails (simulation or real gateway).
 */
public class PaymentProcessingException extends PaymentServiceException {
    private static final String ERROR_CODE = "PAYMENT_003";

    public PaymentProcessingException(String message) {
        super(message, ERROR_CODE, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public PaymentProcessingException(String message, Throwable cause) {
        super(message, ERROR_CODE, HttpStatus.INTERNAL_SERVER_ERROR, cause);
    }
}
