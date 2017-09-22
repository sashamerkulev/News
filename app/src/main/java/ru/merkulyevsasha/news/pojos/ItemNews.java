package ru.merkulyevsasha.news.pojos;

import java.util.Date;

public class ItemNews {

    private int id;
    private int sourceNavId;
    private String title;
    private String link;
    private String description;
    private Date pubDate;
    private String category;
    private String search;

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
}
