package sn.travel.travel_service.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a subscription cannot be cancelled (e.g., too late).
 */
public class SubscriptionCancellationException extends TravelServiceException {

    private static final String ERROR_CODE = "TRAVEL_008";

    public SubscriptionCancellationException(String message) {
        super(
                message,
                ERROR_CODE,
                HttpStatus.CONFLICT
        );
    }
}
