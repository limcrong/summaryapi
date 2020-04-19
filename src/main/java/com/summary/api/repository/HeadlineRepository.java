package com.summary.api.repository;

import com.summary.api.dao.HeadlineDao;
import org.springframework.data.repository.CrudRepository;

public interface HeadlineRepository extends CrudRepository<HeadlineDao, Long> {

}
