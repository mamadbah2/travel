package sn.travel.auth_service.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when authentication credentials are invalid.
 */
public class InvalidCredentialsException extends AuthServiceException {

    private static final String ERROR_CODE = "AUTH_003";

    public InvalidCredentialsException() {
        super(
                "Invalid email or password",
                ERROR_CODE,
                HttpStatus.UNAUTHORIZED
        );
    }

    public InvalidCredentialsException(String message) {
        super(message, ERROR_CODE, HttpStatus.UNAUTHORIZED);
    }
}
