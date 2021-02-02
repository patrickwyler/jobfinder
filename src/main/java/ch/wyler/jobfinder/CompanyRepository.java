package ch.wyler.jobfinder;

import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface CompanyRepository extends Neo4jRepository<Company, Long> {

    Company findByName(String name);
}
