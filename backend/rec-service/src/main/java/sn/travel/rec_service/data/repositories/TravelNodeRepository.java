package sn.travel.rec_service.data.repositories;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.travel.rec_service.data.nodes.TravelNode;

import java.util.List;
import java.util.UUID;

/**
 * Repository Neo4j pour les noeuds Travel.
 */
@Repository
public interface TravelNodeRepository extends Neo4jRepository<TravelNode, UUID> {

    /**
     * Voyages les plus populaires (nombre de souscriptions + note moyenne).
     */
    @Query("MATCH (t:Travel) " +
           "OPTIONAL MATCH (tr:Traveler)-[s:SUBSCRIBED_TO]->(t) " +
           "OPTIONAL MATCH (tr2:Traveler)-[r:RATED]->(t) " +
           "WITH t, count(DISTINCT s) AS subCount, avg(r.rating) AS avgRating " +
           "RETURN t " +
           "ORDER BY subCount DESC, avgRating DESC " +
           "LIMIT $limit")
    List<TravelNode> findPopular(@Param("limit") int limit);

    /**
     * Voyages similaires (partageant des destinations avec le voyage donne).
     */
    @Query("MATCH (t:Travel {id: $travelId})-[:HAS_DESTINATION]->(d:Destination)<-[:HAS_DESTINATION]-(similar:Travel) " +
           "WHERE similar.id <> $travelId " +
           "RETURN DISTINCT similar " +
           "ORDER BY similar.title " +
           "LIMIT $limit")
    List<TravelNode> findSimilar(@Param("travelId") UUID travelId, @Param("limit") int limit);

    /**
     * Note moyenne d'un voyage.
     */
    @Query("MATCH (tr:Traveler)-[r:RATED]->(t:Travel {id: $travelId}) " +
           "RETURN avg(r.rating)")
    Double findAverageRating(@Param("travelId") UUID travelId);

    /**
     * Nombre de souscriptions pour un voyage.
     */
    @Query("MATCH (tr:Traveler)-[:SUBSCRIBED_TO]->(t:Travel {id: $travelId}) " +
           "RETURN count(tr)")
    Long countSubscriptions(@Param("travelId") UUID travelId);
}
