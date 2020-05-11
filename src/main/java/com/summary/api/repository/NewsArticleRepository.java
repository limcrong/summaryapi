package com.summary.api.repository;

import com.summary.api.dao.NewsArticle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NewsArticleRepository extends PagingAndSortingRepository<NewsArticle, Long> {
    Page<NewsArticle> findAllByOrderByPublishedTimeDesc(Pageable pageable);

    Optional<NewsArticle> findByUrl(String url);
}
