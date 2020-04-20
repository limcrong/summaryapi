package com.summary.api.repository;

import org.springframework.context.annotation.Configuration;

import java.util.logging.Logger;

@Configuration
public class LoadDatabase {

    private static final Logger log = Logger.getLogger(LoadDatabase.class.getName());

//    @Bean
//    CommandLineRunner initDatabase(ArticleRepository repository){
//        return args -> {
//            Article article = new Article();
//            article.setContent("Test Content");
//            article.setCategory("Test");
//            Article article2 = new Article();
//            article2.setContent("Test Content2");
//            article2.setCategory("Test");
//            log.info("Preloading "+ repository.save(article));
//            log.info("Preloading "+ repository.save(article2));
//        };
//    }
}
