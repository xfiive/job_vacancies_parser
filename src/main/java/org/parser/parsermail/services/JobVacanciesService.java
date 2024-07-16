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

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class JobVacanciesService {

    private static final Logger logger = LoggerFactory.getLogger(ParsingService.class);

    private final MongoTemplate mongoTemplate;
    private final JobVacancyRepository jobVacancyRepository;

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
        List<JobVacancy> validJobVacancies = new LinkedList<>();

        for (JobVacancy jobVacancy : jobs) {
            if (this.isJobVacancyValid(jobVacancy)) {
                this.addJobVacancy(jobVacancy).ifPresent(validJobVacancies::add);
            }
        }
        return validJobVacancies;
    }

    public boolean isJobVacancyValid(@NotNull JobVacancy jobVacancy) {
        Query query = new Query();
        query.addCriteria(Criteria.where("jobName").is(jobVacancy.getJobName())
                .and("companyName").is(jobVacancy.getCompanyName())
                .and("jobLink").is(normalizeUrl(jobVacancy.getJobLink())));

        return mongoTemplate.findOne(query, JobVacancy.class) == null;
    }

    public Optional<JobVacancy> addJobVacancy(@NotNull JobVacancy jobVacancy) {
        jobVacancy.setId(generateUniqueId(jobVacancy));  // Устанавливаем уникальный ID

        try {
            mongoTemplate.insert(jobVacancy);
            logger.info("Job Vacancy added: {}", jobVacancy);
            return Optional.of(jobVacancy);
        } catch (Exception e) {
            logger.info("Job Vacancy already exists: {}", jobVacancy);
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
        String source = jobVacancy.getJobName() + jobVacancy.getCompanyName() + normalizeUrl(jobVacancy.getJobLink());
        return Integer.toHexString(source.hashCode());
    }

    private String normalizeUrl(String url) {
        if (url == null) {
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
