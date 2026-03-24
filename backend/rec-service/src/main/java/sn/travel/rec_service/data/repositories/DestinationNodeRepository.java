package sn.travel.rec_service.data.repositories;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;
import sn.travel.rec_service.data.nodes.DestinationNode;

import java.util.Optional;

/**
 * Repository Neo4j pour les noeuds Destination.
 */
@Repository
public interface DestinationNodeRepository extends Neo4jRepository<DestinationNode, Long> {

    Optional<DestinationNode> findByNameAndCountryAndCity(String name, String country, String city);
}
