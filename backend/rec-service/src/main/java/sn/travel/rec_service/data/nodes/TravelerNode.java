package sn.travel.rec_service.data.nodes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import sn.travel.rec_service.data.relationships.RatedRelationship;
import sn.travel.rec_service.data.relationships.ReportedRelationship;
import sn.travel.rec_service.data.relationships.SubscribedToRelationship;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Noeud Neo4j representant un voyageur.
 */
@Node("Traveler")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TravelerNode {

    @Id
    private UUID id;

    @Builder.Default
    @Relationship(type = "SUBSCRIBED_TO", direction = Relationship.Direction.OUTGOING)
    private List<SubscribedToRelationship> subscriptions = new ArrayList<>();

    @Builder.Default
    @Relationship(type = "RATED", direction = Relationship.Direction.OUTGOING)
    private List<RatedRelationship> ratings = new ArrayList<>();

    @Builder.Default
    @Relationship(type = "REPORTED", direction = Relationship.Direction.OUTGOING)
    private List<ReportedRelationship> reports = new ArrayList<>();
}
