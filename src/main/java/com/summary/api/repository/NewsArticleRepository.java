package com.summary.api.repository;

import com.summary.api.dao.NewsArticle;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsArticleRepository extends CrudRepository<NewsArticle, Long> {
}
