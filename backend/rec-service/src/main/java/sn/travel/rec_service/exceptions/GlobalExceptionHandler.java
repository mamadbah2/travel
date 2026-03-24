package sn.travel.rec_service.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Gestionnaire global des exceptions pour le rec-service.
 * Utilise RFC 7807 Problem Detail pour les reponses d'erreur.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String BASE_URI = "https://travel.sn/errors/";

    @ExceptionHandler(RecServiceException.class)
    public ProblemDetail handleRecServiceException(RecServiceException ex) {
        log.error("Rec service exception: {} - {}", ex.getErrorCode(), ex.getMessage());

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
                "La validation a echoue pour un ou plusieurs champs"
        );
        problemDetail.setType(URI.create(BASE_URI + "validation-error"));
        problemDetail.setTitle("Validation Error");
        problemDetail.setProperty("errorCode", "REC_400");
        problemDetail.setProperty("errors", errors);
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDeniedException(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN,
                "Acces refuse : permissions insuffisantes"
        );
        problemDetail.setType(URI.create(BASE_URI + "access-denied"));
        problemDetail.setTitle("Access Denied");
        problemDetail.setProperty("errorCode", "REC_403");
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex) {
        log.error("Unexpected error occurred", ex);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Une erreur inattendue s'est produite. Veuillez reessayer plus tard."
        );
        problemDetail.setType(URI.create(BASE_URI + "internal-error"));
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setProperty("errorCode", "REC_500");
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }
}
