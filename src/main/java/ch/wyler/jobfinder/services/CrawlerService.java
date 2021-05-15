package ch.wyler.jobfinder.services;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.apache.commons.lang3.StringUtils.substringBetween;
import static org.apache.commons.lang3.math.NumberUtils.toInt;
import static org.apache.commons.lang3.math.NumberUtils.toLong;

import java.util.Arrays;
import java.util.Optional;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import ch.wyler.jobfinder.nodes.Company;
import ch.wyler.jobfinder.nodes.Job;
import ch.wyler.jobfinder.nodes.Keyword;
import ch.wyler.jobfinder.nodes.Place;
import ch.wyler.jobfinder.repositories.CompanyRepository;
import ch.wyler.jobfinder.repositories.JobRepository;
import ch.wyler.jobfinder.repositories.KeywordRepository;
import ch.wyler.jobfinder.repositories.PlaceRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CrawlerService {

    private static final String BASE_URL = "https://jobs.mobiliar.ch";
    private static final int JOBS_PER_PAGE = 10;

    private final JobRepository jobRepository;
    private final CompanyRepository companyRepository;
    private final PlaceRepository placeRepository;
    private final KeywordRepository keywordRepository;
    private final WebClient client = WebClient.builder()
            .baseUrl(BASE_URL)
            .build();

    public CrawlerService(final JobRepository jobRepository, final CompanyRepository companyRepository,
            final PlaceRepository placeRepository, final KeywordRepository keywordRepository) {
        this.jobRepository = jobRepository;
        this.companyRepository = companyRepository;
        this.placeRepository = placeRepository;
        this.keywordRepository = keywordRepository;
        reset();
        crawl();
    }

    public void crawl() {
        log.info("Start crawling..");
        final Company company = companyRepository.save(new Company("Mobiliar"));

        for (int i = 1; i < getPageCount(); i++) {
            log.info("Crawling page number: {}", i);
            final String result = client.post().uri("/Jobs/All?tc1152481=p" + i)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            final Document document = Jsoup.parse(result);
            final Elements elements = document.select("div.tableaslist_cell");

            elements.forEach(element -> addJobData(company, element));
            waitBeforeNextRequest();
        }
        companyRepository.save(company);
        log.info("All pages crawled.");
    }

    private void addJobData(final Company company, final Element element) {
        final Element elementSubTitle = element.getElementsByClass("HSTableLinkSubTitle").get(0);
        final Element elementPlace = element.getElementsByClass("tableaslist_subtitle").get(3);
        final String jobTitle = elementSubTitle.childNodes().get(0).toString();
        final String descriptionLink = elementSubTitle.attributes().get("href");
        final long id = toLong(substringBetween(descriptionLink, "/Vacancies/", "/Description/"));

        final Job job = jobRepository.save(new Job(id, jobTitle));
        company.addJob(job);

        final String placeText = elementPlace.ownText();
        final String place = substringAfter(placeText, "| Kanton: ");
        if (isNotBlank(place)) {
            Place resultPlace = placeRepository.findByName(place);
            if (resultPlace == null) {
                resultPlace = placeRepository.save(new Place(place));
            }
            job.addPlace(resultPlace);
            jobRepository.save(job);
        }

        addDetailedJobData(descriptionLink, job);
    }

    private void addDetailedJobData(final String descriptionLink, final Job job) {
        final String resultDescription = client.post()
                .uri(descriptionLink)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        final Document documentDetailDescription = Jsoup.parse(resultDescription);
        final Optional<Element> keywordsElement = documentDetailDescription.select("meta")
                .stream()
                .filter(e -> e.attributes().get("name").contains("keywords"))
                .findFirst();

        if (keywordsElement.isPresent()) {
            final String[] keywords = keywordsElement.get()
                    .attributes()
                    .get("content")
                    .split(",");

            Arrays.stream(keywords).forEach(name -> {
                Keyword keyword = keywordRepository.findByName(name);
                if (keyword == null) {
                    keyword = keywordRepository.save(new Keyword(name));
                }

                job.addKeyword(keyword);
            });
        }
        jobRepository.save(job);

        waitBeforeNextRequest();
    }

    private int getPageCount() {
        final String resultGetTokenId = client.get()
                .uri("/Jobs/All")
                .retrieve()
                .bodyToMono(String.class)
                .block();

        final int tokenListId = toInt(substringBetween(resultGetTokenId, "token_list_id&quot;:&quot;", "&quot;"), 0);

        final String result = client.get()
                .uri("/Jobs/All/getTableTotalCount?token_list_id=" + tokenListId)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        final int amountOfJobs = toInt(substringBetween(result, "{\"data\":\"", "\"}"), 0);
        return (amountOfJobs / JOBS_PER_PAGE) + 1;
    }

    private void reset() {
        log.info("Reset");

        jobRepository.deleteAll();
        companyRepository.deleteAll();
        placeRepository.deleteAll();

        log.info("Reset done.");
    }

    private void waitBeforeNextRequest() {
        try {
            Thread.sleep(500);
        } catch (final InterruptedException e) {
            log.error("Thread interrupted", e);
            Thread.currentThread().interrupt();
        }
    }
}
