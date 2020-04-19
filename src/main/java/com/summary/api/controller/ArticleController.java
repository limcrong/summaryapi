package com.summary.api.controller;

import com.summary.api.consumer.HeadlineScrapper;
import com.summary.api.consumer.HeadlinesConsumer;
import com.summary.api.consumer.SummaryConsumer;
import com.summary.api.dao.HeadlineDao;
import com.summary.api.domain.Article;
import com.summary.api.domain.Headline;
import com.summary.api.domain.Headlines;
import com.summary.api.repository.ArticleRepository;
import com.summary.api.repository.HeadlineRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
public class ArticleController {

    private final ArticleRepository repository;

    private final HeadlinesConsumer headlinesConsumer;

    private final HeadlineRepository headlineRepository;

    private final HeadlineScrapper headlineScrapper;

    private final SummaryConsumer summaryConsumer;

    public ArticleController(ArticleRepository repository, HeadlinesConsumer headlinesConsumer, HeadlineRepository headlineRepository, HeadlineScrapper headlineScrapper, SummaryConsumer summaryConsumer) {
        this.repository = repository;
        this.headlinesConsumer = headlinesConsumer;
        this.headlineRepository = headlineRepository;
        this.headlineScrapper = headlineScrapper;
        this.summaryConsumer = summaryConsumer;
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
    public String getLatestHeadlines() {
        String content = headlineScrapper.scrapeContent();
        String summary = summaryConsumer.getSummary(content);
        return summary;
    }

    @GetMapping("/saveHeadlines")
    public List<HeadlineDao> testHeadlines() {
        return (List<HeadlineDao>) headlineRepository.findAll();
    }

    private void saveHeadlines(Headlines headlines){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        List<Headline> articles = headlines.getArticles();
        articles.forEach(article->{
            try {
                HeadlineDao headlineDao = new HeadlineDao();
                headlineDao.setSource(article.getSource().getName());
                headlineDao.setImageUrl(article.getUrlToImage());
                Date date = formatter.parse(article.getPublishedAt().replaceAll("Z$", "+0000"));
                headlineDao.setPublishedTime(date);
                headlineDao.setUrl(article.getUrl());
                headlineRepository.save(headlineDao);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
    }
}
