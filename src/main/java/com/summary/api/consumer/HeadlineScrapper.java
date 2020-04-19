package com.summary.api.consumer;

import com.summary.api.consumer.scrappers.ScrapperReference;
import com.summary.api.domain.Headlines;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import sun.java2d.pipe.SpanClipRenderer;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class HeadlineScrapper {

    public static final String url = "https://www.channelnewsasia.com/news/singapore/man-hit-covid19-safe-distancing-ambassador-hougang-mcdonalds-12655216";
    public static final String url2 = "https://www.channelnewsasia.com/news/asia/covid-19-tests-wuhan-compulsory-key-public-workers-leaving-city-12655178";
    private static final String source = "Channelnewsasia.com";

    private ScrapperReference scrapperReference;

    @Autowired
    public HeadlineScrapper(ScrapperReference scrapperReference) {
        this.scrapperReference = scrapperReference;
    }

    @Bean
    public String scrapeContent() {
        HashMap<String , String> contentMap = scrapperReference.getContentMap();
        HashMap<String , List<String>> filterMap = scrapperReference.getFilterMap();

        try {
            Document doc = Jsoup.connect(url2).get();
            String content = contentMap.get(source);
            Elements elements = doc.getElementsByClass(content);
            List<String> filters = filterMap.get(source);
            filters.forEach(filter->{
                elements.select(filter).remove();
            });
            return Jsoup.parse(elements.text()).text();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "failed";
    }
}
