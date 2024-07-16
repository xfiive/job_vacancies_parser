package org.parser.parsermail.parser;

import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.parser.parsermail.entities.JobVacancy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PageParser {

    private static final Logger logger = LoggerFactory.getLogger(ParsingService.class);

    public List<JobVacancy> getJobVacancies(@NotNull List<Document> documents) {
        var vacancies = this.carveJobVacancies(documents);

        logger.info("Amount of jobs scanned: {}", vacancies.size());

        return vacancies;
    }

    private @NotNull List<JobVacancy> carveJobVacancies(@NotNull List<Document> documents) {
        List<JobVacancy> jobVacancies = new ArrayList<>();

        for (Document document : documents) {
            Elements liClass = document.select("li.list-row");
            for (Element element : liClass) {
                Element jobNameElement = element.select("h2 a").first();
                String jobName = jobNameElement != null ? jobNameElement.text() : "N/A";
                String jobLink = jobNameElement != null ? "https://www.profesia.sk" + jobNameElement.attr("href") : null;

                String companyName = getElementText(element, "span.employer", "N/A");
                String jobLocation = getElementText(element, "span.job-location", "N/A");
                int salary = getElementInt(element, "span.label.label-bordered.green", 0);
                String contractType = getElementText(element, "span.contract-type", "N/A");

                JobVacancy jobVacancy = new JobVacancy();
                jobVacancy.setJobName(jobName);
                jobVacancy.setJobLink(jobLink);
                jobVacancy.setCompanyName(companyName);
                jobVacancy.setJobLocation(jobLocation);
                jobVacancy.setSalary(salary);
                jobVacancy.setContractType(contractType);

                jobVacancies.add(jobVacancy);
//                logger.info("Saved job vacancy: {}", jobVacancy);
            }
        }

        return jobVacancies;
    }

    private String getElementText(@NotNull Element element, String cssQuery, String defaultValue) {
        Element foundElement = element.select(cssQuery).first();
        return foundElement != null ? foundElement.text() : defaultValue;
    }

    private String getElementAttr(@NotNull Element element, String cssQuery, String attr, String defaultValue) {
        Element foundElement = element.select(cssQuery).first();
        return foundElement != null ? foundElement.attr(attr) : defaultValue;
    }

    private int getElementInt(@NotNull Element element, String cssQuery, int defaultValue) {
        Element foundElement = element.select(cssQuery).first();
        if (foundElement != null) {
            try {
                String text = foundElement.text().replaceAll("[^0-9]", "");
                return Integer.parseInt(text);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

}
