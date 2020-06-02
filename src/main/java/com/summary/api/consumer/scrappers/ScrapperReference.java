package com.summary.api.consumer.scrappers;

import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Configuration
public class ScrapperReference {

    private List<String> supportedSources = new ArrayList<String>() {{
        add("Channelnewsasia.com");
        add("Straitstimes.com");
        add("Todayonline.com");
        add("CNA");
        add("The Straits Times");
        add("TODAYonline");
//        add("Businessinsider.com");
    }};

    public List<String> getSupportedSources(){return supportedSources;}

    private HashMap<String, String> contentClassMap = new HashMap<String, String>() {{
        put("Channelnewsasia.com", "c-rte--article");
        put("Straitstimes.com", "odd field-item");
        put("Todayonline.com", "article-detail_body");
    }};

    public HashMap<String, String> getContentClassMap(){
        return contentClassMap;
    }

    private HashMap<String, String> contentIdMap = new HashMap<String, String>() {{
        put("Business Insider", "the_bi_content");
    }};

    public HashMap<String, String> getContentIdMap(){
        return contentIdMap;
    }

    private HashMap<String, List<String>> filterTagMap = new HashMap<String, List<String>>() {{
        List<String> cnaFilter = Arrays.asList("a", "em");
        List<String> biFilter = Arrays.asList("dl");
        put("Channelnewsasia.com", cnaFilter);
        put("Business Insider", biFilter);
    }};

    public HashMap<String, List<String>> getFilterTagMap(){
        return filterTagMap;
    }

    private HashMap<String, List<String>> filterClassMap = new HashMap<String, List<String>>() {{
        List<String> straitsTimesFilter = Arrays.asList("paywall-box-area");
        put("Straitstimes.com", straitsTimesFilter);
    }};

    public HashMap<String, List<String>> getFilterClassMap(){
        return filterClassMap;
    }
}
