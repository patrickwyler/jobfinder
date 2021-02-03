package ch.wyler.jobfinder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

@SpringBootApplication
@EnableNeo4jRepositories
public class JobfinderApplication {

    public static void main(final String[] args) {
        SpringApplication.run(JobfinderApplication.class, args);
    }
}
