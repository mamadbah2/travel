package sn.travel.search_service.services.implementation;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import sn.travel.search_service.data.documents.TravelDocument;
import sn.travel.search_service.data.records.TravelCreatedEvent;
import sn.travel.search_service.data.records.TravelUpdatedEvent;
import sn.travel.search_service.data.repositories.TravelSearchRepository;
import sn.travel.search_service.exceptions.DocumentNotFoundException;
import sn.travel.search_service.exceptions.SearchQueryException;
import sn.travel.search_service.services.SearchService;
import sn.travel.search_service.web.dto.responses.PageResponse;
import sn.travel.search_service.web.dto.responses.SearchResultResponse;
import sn.travel.search_service.web.mappers.SearchMapper;

import java.time.LocalDate;
import java.util.List;

/**
 * Implementation of SearchService.
 * Uses ElasticsearchOperations for advanced queries and TravelSearchRepository for CRUD.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SearchServiceImpl implements SearchService {

    private final TravelSearchRepository travelSearchRepository;
    private final ElasticsearchOperations elasticsearchOperations;
    private final SearchMapper searchMapper;

    @Override
    public PageResponse<SearchResultResponse> search(String query, Double minPrice, Double maxPrice,
                                                      LocalDate fromDate, Pageable pageable) {
        log.info("Searching travels: query='{}', minPrice={}, maxPrice={}, fromDate={}", query, minPrice, maxPrice, fromDate);

        try {
            Query esQuery = buildElasticsearchQuery(query, minPrice, maxPrice, fromDate);

            NativeQuery nativeQuery = NativeQuery.builder()
                    .withQuery(esQuery)
                    .withPageable(pageable)
                    .build();

            SearchHits<TravelDocument> searchHits = elasticsearchOperations.search(nativeQuery, TravelDocument.class);

            List<SearchResultResponse> results = searchHits.getSearchHits().stream()
                    .map(hit -> searchMapper.toResponse(hit.getContent()))
                    .toList();

            long totalHits = searchHits.getTotalHits();
            int totalPages = pageable.getPageSize() > 0
                    ? (int) Math.ceil((double) totalHits / pageable.getPageSize())
                    : 0;

            return new PageResponse<>(
                    results,
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    totalHits,
                    totalPages,
                    pageable.getPageNumber() == 0,
                    pageable.getPageNumber() >= totalPages - 1
            );
        } catch (Exception e) {
            log.error("Search query failed: {}", e.getMessage(), e);
            throw new SearchQueryException(query, e.getMessage());
        }
    }

    @Override
    public SearchResultResponse getById(String travelId) {
        log.debug("Getting travel document by ID: {}", travelId);

        TravelDocument document = travelSearchRepository.findById(travelId)
                .orElseThrow(() -> new DocumentNotFoundException(travelId));

        return searchMapper.toResponse(document);
    }

    @Override
    public void indexTravel(TravelCreatedEvent event) {
        log.info("Indexing new travel document: id={}, title='{}'", event.travelId(), event.title());

        TravelDocument document = searchMapper.toDocument(event);
        travelSearchRepository.save(document);

        log.info("Travel document indexed successfully: {}", event.travelId());
    }

    @Override
    public void updateTravel(TravelUpdatedEvent event) {
        log.info("Updating travel document: id={}, title='{}'", event.travelId(), event.title());

        TravelDocument document = searchMapper.toDocument(event);
        travelSearchRepository.save(document);

        log.info("Travel document updated successfully: {}", event.travelId());
    }

    @Override
    public void deleteTravel(String travelId) {
        log.info("Deleting travel document: id={}", travelId);

        if (travelSearchRepository.existsById(travelId)) {
            travelSearchRepository.deleteById(travelId);
            log.info("Travel document deleted successfully: {}", travelId);
        } else {
            log.warn("Travel document not found for deletion: {}", travelId);
        }
    }

    // ---- Private helpers ----

    /**
     * Builds an Elasticsearch bool query with fuzzy text search and optional filters.
     */
    private Query buildElasticsearchQuery(String searchText, Double minPrice, Double maxPrice, LocalDate fromDate) {
        BoolQuery.Builder boolBuilder = new BoolQuery.Builder();

        // Always filter by PUBLISHED status
        boolBuilder.filter(f -> f.term(t -> t.field("status").value("PUBLISHED")));

        // Fuzzy text search across title, description, destinations, activities
        if (searchText != null && !searchText.isBlank()) {
            boolBuilder.must(m -> m.multiMatch(mm -> mm
                    .query(searchText)
                    .fields("title^3", "description^2", "destinations.name",
                            "destinations.country", "activities.name")
                    .fuzziness("AUTO")
            ));
        } else {
            boolBuilder.must(m -> m.matchAll(ma -> ma));
        }

        // Price range filters
        if (minPrice != null) {
            boolBuilder.filter(f -> f.range(r -> r
                    .number(n -> n.field("price").gte(minPrice))
            ));
        }
        if (maxPrice != null) {
            boolBuilder.filter(f -> f.range(r -> r
                    .number(n -> n.field("price").lte(maxPrice))
            ));
        }

        // Date filter: travels starting on or after fromDate
        if (fromDate != null) {
            boolBuilder.filter(f -> f.range(r -> r
                    .date(d -> d.field("startDate").gte(fromDate.toString()))
            ));
        }

        return boolBuilder.build()._toQuery();
    }
}
