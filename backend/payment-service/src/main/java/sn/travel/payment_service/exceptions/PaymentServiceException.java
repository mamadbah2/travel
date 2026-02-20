package sn.travel.payment_service.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Base exception for all payment-service business exceptions.
 * Provides a consistent structure: message, errorCode, httpStatus.
 */
@Getter
public abstract class PaymentServiceException extends RuntimeException {

    private final String errorCode;
    private final HttpStatus httpStatus;

    protected PaymentServiceException(String message, String errorCode, HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    protected PaymentServiceException(String message, String errorCode, HttpStatus httpStatus, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }
}
