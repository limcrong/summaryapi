package com.summary.api.consumer.scrappers;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;

@Configuration
public class ScrapperReference {

    private List<String> supportedSources = new ArrayList<String>() {{
        add("Channelnewsasia.com");
        add("Straitstimes.com");
        add("Todayonline.com");
        add("CNA");
        add("The Straits Times");
        add("TODAYonline");
        add("Business Times");
        add("Yahoo Entertainment");
        add("Post Magazine");
        add("AsiaOne");
//        add("Businessinsider.com");
    }};

    public List<String> getSupportedSources() {
        return supportedSources;
    }

    @Bean
    public List<String> seleniumSources() {
        return new ArrayList<String>() {{
            add("TODAYonline");
            add("Business Times");
            add("Yahoo Entertainment");
            add("Post Magazine");
            add("AsiaOne");
        }};
    }

    @Bean
    public List<String> jsoupSources() {
        return new ArrayList<String>() {{
            add("CNA");
            add("The Straits Times");
        }};
    }

    @Bean
    public HashMap<String, String> contentClassMap() {
        return new HashMap<String, String>() {{
            put("CNA", "c-rte--article");
            put("The Straits Times", "odd field-item");
            put("TODAYonline", "article-detail_body");
            put("Business Times", "field-type-text-with-summary");
            put("Yahoo Entertainment", "canvas-body");
            put("Post Magazine", "row__details");
            put("AsiaOne", "body");
        }};
    }

    @Bean
    public HashMap<String, String> contentIdMap() {
        return new HashMap<String, String>() {{
            put("Business Insider", "the_bi_content");
        }};
    }

    @Bean
    public HashMap<String, List<String>> filterTagMap() {
        return new HashMap<String, List<String>>() {{
            List<String> cnaFilter = Arrays.asList("a", "em");
            List<String> biFilter = Collections.singletonList("dl");
            put("CNA", cnaFilter);
            put("Business Insider", biFilter);
        }};
    }
    @Bean
    public HashMap<String, List<String>> filterClassMap() {
        return new HashMap<String, List<String>>() {{
            List<String> straitsTimesFilter = Collections.singletonList("paywall-box-area");
            List<String> businessTimesFilter = Arrays.asList("related-articles", "block-block");
            List<String> postMagazineFilter = Arrays.asList("subscription", "comment",
                    "author-card", "related-topic");
            List<String> asiaOneFilter = Collections.singletonList("embed-title");
            put("The Straits Times", straitsTimesFilter);
            put("Business Times", businessTimesFilter);
            put("Post Magazine", postMagazineFilter);
            put("AsiaOne", asiaOneFilter);
        }};
    }

}
