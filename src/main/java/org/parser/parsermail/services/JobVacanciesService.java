package org.parser.parsermail.services;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.parser.parsermail.entities.JobVacancy;
import org.parser.parsermail.parser.ParsingService;
import org.parser.parsermail.repositories.JobVacancyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobVacanciesService {

    private static final Logger logger = LoggerFactory.getLogger(ParsingService.class);

    private final MongoTemplate mongoTemplate;
    private final JobVacancyRepository jobVacancyRepository;

    private final ConcurrentHashMap<String, JobVacancy> cache = new ConcurrentHashMap<>();

    @PostConstruct
    public void ensureIndexes() {
        IndexOperations indexOps = mongoTemplate.indexOps(JobVacancy.class);
        indexOps.ensureIndex(new Index()
                .on("jobName", Sort.Direction.ASC)
                .on("companyName", Sort.Direction.ASC)
                .on("jobLink", Sort.Direction.ASC)
                .unique());
    }

    public List<JobVacancy> getValidJobVacancies(@NotNull List<JobVacancy> jobs) {
        if (cache.isEmpty()) {
            List<JobVacancy> existingVacancies = mongoTemplate.findAll(JobVacancy.class);
            existingVacancies.forEach(vacancy -> {
                String normalizedUrl = normalizeUrl(vacancy.getJobLink());
                if (normalizedUrl != null) {
                    cache.put(normalizedUrl, vacancy);
                }
            });
        }

        return jobs.stream()
                .filter(this::isJobVacancyValid)
                .peek(this::addJobVacancy)
                .collect(Collectors.toList());
    }

    public boolean isJobVacancyValid(@NotNull JobVacancy jobVacancy) {
        if (jobVacancy.getJobName().equals("N/A") && jobVacancy.getCompanyName().equals("N/A") && jobVacancy.getJobLocation().equals("N/A")) {
            return false;
        }

        String normalizedUrl = normalizeUrl(jobVacancy.getJobLink());
        if (normalizedUrl == null) {
            return false;
        }

        return !cache.containsKey(normalizedUrl);
    }

    public Optional<JobVacancy> addJobVacancy(@NotNull JobVacancy jobVacancy) {
        jobVacancy.setId(generateUniqueId(jobVacancy));
        String normalizedUrl = normalizeUrl(jobVacancy.getJobLink());

        if (normalizedUrl == null) {
            return Optional.empty();
        }

        try {
            mongoTemplate.insert(jobVacancy);
            cache.put(normalizedUrl, jobVacancy); // Update cache
            return Optional.of(jobVacancy);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public boolean delete(@NotNull JobVacancy jobVacancy) {
        Query query = new Query();
        query.addCriteria(Criteria.where("jobName").is(jobVacancy.getJobName())
                .and("companyName").is(jobVacancy.getCompanyName())
                .and("jobLink").is(normalizeUrl(jobVacancy.getJobLink())));

        JobVacancy existingJobVacancy = mongoTemplate.findOne(query, JobVacancy.class);

        if (existingJobVacancy == null) return false;

        mongoTemplate.remove(existingJobVacancy);
        return true;
    }

    public Optional<JobVacancy> find(@NotNull JobVacancy jobVacancy) {
        return jobVacancyRepository.findByJobNameAndCompanyNameAndJobLocation(
                jobVacancy.getJobName(),
                jobVacancy.getCompanyName(),
                jobVacancy.getJobLocation()
        );
    }

    public List<JobVacancy> findAll() {
        return this.jobVacancyRepository.findAll();
    }

    public void clearVacanciesCollection() {
        mongoTemplate.dropCollection("vacancies");
    }

    private @NotNull String generateUniqueId(@NotNull JobVacancy jobVacancy) {
        String source;
        if (jobVacancy.getJobLink() != null)
            source = jobVacancy.getJobName() + jobVacancy.getCompanyName() + normalizeUrl(jobVacancy.getJobLink());
        else
            source = jobVacancy.getJobName() + jobVacancy.getCompanyName();
        return Integer.toHexString(source.hashCode());
    }

    private String normalizeUrl(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }
        String normalizedUrl = url;
        Pattern pattern = Pattern.compile("^(.*?)(\\?.*search_id=[^&]*&?)(.*)$");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            normalizedUrl = matcher.group(1) + matcher.group(3);
        }
        return normalizedUrl;
    }
}
