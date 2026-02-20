package sn.travel.notification_service.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for notification-service.
 * Returns RFC 7807 ProblemDetail responses with consistent structure.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String BASE_URI = "https://travel.sn/errors/";

    @ExceptionHandler(NotificationServiceException.class)
    public ProblemDetail handleNotificationServiceException(NotificationServiceException ex) {
        log.error("Business exception: [{}] {}", ex.getErrorCode(), ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(ex.getHttpStatus(), ex.getMessage());
        problemDetail.setType(URI.create(BASE_URI + ex.getErrorCode().toLowerCase()));
        problemDetail.setTitle(ex.getClass().getSimpleName());
        problemDetail.setProperty("errorCode", ex.getErrorCode());
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(MethodArgumentNotValidException ex) {
        log.warn("Validation error: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = (error instanceof FieldError fieldError) ? fieldError.getField() : error.getObjectName();
            errors.put(fieldName, error.getDefaultMessage());
        });

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed");
        problemDetail.setType(URI.create(BASE_URI + "validation-error"));
        problemDetail.setTitle("Validation Error");
        problemDetail.setProperty("errorCode", "NOTIFICATION_VALIDATION");
        problemDetail.setProperty("errors", errors);
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex) {
        log.error("Unexpected error: ", ex);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred. Please try again later."
        );
        problemDetail.setType(URI.create(BASE_URI + "internal-error"));
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setProperty("errorCode", "NOTIFICATION_INTERNAL");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }
}
