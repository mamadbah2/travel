package sn.travel.search_service.services;

import org.springframework.data.domain.Pageable;
import sn.travel.search_service.data.records.TravelCreatedEvent;
import sn.travel.search_service.data.records.TravelUpdatedEvent;
import sn.travel.search_service.web.dto.responses.PageResponse;
import sn.travel.search_service.web.dto.responses.SearchResultResponse;

import java.time.LocalDate;

/**
 * Service interface for search and indexing operations.
 */
public interface SearchService {

    /**
     * Full-text fuzzy search across the travel catalog with optional filters.
     *
     * @param query    search text (fuzzy match on title, description, destinations, activities)
     * @param minPrice minimum price filter (inclusive)
     * @param maxPrice maximum price filter (inclusive)
     * @param fromDate only travels starting on or after this date
     * @param pageable pagination parameters
     * @return paginated search results
     */
    PageResponse<SearchResultResponse> search(String query, Double minPrice, Double maxPrice,
                                               LocalDate fromDate, Pageable pageable);

    /**
     * Get a single travel document by its ID.
     *
     * @param travelId the travel UUID (stored as String in ES)
     * @return the search result
     */
    SearchResultResponse getById(String travelId);

    /**
     * Index a new travel document (triggered by TravelCreatedEvent).
     */
    void indexTravel(TravelCreatedEvent event);

    /**
     * Update an existing travel document (triggered by TravelUpdatedEvent).
     */
    void updateTravel(TravelUpdatedEvent event);

    /**
     * Delete a travel document from the index (triggered by TravelDeletedEvent).
     */
    void deleteTravel(String travelId);
}
