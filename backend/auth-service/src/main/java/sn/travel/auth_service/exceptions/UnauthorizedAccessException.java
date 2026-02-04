package sn.travel.auth_service.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when access to a resource is denied.
 */
public class UnauthorizedAccessException extends AuthServiceException {

    private static final String ERROR_CODE = "AUTH_006";

    public UnauthorizedAccessException() {
        super(
                "Access denied: insufficient permissions",
                ERROR_CODE,
                HttpStatus.FORBIDDEN
        );
    }

    public UnauthorizedAccessException(String message) {
        super(message, ERROR_CODE, HttpStatus.FORBIDDEN);
    }
}
