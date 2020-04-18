package com.summary.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.lang.reflect.Array;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Headlines {

    private String status;

    private int totalResults;

    private List<Headline> articles;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public List<Headline> getArticles() {
        return articles;
    }

    public void setArticles(List<Headline> articles) {
        this.articles = articles;
    }
}
