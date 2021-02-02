package ch.wyler.jobfinder;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node
public class Job {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private Job() {
        // Empty constructor required as of Neo4j API 2.0.5
    }

    public Job(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }
}
