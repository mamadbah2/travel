package sn.travel.search_service.web.controllers.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.travel.search_service.services.SearchService;
import sn.travel.search_service.web.controllers.SearchController;
import sn.travel.search_service.web.dto.responses.PageResponse;
import sn.travel.search_service.web.dto.responses.SearchResultResponse;

import java.time.LocalDate;

/**
 * REST controller implementation for search endpoints.
 * All endpoints under /api/v1/search are public (no authentication required).
 */
@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class SearchControllerImpl implements SearchController {

    private final SearchService searchService;

    @Override
    @GetMapping
    public ResponseEntity<PageResponse<SearchResultResponse>> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            Pageable pageable
    ) {
        return ResponseEntity.ok(searchService.search(q, minPrice, maxPrice, fromDate, pageable));
    }

    @Override
    @GetMapping("/{travelId}")
    public ResponseEntity<SearchResultResponse> getById(@PathVariable String travelId) {
        return ResponseEntity.ok(searchService.getById(travelId));
    }
}
