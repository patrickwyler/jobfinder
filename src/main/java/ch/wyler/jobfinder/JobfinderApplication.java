package ch.wyler.jobfinder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.web.reactive.function.client.WebClient;


@SpringBootApplication
@EnableNeo4jRepositories
public class JobfinderApplication {

    private static final int MAX_PAGES = 7;
    private static final String BASE_URL = "https://jobs.mobiliar.ch";

    public static void main(final String[] args) {
        SpringApplication.run(JobfinderApplication.class, args);
    }

    @Bean
    CommandLineRunner demo(final JobRepository jobRepository, final CompanyRepository companyRepository) {
        return args -> {
            jobRepository.deleteAll();
            companyRepository.deleteAll();

            final Company company = companyRepository.save(new Company("Mobiliar"));

            final WebClient client = WebClient.builder()
                    .baseUrl(BASE_URL)
                    .build();
            for (int i = 1; i < MAX_PAGES; i++) {
                final String result = client.post().uri("/Jobs/All?tc1152481=p" + i).retrieve().bodyToMono(String.class).block();
                final Document document = Jsoup.parse(result);
                final Elements elements = document.select("span.tableaslist_subtitle a.HSTableLinkSubTitle");
                for (final Element element : elements) {
                    final String jobTitle = element.childNodes().get(0).toString();
                    final Job job = jobRepository.save(new Job(jobTitle));
                    company.addJob(job);
                }

                // wait before next request
                Thread.sleep(2000);
            }
            companyRepository.save(company);
        };
    }
}
