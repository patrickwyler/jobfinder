package ch.wyler.jobfinder.nodes;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@Getter
@Setter
@Node
public class Job {

    @Id
    private Long id;

    private String name;

    public Job(final String name) {
        this.name = name;
    }

    public Job(final Long id, final String name) {
        this.id = id;
        this.name = name;
    }
}
