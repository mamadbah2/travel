package sn.travel.auth_service.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the auth-service.
 * Uses RFC 7807 Problem Detail for error responses.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String BASE_URI = "https://travel.sn/errors/";

    @ExceptionHandler(AuthServiceException.class)
    public ProblemDetail handleAuthServiceException(AuthServiceException ex) {
        log.error("Auth service exception: {} - {}", ex.getErrorCode(), ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                ex.getHttpStatus(),
                ex.getMessage()
        );
        problemDetail.setType(URI.create(BASE_URI + ex.getErrorCode().toLowerCase()));
        problemDetail.setTitle(ex.getClass().getSimpleName().replace("Exception", " Error"));
        problemDetail.setProperty("errorCode", ex.getErrorCode());
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(MethodArgumentNotValidException ex) {
        log.warn("Validation error: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Validation failed for one or more fields"
        );
        problemDetail.setType(URI.create(BASE_URI + "validation-error"));
        problemDetail.setTitle("Validation Error");
        problemDetail.setProperty("errorCode", "AUTH_400");
        problemDetail.setProperty("errors", errors);
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail handleBadCredentialsException(BadCredentialsException ex) {
        log.warn("Bad credentials attempt");

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED,
                "Invalid email or password"
        );
        problemDetail.setType(URI.create(BASE_URI + "invalid-credentials"));
        problemDetail.setTitle("Invalid Credentials");
        problemDetail.setProperty("errorCode", "AUTH_003");
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDeniedException(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN,
                "Access denied: insufficient permissions"
        );
        problemDetail.setType(URI.create(BASE_URI + "access-denied"));
        problemDetail.setTitle("Access Denied");
        problemDetail.setProperty("errorCode", "AUTH_006");
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex) {
        log.error("Unexpected error occurred", ex);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred. Please try again later."
        );
        problemDetail.setType(URI.create(BASE_URI + "internal-error"));
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setProperty("errorCode", "AUTH_500");
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }
}
