package ch.wyler.jobfinder;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

@Node
public class Company {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private Company() {
        // Empty constructor required as of Neo4j API 2.0.5
    }

    public Company(final String name) {
        this.name = name;
    }

    @Relationship(type = "OFFERS")
    public Set<Job> jobs;

    public void addJob(final Job job) {
        if (jobs == null) {
            jobs = new HashSet<>();
        }
        jobs.add(job);
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }
}
