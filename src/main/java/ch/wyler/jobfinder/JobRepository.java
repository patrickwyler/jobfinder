package ch.wyler.jobfinder;

import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface JobRepository extends Neo4jRepository<Job, Long> {

    Job findByName(String name);
}
