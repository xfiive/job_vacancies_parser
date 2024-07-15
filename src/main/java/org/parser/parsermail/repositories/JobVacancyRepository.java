package org.parser.parsermail.repositories;

import org.parser.parsermail.entities.JobVacancy;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JobVacancyRepository extends MongoRepository<JobVacancy, String> {
    Optional<JobVacancy> findByJobNameAndCompanyNameAndJobLocation(String jobName, String companyName, String jobLocation);
}
