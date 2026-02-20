package sn.travel.search_service.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Thrown when a travel document is not found in the Elasticsearch index.
 */
public class DocumentNotFoundException extends SearchServiceException {

    private static final String ERROR_CODE = "SEARCH_001";

    public DocumentNotFoundException(String travelId) {
        super(
                String.format("Travel document not found in search index with ID: %s", travelId),
                ERROR_CODE,
                HttpStatus.NOT_FOUND
        );
    }
}
