package com.summary.api.controller;

import com.summary.api.consumer.HeadlinesConsumer;
import com.summary.api.domain.Article;
import com.summary.api.domain.Headlines;
import com.summary.api.repository.ArticleRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class ArticleController {

    private final ArticleRepository repository;

    private final HeadlinesConsumer headlinesConsumer;

    public ArticleController(ArticleRepository repository, HeadlinesConsumer headlinesConsumer) {
        this.repository = repository;
        this.headlinesConsumer = headlinesConsumer;
    }


    @GetMapping("/articles")
    public List<Article> getArticles() {
        return repository.findAll();
    }

    @GetMapping("/articles/latest")
    public List<Article> getLatestArticles() {
        return repository.findFirst10ByOrderByCreationDateDesc();
    }

    @GetMapping("/headlines")
    public Headlines getLatestHeadlines() {
        return headlinesConsumer.getHeadlines();
    }
}
