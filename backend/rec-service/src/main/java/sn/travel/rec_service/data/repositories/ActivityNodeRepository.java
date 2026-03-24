package sn.travel.rec_service.data.repositories;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;
import sn.travel.rec_service.data.nodes.ActivityNode;

import java.util.Optional;

/**
 * Repository Neo4j pour les noeuds Activity.
 */
@Repository
public interface ActivityNodeRepository extends Neo4jRepository<ActivityNode, Long> {

    Optional<ActivityNode> findByName(String name);
}
