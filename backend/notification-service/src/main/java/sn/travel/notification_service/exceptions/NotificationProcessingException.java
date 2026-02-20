package sn.travel.notification_service.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Thrown when notification processing fails unexpectedly.
 */
public class NotificationProcessingException extends NotificationServiceException {
    private static final String ERROR_CODE = "NOTIFICATION_003";

    public NotificationProcessingException(String message) {
        super(message, ERROR_CODE, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public NotificationProcessingException(String message, Throwable cause) {
        super(message, ERROR_CODE, HttpStatus.INTERNAL_SERVER_ERROR, cause);
    }
}
