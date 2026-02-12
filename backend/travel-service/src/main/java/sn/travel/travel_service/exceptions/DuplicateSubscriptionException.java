package sn.travel.travel_service.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a traveler already has an active subscription for a travel.
 */
public class DuplicateSubscriptionException extends TravelServiceException {

    private static final String ERROR_CODE = "TRAVEL_005";

    public DuplicateSubscriptionException(String travelTitle) {
        super(
                String.format("You already have an active subscription for travel '%s'", travelTitle),
                ERROR_CODE,
                HttpStatus.CONFLICT
        );
    }
}
