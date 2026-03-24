package sn.travel.rec_service.data.nodes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Noeud Neo4j representant un voyage.
 */
@Node("Travel")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TravelNode {

    @Id
    private UUID id;

    private String title;
    private String description;
    private Double price;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private String accommodationType;
    private String transportationType;

    @Builder.Default
    @Relationship(type = "HAS_DESTINATION", direction = Relationship.Direction.OUTGOING)
    private List<DestinationNode> destinations = new ArrayList<>();

    @Builder.Default
    @Relationship(type = "HAS_ACTIVITY", direction = Relationship.Direction.OUTGOING)
    private List<ActivityNode> activities = new ArrayList<>();
}
