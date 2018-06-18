package ru.merkulyevsasha.news.data.db;

import java.util.List;

import io.reactivex.Single;
import ru.merkulyevsasha.news.pojos.Article;

public interface NewsDbRepository {

    void addListNews(List<Article> items);

    void delete(int navId);

    void deleteAll();

    Single<List<Article>> selectAll();

    Single<List<Article>> selectNavId(int navId);

    Single<List<Article>> search(String searchTtext);

}
