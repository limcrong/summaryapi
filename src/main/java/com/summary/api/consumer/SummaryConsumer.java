package com.summary.api.consumer;

import com.summary.api.domain.SummaryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;


@Component
public class SummaryConsumer {

    private static final Logger log = LoggerFactory.getLogger(SummaryConsumer.class);
    private static final String apiKey = "b12ac524a6fc37f5e15d4279108cd6e8";

    private static final int sentences = 2;

    private static final String url = "https://api.meaningcloud.com/summarization-1.0";

    public String getSummary(String content) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("key", apiKey);
        params.add("txt", content);
        params.add("sentences", String.valueOf(sentences));

        ResponseEntity<SummaryResponse> responseEntity = restTemplate.postForEntity(url, params, SummaryResponse.class);

        if(responseEntity.getStatusCodeValue() == 200){
            return responseEntity.getBody().getSummary();
        }
        return "";

    }
}
