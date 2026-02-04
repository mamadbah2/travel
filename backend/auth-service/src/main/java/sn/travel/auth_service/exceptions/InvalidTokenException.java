package sn.travel.auth_service.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a JWT token is invalid or expired.
 */
public class InvalidTokenException extends AuthServiceException {

    private static final String ERROR_CODE = "AUTH_004";

    public InvalidTokenException(String message) {
        super(message, ERROR_CODE, HttpStatus.UNAUTHORIZED);
    }

    public InvalidTokenException() {
        super("Invalid or expired token", ERROR_CODE, HttpStatus.UNAUTHORIZED);
    }
}
