package ch.wyler.jobfinder.repositories;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import ch.wyler.jobfinder.nodes.Keyword;

public interface KeywordRepository extends Neo4jRepository<Keyword, Long> {

    Keyword findByName(String name);
}
