package sn.travel.travel_service.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a traveler tries to subscribe but the 3-day rule is violated.
 * A traveler cannot subscribe if (travel.startDate - now) < 3 days.
 */
public class SubscriptionTooLateException extends TravelServiceException {

    private static final String ERROR_CODE = "TRAVEL_003";

    public SubscriptionTooLateException(String travelTitle) {
        super(
                String.format("Cannot subscribe to travel '%s': subscription deadline has passed (must be at least 3 days before departure)", travelTitle),
                ERROR_CODE,
                HttpStatus.CONFLICT
        );
    }
}
