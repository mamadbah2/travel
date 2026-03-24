package sn.travel.rec_service.data.repositories;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.travel.rec_service.data.nodes.TravelNode;
import sn.travel.rec_service.data.nodes.TravelerNode;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository Neo4j pour les noeuds Traveler.
 */
@Repository
public interface TravelerNodeRepository extends Neo4jRepository<TravelerNode, UUID> {

    /**
     * Recupere un voyageur avec ses souscriptions (profondeur 1).
     */
    @Query("MATCH (t:Traveler {id: $travelerId}) " +
           "OPTIONAL MATCH (t)-[s:SUBSCRIBED_TO]->(tr:Travel) " +
           "RETURN t, collect(s), collect(tr)")
    Optional<TravelerNode> findByIdWithSubscriptions(@Param("travelerId") UUID travelerId);

    /**
     * Recommandations collaboratives : voyageurs ayant souscrit aux memes voyages
     * ont aussi souscrit a d'autres voyages.
     */
    @Query("MATCH (me:Traveler {id: $travelerId})-[:SUBSCRIBED_TO]->(t:Travel)<-[:SUBSCRIBED_TO]-(other:Traveler) " +
           "MATCH (other)-[:SUBSCRIBED_TO]->(rec:Travel) " +
           "WHERE NOT (me)-[:SUBSCRIBED_TO]->(rec) " +
           "RETURN DISTINCT rec " +
           "ORDER BY rec.title " +
           "LIMIT $limit")
    List<TravelNode> findCollaborativeRecommendations(@Param("travelerId") UUID travelerId, @Param("limit") int limit);

    /**
     * Recommandations basees sur les destinations : voyages partageant des destinations
     * avec l'historique du voyageur.
     */
    @Query("MATCH (me:Traveler {id: $travelerId})-[:SUBSCRIBED_TO]->(t:Travel)-[:HAS_DESTINATION]->(d:Destination) " +
           "MATCH (rec:Travel)-[:HAS_DESTINATION]->(d) " +
           "WHERE NOT (me)-[:SUBSCRIBED_TO]->(rec) AND rec.id <> t.id " +
           "RETURN DISTINCT rec " +
           "ORDER BY rec.title " +
           "LIMIT $limit")
    List<TravelNode> findDestinationBasedRecommendations(@Param("travelerId") UUID travelerId, @Param("limit") int limit);

    /**
     * Recommandations basees sur les activites : voyages partageant des activites
     * avec l'historique du voyageur.
     */
    @Query("MATCH (me:Traveler {id: $travelerId})-[:SUBSCRIBED_TO]->(t:Travel)-[:HAS_ACTIVITY]->(a:Activity) " +
           "MATCH (rec:Travel)-[:HAS_ACTIVITY]->(a) " +
           "WHERE NOT (me)-[:SUBSCRIBED_TO]->(rec) AND rec.id <> t.id " +
           "RETURN DISTINCT rec " +
           "ORDER BY rec.title " +
           "LIMIT $limit")
    List<TravelNode> findActivityBasedRecommendations(@Param("travelerId") UUID travelerId, @Param("limit") int limit);
}
