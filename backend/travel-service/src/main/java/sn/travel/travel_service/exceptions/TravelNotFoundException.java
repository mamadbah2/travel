package sn.travel.travel_service.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a requested travel is not found.
 */
public class TravelNotFoundException extends TravelServiceException {

    private static final String ERROR_CODE = "TRAVEL_001";

    public TravelNotFoundException(String identifier) {
        super(
                String.format("Travel not found with identifier: %s", identifier),
                ERROR_CODE,
                HttpStatus.NOT_FOUND
        );
    }
}
