package ch.wyler.jobfinder.repositories;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import ch.wyler.jobfinder.nodes.Job;

public interface JobRepository extends Neo4jRepository<Job, Long> {

    Job findByName(String name);

}
