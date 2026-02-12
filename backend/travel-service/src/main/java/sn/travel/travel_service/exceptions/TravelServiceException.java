package sn.travel.travel_service.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Base exception class for all travel-service business exceptions.
 */
@Getter
public abstract class TravelServiceException extends RuntimeException {

    private final String errorCode;
    private final HttpStatus httpStatus;

    protected TravelServiceException(String message, String errorCode, HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    protected TravelServiceException(String message, String errorCode, HttpStatus httpStatus, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }
}
