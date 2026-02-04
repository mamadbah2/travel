package sn.travel.auth_service.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a user with the same email already exists.
 */
public class UserAlreadyExistsException extends AuthServiceException {

    private static final String ERROR_CODE = "AUTH_002";

    public UserAlreadyExistsException(String email) {
        super(
                String.format("User with email '%s' already exists", email),
                ERROR_CODE,
                HttpStatus.CONFLICT
        );
    }
}
