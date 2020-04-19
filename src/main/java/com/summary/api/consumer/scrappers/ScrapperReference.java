package com.summary.api.consumer.scrappers;

import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Configuration
public class ScrapperReference {
    private HashMap<String, String> contentMap = new HashMap<String, String>() {{
        put("Channelnewsasia.com", "c-rte--article");
    }};

    public HashMap<String, String> getContentMap(){
        return contentMap;
    }

    private HashMap<String, List<String>> filterMap = new HashMap<String, List<String>>() {{
        List<String> cnaFilter = Arrays.asList("a", "em");
        put("Channelnewsasia.com", cnaFilter);
    }};

    public HashMap<String, List<String>> getFilterMap(){
        return filterMap;
    }
}
