package ru.merkulyevsasha.news.data.db;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;
import ru.merkulyevsasha.news.data.ListUtils;
import ru.merkulyevsasha.news.pojos.Article;

public class NewsDbRepositoryImpl implements NewsDbRepository {

    private final NewsDbRoom room;

    private final ArticleMapper articleMapper;
    private final ArticleEntityMapper articleEntityMapper;

    @Inject
    public NewsDbRepositoryImpl(NewsDbRoom room) {
        this.room = room;
        articleMapper = new ArticleMapper();
        articleEntityMapper = new ArticleEntityMapper();
    }

    @Override
    public void addListNews(List<Article> items) {
        ArticleDao dao = room.getArticleDao();
        dao.addListNews(ListUtils.convertToArray(items, articleMapper, ArticleEntity.class));
    }

    @Override
    public void delete(final int navId) {
        ArticleDao dao = room.getArticleDao();
        dao.delete(navId);
    }

    @Override
    public void deleteAll() {
        ArticleDao dao = room.getArticleDao();
        dao.deleteAll();
    }

    @Override
    public Single<List<Article>> selectAll() {
        ArticleDao dao = room.getArticleDao();
        return dao.selectAll().flattenAsFlowable(t -> t).map(articleEntityMapper::map).toList();
    }

    @Override
    public Single<List<Article>> selectNavId(int navId) {
        ArticleDao dao = room.getArticleDao();
        return dao.selectNavId(navId).flattenAsFlowable(t -> t).map(articleEntityMapper::map).toList();
    }

    @Override
    public Single<List<Article>> search(String searchTtext) {
        ArticleDao dao = room.getArticleDao();
        return dao.search(searchTtext).flattenAsFlowable(t -> t).map(articleEntityMapper::map).toList();
    }

    @Override
    public Date getLastPubDate() {
        ArticleDao dao = room.getArticleDao();
        ArticleEntity article = dao.getLastArticle();
        return article == null? null : article.getPubDate();
    }

}
