package com.summary.api.dao;

import javax.persistence.*;
import java.util.Date;

@Entity
public class HeadlineDao {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String source;
    private String url;
    @Lob
    private String imageUrl;
    private Date publishedTime;

    public HeadlineDao() {

    }

    public HeadlineDao(String source, String description, String url, String imageUrl, Date publishedTime) {
        this.source = source;
        this.url = url;
        this.imageUrl = imageUrl;
        this.publishedTime = publishedTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Date getPublishedTime() {
        return publishedTime;
    }

    public void setPublishedTime(Date publishedTime) {
        this.publishedTime = publishedTime;
    }
}
