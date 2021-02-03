package ch.wyler.jobfinder.services;

import static org.apache.commons.lang3.StringUtils.substringBetween;
import static org.apache.commons.lang3.math.NumberUtils.toLong;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import ch.wyler.jobfinder.nodes.Company;
import ch.wyler.jobfinder.nodes.Job;
import ch.wyler.jobfinder.repositories.CompanyRepository;
import ch.wyler.jobfinder.repositories.JobRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CrawlerService {

    private static final int MAX_PAGES = 2;
    private static final String BASE_URL = "https://jobs.mobiliar.ch";

    private final JobRepository jobRepository;
    private final CompanyRepository companyRepository;

    public CrawlerService(final JobRepository jobRepository, final CompanyRepository companyRepository) {
        this.jobRepository = jobRepository;
        this.companyRepository = companyRepository;
        reset();
        crawl();
    }

    public void crawl() {
        final Company company = companyRepository.save(new Company("Mobiliar"));

        final WebClient client = WebClient.builder()
                .baseUrl(BASE_URL)
                .build();
        for (int i = 1; i < MAX_PAGES; i++) {
            final String result = client.post().uri("/Jobs/All?tc1152481=p" + i).retrieve().bodyToMono(String.class).block();
            final Document document;

            document = Jsoup.parse(result);

            final Elements elements = document.select("span.tableaslist_subtitle a.HSTableLinkSubTitle");
            for (final Element element : elements) {
                final String jobTitle = element.childNodes().get(0).toString();
                final String descriptionLink = element.attributes().get("href");
                final long id = toLong(substringBetween(descriptionLink, "/Vacancies/", "/Description/"));

                final Job job = jobRepository.save(new Job(id, jobTitle));
                company.addJob(job);
            }

            waitBeforeNextRequest();
        }
        companyRepository.save(company);
    }

    private void reset() {
        jobRepository.deleteAll();
        companyRepository.deleteAll();
    }

    private void waitBeforeNextRequest() {
        try {
            Thread.sleep(2000);
        } catch (final InterruptedException e) {
            log.error("Thread interrupted", e);
            Thread.currentThread().interrupt();
        }
    }
}
