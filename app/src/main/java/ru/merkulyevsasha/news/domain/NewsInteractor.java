package ru.merkulyevsasha.news.domain;

import java.util.List;

import io.reactivex.Single;
import ru.merkulyevsasha.news.pojos.Article;

public interface NewsInteractor {
    Single<List<Article>> readNewsAndSaveToDb(int navId);

    Single<Boolean> getFirstRunFlag();

    void setFirstRunFlag();

    Single<List<Article>> selectAll();

    Single<List<Article>> selectNavId(int navId);

    Single<List<Article>> search(String searchTtext);

    boolean needUpdate();
}
