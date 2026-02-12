package sn.travel.travel_service.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when an optimistic locking conflict occurs (concurrent booking).
 */
public class ConcurrentBookingException extends TravelServiceException {

    private static final String ERROR_CODE = "TRAVEL_007";

    public ConcurrentBookingException(String travelTitle) {
        super(
                String.format("Concurrent booking conflict for travel '%s'. Please try again.", travelTitle),
                ERROR_CODE,
                HttpStatus.CONFLICT
        );
    }
}
