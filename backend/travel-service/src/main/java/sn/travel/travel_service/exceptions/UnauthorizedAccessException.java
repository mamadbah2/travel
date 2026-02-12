package sn.travel.travel_service.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a user tries to access or modify a resource they don't own.
 */
public class UnauthorizedAccessException extends TravelServiceException {

    private static final String ERROR_CODE = "TRAVEL_006";

    public UnauthorizedAccessException(String message) {
        super(
                message,
                ERROR_CODE,
                HttpStatus.FORBIDDEN
        );
    }
}
