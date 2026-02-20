package sn.travel.search_service.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Thrown when an Elasticsearch query fails to execute.
 */
public class SearchQueryException extends SearchServiceException {

    private static final String ERROR_CODE = "SEARCH_002";

    public SearchQueryException(String query, String reason) {
        super(
                String.format("Search query failed for '%s': %s", query, reason),
                ERROR_CODE,
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
