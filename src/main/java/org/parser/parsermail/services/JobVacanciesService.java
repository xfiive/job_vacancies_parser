package org.parser.parsermail.services;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.parser.parsermail.entities.JobVacancy;
import org.parser.parsermail.repositories.JobVacancyRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JobVacanciesService {

    private final MongoTemplate mongoTemplate;

    private final JobVacancyRepository jobVacancyRepository;

    @PostConstruct
    public void ensureIndexes() {
        IndexOperations indexOps = mongoTemplate.indexOps(JobVacancy.class);
        indexOps.ensureIndex(new Index().on("jobName", Sort.Direction.ASC).on("companyName", Sort.Direction.ASC).on("jobLink", Sort.Direction.ASC).unique());
    }

    public void addJobVacancy(@NotNull JobVacancy jobVacancy) {
        Query query = new Query();
        query.addCriteria(Criteria.where("jobName").is(jobVacancy.getJobName())
                .and("companyName").is(jobVacancy.getCompanyName())
                .and("jobLink").is(jobVacancy.getJobLink()));

        JobVacancy existingJobVacancy = mongoTemplate.findOne(query, JobVacancy.class);

        if (existingJobVacancy == null) {
            mongoTemplate.save(jobVacancy);
        } else {
            System.out.println("Job vacancy already exists: " + existingJobVacancy);
        }
    }

    public Optional<JobVacancy> find(@NotNull JobVacancy jobVacancy) {
        return jobVacancyRepository.findByJobNameAndCompanyNameAndJobLocation(
                jobVacancy.getJobName(),
                jobVacancy.getCompanyName(),
                jobVacancy.getJobLocation()
        );
    }

}
