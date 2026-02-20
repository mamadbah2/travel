package sn.travel.notification_service.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Thrown when a notification is not found by its ID.
 */
public class NotificationNotFoundException extends NotificationServiceException {
    private static final String ERROR_CODE = "NOTIFICATION_001";

    public NotificationNotFoundException(String identifier) {
        super(
                String.format("Notification not found with identifier: %s", identifier),
                ERROR_CODE,
                HttpStatus.NOT_FOUND
        );
    }
}
