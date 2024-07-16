package org.parser.parsermail.parser;

import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Document;
import org.parser.parsermail.entities.JobVacancy;
import org.parser.parsermail.services.JobVacanciesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ParsingService {

    private static final Logger logger = LoggerFactory.getLogger(ParsingService.class);

    private final PageConsumer pageConsumer;

    private final PageParser pageParser;

    private final JobVacanciesService jobVacanciesService;

    public List<JobVacancy> parseAndGetValidJobs() {

        List<Document> pagesToParse = this.pageConsumer.getPages();

        List<JobVacancy> jobs = this.pageParser.getJobVacancies(pagesToParse);

        return this.jobVacanciesService.getValidJobVacancies(jobs);
    }

    public void dropThemAll() {
        this.jobVacanciesService.clearVacanciesCollection();
    }


}