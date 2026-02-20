package sn.travel.search_service.data.repositories;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import sn.travel.search_service.data.documents.TravelDocument;

/**
 * Elasticsearch repository for TravelDocument CRUD operations.
 * Complex search queries are handled via ElasticsearchOperations in the service layer.
 */
public interface TravelSearchRepository extends ElasticsearchRepository<TravelDocument, String> {
}
