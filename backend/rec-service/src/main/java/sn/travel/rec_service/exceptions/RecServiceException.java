package sn.travel.rec_service.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Classe de base pour toutes les exceptions metier du rec-service.
 */
@Getter
public abstract class RecServiceException extends RuntimeException {

    private final String errorCode;
    private final HttpStatus httpStatus;

    protected RecServiceException(String message, String errorCode, HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    protected RecServiceException(String message, String errorCode, HttpStatus httpStatus, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }
}
