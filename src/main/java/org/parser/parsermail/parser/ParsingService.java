package org.parser.parsermail.parser;

import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParsingService {

    private static final Logger logger = LoggerFactory.getLogger(ParsingService.class);

    private PageConsumer pageConsumer;

    private PageParser pageParser;

    @Autowired
    public void setPageParser(PageParser pageParser) {
        this.pageParser = pageParser;
    }

    @Autowired
    public void setPageConsumer(PageConsumer pageConsumer) {
        this.pageConsumer = pageConsumer;
    }

    @Scheduled(fixedRate = 30000)
    public void start() {

        List<Document> pagesToParse = this.pageConsumer.getPages();

        var jobs = this.pageParser.getJobVacancies(pagesToParse);
    }


}