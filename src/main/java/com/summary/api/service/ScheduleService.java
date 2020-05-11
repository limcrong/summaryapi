package com.summary.api.service;

import com.summary.api.consumer.HeadlineScrapper;
import com.summary.api.consumer.HeadlinesConsumer;
import com.summary.api.consumer.SummaryConsumer;
import com.summary.api.consumer.scrappers.ScrapperReference;
import com.summary.api.dao.NewsArticle;
import com.summary.api.domain.Headline;
import com.summary.api.domain.Headlines;
import com.summary.api.repository.NewsArticleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
public class ScheduleService {

    private static final Logger log = LoggerFactory.getLogger(ScheduleService.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    private final NewsArticleRepository newsArticleRepository;

    private final HeadlinesConsumer headlinesConsumer;

    private final ScrapperReference scrapperReference;

    private final HeadlineScrapper headlineScrapper;

    private final SummaryConsumer summaryConsumer;

    public ScheduleService(NewsArticleRepository newsArticleRepository, HeadlinesConsumer headlinesConsumer, ScrapperReference scrapperReference, HeadlineScrapper headlineScrapper, SummaryConsumer summaryConsumer) {
        this.newsArticleRepository = newsArticleRepository;
        this.headlinesConsumer = headlinesConsumer;
        this.scrapperReference = scrapperReference;
        this.headlineScrapper = headlineScrapper;
        this.summaryConsumer = summaryConsumer;
    }

    @Scheduled(fixedRate = 1800000)
    public void processNewArticles() {
        log.info("The time is now {}", dateFormat.format(new Date()));
        log.info("Fetching articles now..");
        Headlines headlines = headlinesConsumer.getHeadlines();
        log.info("Fetched " + headlines);
        List<String> supportedSources = scrapperReference.getSupportedSources();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        List<NewsArticle> newsArticles = new ArrayList<>();
        if (headlines != null) {
            List<Headline> headlineList = headlines.getArticles();
            log.info("Processing each article.. total = "+headlineList.size());
//            headlineList.forEach(headline -> {
            for(Headline headline : headlineList){
                if (supportedSources.contains(headline.getSource().getName())) {
                    log.info("<Processing> Article {} source is supported: {}",headlineList.indexOf(headline),headline.getUrl());
                    Optional<NewsArticle> existingArticle = newsArticleRepository.findByUrl(headline.getUrl());
                    if (existingArticle.isPresent()) {
                        log.info("Article exists already.. <Processed>");
                        continue;
                    }
                    log.info("New Article confirmed..");
                    try {
                        log.info("Waiting for 3 seconds cooldown before scraping...");
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        log.info("Thread interrupted..");
                        e.printStackTrace();
                    }
                    log.info("Scraping content from: " + headline.getSource().getName() + " at url: " + headline.getUrl());
                    String content = headlineScrapper.scrapeContent(headline.getUrl(), headline.getSource().getName());
                    log.info("Got content: " + content);
                    if (!content.equals("failed") && !content.equals("")) {
                        log.info("Summarizing content");
                        String summary = summaryConsumer.getSummary(content);
//                        if(summary == null){
//                            return;
//                        }
                        log.info("Got summary: " + summary);
                        try {
                            log.info("Saving news article now..");
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
                            log.info("Article saved <Processed>");
                        } catch (ParseException e) {
                            log.info("ParseException..");
                            e.printStackTrace();
                            continue;
                        } catch (DataIntegrityViolationException e) {
                            log.info("DataIntegrity Exception..");
                            e.printStackTrace();
                            continue;
                        } catch (Exception e) {
                            log.info("General Exception..");
                            e.printStackTrace();
                            continue;
                        }
                    }else {
                        log.info("Failed to scrap from.. {} <Processed>",headline.getUrl());
                    }
                }else {
                    log.info("<Processing> Article source is not supported: "+headline.getUrl()+"<Processed>");
                }
            };
        }
    }
}
