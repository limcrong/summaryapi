package com.summary.api.controller;

import com.summary.api.consumer.HeadlineScrapper;
import com.summary.api.consumer.HeadlinesConsumer;
import com.summary.api.consumer.SummaryConsumer;
import com.summary.api.dao.HeadlineDao;
import com.summary.api.dao.Article;
import com.summary.api.domain.Headline;
import com.summary.api.domain.Headlines;
import com.summary.api.repository.ArticleRepository;
import com.summary.api.repository.HeadlineRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping("/headline/summarize")
    public String getLatestHeadlines(@RequestParam String id) {
        HeadlineDao headline = headlineRepository.findById(Long.parseLong(id)).orElse(null);
        if (headline != null) {
            String content = headlineScrapper.scrapeContent(headline.getUrl(), headline.getSource());
            if(!content.equals("failed") || !content.equals("")){
                String summary = summaryConsumer.getSummary(content);
                Article article = new Article();
                article.setContent(summary);
                article.setCategory("Top News");
                article.setTitle(headline.getTitle());
                repository.save(article);
                return summary;
            }
            return content;
        }
        return "Not found";
    }

    @GetMapping("/getHeadlines")
    public List<HeadlineDao> testHeadlines() {
        Headlines articles = headlinesConsumer.getHeadlines();
        saveHeadlines(articles);
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
                headlineDao.setTitle(article.getTitle());
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
