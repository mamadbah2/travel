package sn.travel.travel_service.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when the travel has reached its maximum capacity.
 */
public class TravelFullException extends TravelServiceException {

    private static final String ERROR_CODE = "TRAVEL_004";

    public TravelFullException(String travelTitle) {
        super(
                String.format("Travel '%s' is fully booked. No more spots available.", travelTitle),
                ERROR_CODE,
                HttpStatus.CONFLICT
        );
    }
}
