package ch.wyler.jobfinder.repositories;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import ch.wyler.jobfinder.nodes.Company;

public interface CompanyRepository extends Neo4jRepository<Company, Long> {

    Company findByName(String name);
}
