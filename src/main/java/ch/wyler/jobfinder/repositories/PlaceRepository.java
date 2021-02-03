package ch.wyler.jobfinder.repositories;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import ch.wyler.jobfinder.nodes.Place;

public interface PlaceRepository extends Neo4jRepository<Place, Long> {

    Place findByName(String name);
}
