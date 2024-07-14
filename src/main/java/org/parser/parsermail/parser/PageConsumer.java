package org.parser.parsermail.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class PageConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ParsingService.class);

    private static String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";

    @Value("${base.url}")
    private String baseUrl;

    public List<Document> getPages() {
        int pageNumber = 1;
        List<Document> pages = new ArrayList<>();

        // Oh God, sorry, really sorry for that while(true) loop, but I don't have any other ideas...
        while (true) {
            String nextPageUrl = baseUrl + pageNumber;
            Document page = this.getRequiredPage(nextPageUrl);

            if (page.select("li.list-row").isEmpty()) {
                break;
            }

            pages.add(page);
            pageNumber++;
        }

        return pages;
    }

    private Document getRequiredPage(String url) {
        Document document = null;

        try {
            document = Jsoup
                    .connect(url)
                    .userAgent(userAgent)
                    .get();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        return document;
    }

}
