package ch.wyler.jobfinder.nodes;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Node
public class Place {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    public Place(final String name) {
        this.name = name;
    }

}
