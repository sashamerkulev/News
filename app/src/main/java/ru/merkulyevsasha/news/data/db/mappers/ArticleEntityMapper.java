package ru.merkulyevsasha.news.data.db.mappers;

import ru.merkulyevsasha.news.data.Mapper;
import ru.merkulyevsasha.news.data.db.entities.ArticleEntity;
import ru.merkulyevsasha.news.pojos.Article;

public class ArticleEntityMapper implements Mapper<ArticleEntity, Article> {
    @Override
    public Article map(ArticleEntity item) {
        return new Article(item.getId(), item.getSourceNavId(), item.getTitle(), item.getLink()
                , item.getDescription(), item.getPubDate(), item.getCategory(), item.getSearch(), item.getPictureUrl());
    }
}
