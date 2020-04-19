package com.summary.api.consumer;

import com.summary.api.domain.Headlines;
import jdk.nashorn.internal.runtime.options.LoggingOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class HeadlinesConsumer {

    private static final Logger log = LoggerFactory.getLogger(HeadlinesConsumer.class);
    private static final String apiKey = "7feac32a6cc4425abe64dfe9b42f22ba";

    private static final String country = "sg";

    private static final String url = "http://newsapi.org/v2/top-headlines?country=" + country + "&apiKey=" + apiKey;

    @Bean
    public Headlines getHeadlines() {
        RestTemplate restTemplate = new RestTemplate();
        Headlines headlines = restTemplate.getForObject(url, Headlines.class);
        log.info(url);
        log.info(headlines.toString());
        return headlines;
    }
}
