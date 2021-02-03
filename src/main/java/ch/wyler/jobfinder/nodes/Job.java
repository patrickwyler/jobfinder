package ch.wyler.jobfinder.nodes;

import java.util.HashSet;
import java.util.Set;

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
public class Job {

    @Id
    private Long id;

    private String name;

    @Relationship(type = "WORKS_IN")
    public Set<Place> places;

    public Job(final String name) {
        this.name = name;
    }

    public Job(final Long id, final String name) {
        this.id = id;
        this.name = name;
    }

    public void addPlace(final Place place) {
        if (places == null) {
            places = new HashSet<>();
        }
        places.add(place);
    }
}
