package ru.merkulyevsasha.news.data.db.mappers;

import ru.merkulyevsasha.news.data.Mapper;
import ru.merkulyevsasha.news.data.db.entities.ArticleEntity;
import ru.merkulyevsasha.news.pojos.Article;

public class ArticleMapper implements Mapper<Article, ArticleEntity> {
    @Override
    public ArticleEntity map(Article item) {
        ArticleEntity entity = new ArticleEntity();
        entity.setCategory(item.getCategory());
        entity.setDescription(item.getDescription());
        entity.setLink(item.getLink());
        entity.setPictureUrl(item.getPictureUrl());
        entity.setPubDate(item.getPubDate());
        entity.setSourceNavId(item.getSourceNavId());
        entity.setTitle(item.getTitle());
        entity.setSearch(item.getSearch());
        return entity;
    }
}
