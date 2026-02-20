package sn.travel.search_service.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import sn.travel.search_service.web.dto.responses.PageResponse;
import sn.travel.search_service.web.dto.responses.SearchResultResponse;

import java.time.LocalDate;

/**
 * Controller interface for search endpoints.
 * All search endpoints are public (CQRS read-side).
 */
@Tag(name = "Search", description = "Elasticsearch-powered travel search API")
public interface SearchController {

    @Operation(
            summary = "Search travels",
            description = "Full-text fuzzy search across the travel catalog. " +
                    "Supports optional filters for price range and departure date. " +
                    "Returns paginated results sorted by relevance score."
    )
    ResponseEntity<PageResponse<SearchResultResponse>> search(
            @Parameter(description = "Search query (fuzzy-matched against title, description, destinations, activities)")
            String query,
            @Parameter(description = "Minimum price filter (inclusive)")
            Double minPrice,
            @Parameter(description = "Maximum price filter (inclusive)")
            Double maxPrice,
            @Parameter(description = "Only travels starting on or after this date (yyyy-MM-dd)")
            LocalDate fromDate,
            Pageable pageable
    );

    @Operation(
            summary = "Get travel by ID",
            description = "Retrieve a specific travel document from the search index by its ID."
    )
    ResponseEntity<SearchResultResponse> getById(String travelId);
}
