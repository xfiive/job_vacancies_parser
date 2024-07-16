package org.parser.parsermail.services;

import lombok.RequiredArgsConstructor;
import org.parser.parsermail.bot.MyTelegramBot;
import org.parser.parsermail.entities.JobVacancy;
import org.parser.parsermail.parser.ParsingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ControllingService {

    private static final Logger logger = LoggerFactory.getLogger(ParsingService.class);
    private final MyTelegramBot bot;
    private final ParsingService parsingService;
    private final JobVacanciesService jobVacanciesService;

        @Scheduled(fixedRate = 30000)
    public void parseJobVacancies() {
        List<JobVacancy> validJobVacancies = this.parsingService.parseAndGetValidJobs();

//        validJobVacancies.forEach(job -> System.out.println(job.toString()));

        logger.info("Valid jobs size: {}", validJobVacancies.size());

        logger.info("Whole amount of jobs: {}", this.jobVacanciesService.findAll().size());
    }

//    @Scheduled(fixedRate = 30000)
    public void clearThemAll() {
        this.parsingService.dropThemAll();
        logger.info("Collection has been cleared.");
    }

}
