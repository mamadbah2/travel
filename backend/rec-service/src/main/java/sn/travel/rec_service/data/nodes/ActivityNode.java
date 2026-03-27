package sn.travel.rec_service.data.nodes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

/**
 * Noeud Neo4j representant une activite.
 */
@Node("Activity")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityNode {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String description;
}
