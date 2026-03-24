package sn.travel.rec_service.data.relationships;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;
import sn.travel.rec_service.data.enums.ReportStatus;
import sn.travel.rec_service.data.nodes.TravelerNode;

import java.time.LocalDateTime;

/**
 * Relation REPORTED entre un Traveler (rapporteur) et un Traveler (signale).
 */
@RelationshipProperties
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportedRelationship {

    @Id
    @GeneratedValue
    private Long id;

    @TargetNode
    private TravelerNode reportedUser;

    private String reason;
    private ReportStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
}
