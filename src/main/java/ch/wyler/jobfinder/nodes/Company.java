package ch.wyler.jobfinder.nodes;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Node
public class Company {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @Relationship(type = "OFFERS")
    public Set<Job> jobs;

    public Company(final String name) {
        this.name = name;
    }

    public void addJob(final Job job) {
        if (jobs == null) {
            jobs = new HashSet<>();
        }
        jobs.add(job);
    }
}
