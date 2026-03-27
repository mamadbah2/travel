package sn.travel.rec_service.data.relationships;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;
import sn.travel.rec_service.data.nodes.TravelNode;

import java.time.LocalDateTime;

/**
 * Relation SUBSCRIBED_TO entre un Traveler et un Travel.
 */
@RelationshipProperties
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscribedToRelationship {

    @Id
    @GeneratedValue
    private Long id;

    @TargetNode
    private TravelNode travel;

    private LocalDateTime createdAt;
}
