package com.summary.api.consumer;

import com.summary.api.consumer.scrappers.ScrapperReference;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@Component
public class HeadlineScrapper {

    //    public static final String url = "https://www.channelnewsasia.com/news/singapore/man-hit-covid19-safe-distancing-ambassador-hougang-mcdonalds-12655216";
    public static final String url2 = "https://www.businessinsider.sg/coronavirus-antibodies-cant-guarantee-long-term-immunity-who-said-2020-4?r=US&IR=T";
    public static final String url3 = "https://www.todayonline.com/singapore/all-construction-workers-holding-work-permits-or-s-passes-be-placed-compulsory-stay-home";
    public static final String url4 = "https://www.channelnewsasia.com/news/asia/hong-kong-protests-police-arrest-more-than-200-12721380";
//    private static final String source = "Channelnewsasia.com";
//    private static final String source = "Todayonline.com";

    private ScrapperReference scrapperReference;

    @Autowired
    public HeadlineScrapper(ScrapperReference scrapperReference) {
        this.scrapperReference = scrapperReference;
    }


    public String scrapeContent(String url, String source) {
        HashMap<String, String> contentClassMap = scrapperReference.getContentClassMap();
        HashMap<String, String> contendIdMap = scrapperReference.getContentIdMap();
        HashMap<String, List<String>> filterClassMap = scrapperReference.getFilterClassMap();
        HashMap<String, List<String>> filterTagMap = scrapperReference.getFilterTagMap();

        try {
            Document doc = Jsoup.connect(url).maxBodySize(0).timeout(30000).get();

            if (contentClassMap.containsKey(source)) {
                String content = contentClassMap.get(source);
                Elements elements = doc.getElementsByClass(content);
                filterUnnecessaryWords(filterClassMap, filterTagMap, elements, source);
                return Jsoup.parse(elements.text()).text();
            } else if (contendIdMap.containsKey(source)) {
                String id = contendIdMap.get(source);
                Elements elements = doc.select("div#" + id);
                filterUnnecessaryWords(filterClassMap, filterTagMap, elements, source);
                return Jsoup.parse(elements.text()).text();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "failed";
    }

    private void filterUnnecessaryWords(HashMap<String, List<String>> filterClassMap,
                                        HashMap<String, List<String>> filterTagMap,
                                        Elements elements, String source) {
        if (filterClassMap.containsKey(source)) {
            List<String> filters = filterClassMap.get(source);
            filters.forEach(filter -> {
                elements.select("div." + filter).remove();
            });
        }
        if (filterTagMap.containsKey(source)) {
            List<String> tagFilters = filterTagMap.get(source);
            tagFilters.forEach(tagFilter -> {
                elements.select(tagFilter).remove();
            });
        }
        elements.select("aside[class*=advertisement]").remove();
        elements.select("div[class*=picture]").remove();
    }
}
