package sn.travel.notification_service.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Thrown when an email sending operation fails.
 */
public class EmailSendingException extends NotificationServiceException {
    private static final String ERROR_CODE = "NOTIFICATION_002";

    public EmailSendingException(String message) {
        super(message, ERROR_CODE, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public EmailSendingException(String message, Throwable cause) {
        super(message, ERROR_CODE, HttpStatus.INTERNAL_SERVER_ERROR, cause);
    }
}
