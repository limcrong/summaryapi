package com.summary.api.controller;

import com.summary.api.consumer.HeadlineScrapper;
import com.summary.api.consumer.HeadlinesConsumer;
import com.summary.api.consumer.SummaryConsumer;
import com.summary.api.consumer.scrappers.ScrapperReference;
import com.summary.api.dao.HeadlineDao;
import com.summary.api.dao.Article;
import com.summary.api.dao.NewsArticle;
import com.summary.api.domain.Headline;
import com.summary.api.domain.Headlines;
import com.summary.api.repository.ArticleRepository;
import com.summary.api.repository.HeadlineRepository;
import com.summary.api.repository.NewsArticleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

@RestController
public class ArticleController {

    private final ArticleRepository repository;

    private final HeadlinesConsumer headlinesConsumer;

    private final HeadlineRepository headlineRepository;

    private final HeadlineScrapper headlineScrapper;

    private final SummaryConsumer summaryConsumer;

    private final ScrapperReference scrapperReference;

    private final NewsArticleRepository newsArticleRepository;

    private static final Logger LOGGER = Logger.getLogger( ArticleController.class.getName() );

    public ArticleController(ArticleRepository repository, HeadlinesConsumer headlinesConsumer,
                             HeadlineRepository headlineRepository, HeadlineScrapper headlineScrapper,
                             SummaryConsumer summaryConsumer, ScrapperReference scrapperReference, NewsArticleRepository newsArticleRepository) {
        this.repository = repository;
        this.headlinesConsumer = headlinesConsumer;
        this.headlineRepository = headlineRepository;
        this.headlineScrapper = headlineScrapper;
        this.summaryConsumer = summaryConsumer;
        this.scrapperReference = scrapperReference;
        this.newsArticleRepository = newsArticleRepository;
    }


    @GetMapping("/articles")
    public List<Article> getArticles() {
        return repository.findAll();
    }

    @GetMapping("/articles/latest")
    public List<Article> getLatestArticles() {
        return repository.findFirst10ByOrderByCreationDateDesc();
    }

    @GetMapping("/headline/schedule")
    public List<NewsArticle> getHeadlineTask() {
        Headlines headlines = headlinesConsumer.getHeadlines();
        List<String> supportedSources = scrapperReference.getSupportedSources();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        List<NewsArticle> newsArticles = new ArrayList<>();
        if (headlines != null) {
            List<Headline> headlineList = headlines.getArticles();
            headlineList.forEach(headline -> {
                if (supportedSources.contains(headline.getSource().getName())) {
                    String content = headlineScrapper.scrapeContent(headline.getUrl(), headline.getSource().getName());
                    if (!content.equals("failed") && !content.equals("")) {
                        String summary = summaryConsumer.getSummary(content);

                        try {
                            NewsArticle newsArticle = new NewsArticle();
                            newsArticle.setSource(headline.getSource().getName());
                            newsArticle.setTitle(headline.getTitle());
                            newsArticle.setImageUrl(headline.getUrlToImage());
                            Date date = formatter.parse(headline.getPublishedAt().replaceAll("Z$", "+0000"));
                            newsArticle.setPublishedTime(date);
                            newsArticle.setUrl(headline.getUrl());
                            newsArticle.setCategory("Headlines");
                            newsArticle.setContent(summary);
                            newsArticles.add(newsArticle);
                            newsArticleRepository.save(newsArticle);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            return newsArticles;
        } else {
            return null;
        }
    }

    @GetMapping("/newsarticles")
    public List<NewsArticle> getLatestNewsArticles(@RequestParam int page){
        Pageable pageable =
                PageRequest.of(page, 5, Sort.by("publishedTime"));

        Page<NewsArticle> topPage = newsArticleRepository.findAllByOrderByPublishedTimeDesc(pageable);
        List<NewsArticle> topArticles = topPage.getContent();
        return topArticles;
    }

    @GetMapping("/scrape")
    public String testScrape(){
        String url4 = "https://www.channelnewsasia.com/news/asia/hong-kong-protests-police-arrest-more-than-200-12721380";
        String source = "Channelnewsasia.com";
        String content =  headlineScrapper.scrapeContent(url4,source);
        return content;
    }

    @GetMapping("/headline/summarize")
    public String getLatestHeadlines(@RequestParam String id) {
        HeadlineDao headline = headlineRepository.findById(Long.parseLong(id)).orElse(null);
        if (headline != null) {
            String content = headlineScrapper.scrapeContent(headline.getUrl(), headline.getSource());
            if (!content.equals("failed") && !content.equals("")) {
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

    private void saveHeadlines(Headlines headlines) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        List<Headline> articles = headlines.getArticles();
        articles.forEach(article -> {
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
