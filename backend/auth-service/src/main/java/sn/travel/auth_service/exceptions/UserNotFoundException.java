package sn.travel.auth_service.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a requested user is not found.
 */
public class UserNotFoundException extends AuthServiceException {

    private static final String ERROR_CODE = "AUTH_001";

    public UserNotFoundException(String identifier) {
        super(
                String.format("User not found with identifier: %s", identifier),
                ERROR_CODE,
                HttpStatus.NOT_FOUND
        );
    }
}
