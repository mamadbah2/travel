package sn.travel.travel_service.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a requested subscription is not found.
 */
public class SubscriptionNotFoundException extends TravelServiceException {

    private static final String ERROR_CODE = "TRAVEL_002";

    public SubscriptionNotFoundException(String identifier) {
        super(
                String.format("Subscription not found with identifier: %s", identifier),
                ERROR_CODE,
                HttpStatus.NOT_FOUND
        );
    }
}
