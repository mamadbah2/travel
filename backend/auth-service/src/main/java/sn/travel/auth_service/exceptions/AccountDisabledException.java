package sn.travel.auth_service.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when user account is banned or inactive.
 */
public class AccountDisabledException extends AuthServiceException {

    private static final String ERROR_CODE = "AUTH_005";

    public AccountDisabledException() {
        super(
                "Account is disabled or banned",
                ERROR_CODE,
                HttpStatus.FORBIDDEN
        );
    }

    public AccountDisabledException(String message) {
        super(message, ERROR_CODE, HttpStatus.FORBIDDEN);
    }
}
