package sn.travel.search_service.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Base abstract exception for the search-service.
 * All domain exceptions extend this class for consistent error handling.
 */
@Getter
public abstract class SearchServiceException extends RuntimeException {

    private final String errorCode;
    private final HttpStatus httpStatus;

    protected SearchServiceException(String message, String errorCode, HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }
}
