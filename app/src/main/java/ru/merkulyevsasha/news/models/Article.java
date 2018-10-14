package ru.merkulyevsasha.news.models;

import org.jetbrains.annotations.Nullable;

import java.util.Date;

import merkulyevsasha.ru.annotations.Mapper;
import merkulyevsasha.ru.annotations.params.Source;
import ru.merkulyevsasha.news.data.db.entities.ArticleEntity;

@Mapper(source = Source.Kotlin, twoWayMapClasses = ArticleEntity.class)
public class Article {

    private int id;
    private int sourceNavId;
    private String title;
    private String link;
    @Nullable private String description;
    private Date pubDate;
    @Nullable private String category;
    private String search;
    @Nullable private String pictureUrl;

    public Article() {
    }

    public Article(int id, int sourceNavId, String title, String link, @Nullable String description, Date pubDate, @Nullable String category, String search, @Nullable String pictureUrl) {
        this.id = id;
        this.sourceNavId = sourceNavId;
        this.title = title;
        this.link = link;
        this.description = description;
        this.pubDate = pubDate;
        this.category = category;
        this.search = search;
        this.pictureUrl = pictureUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSourceNavId() {
        return sourceNavId;
    }

    public void setSourceNavId(int sourceNavId) {
        this.sourceNavId = sourceNavId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getPubDate() {
        return pubDate;
    }

    public void setPubDate(Date pubDate) {
        this.pubDate = pubDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }
}
