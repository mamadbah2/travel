package sn.travel.auth_service.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Base exception class for all auth-service business exceptions.
 */
@Getter
public abstract class AuthServiceException extends RuntimeException {

    private final String errorCode;
    private final HttpStatus httpStatus;

    protected AuthServiceException(String message, String errorCode, HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    protected AuthServiceException(String message, String errorCode, HttpStatus httpStatus, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }
}
