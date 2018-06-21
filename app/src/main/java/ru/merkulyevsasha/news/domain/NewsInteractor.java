package ru.merkulyevsasha.news.domain;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

import ru.merkulyevsasha.news.R;
import ru.merkulyevsasha.news.data.db.NewsDbRepository;
import ru.merkulyevsasha.news.data.http.HttpReader;
import ru.merkulyevsasha.news.data.prefs.NewsSharedPreferences;
import ru.merkulyevsasha.news.data.utils.NewsConstants;
import ru.merkulyevsasha.news.helpers.BroadcastHelper;
import ru.merkulyevsasha.news.pojos.Article;

public class NewsInteractor {

    private final HttpReader reader;
    private final NewsDbRepository db;
    private final NewsSharedPreferences prefs;
    private final NewsConstants newsConstants;
    private final BroadcastHelper broadcastHelper;

    public NewsInteractor(NewsConstants newsConstants, HttpReader reader, NewsSharedPreferences prefs, NewsDbRepository db, BroadcastHelper broadcastHelper) {
        this.reader = reader;
        this.db = db;
        this.prefs = prefs;
        this.newsConstants = newsConstants;
        this.broadcastHelper = broadcastHelper;
    }

    public Single<List<Article>> readNewsAndSaveToDb(int navId) {
        return Single.fromCallable(() -> {
            List<Article> items = new ArrayList<>();

            if (navId == R.id.nav_all) {
                for (Map.Entry<Integer, String> entry : newsConstants.getLinks().entrySet()) {
                    try {
                        List<Article> articles = getArticles(entry.getKey(), newsConstants.getLinkByNavId(entry.getKey()));
                        updaDbAndSendNotification(articles, entry.getKey());
                        items.addAll(articles);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                items = getArticles(navId, newsConstants.getLinkByNavId(navId));
                updaDbAndSendNotification(items, navId);
            }
            broadcastHelper.sendFinishBroadcast();

            return items;
        })
                .flattenAsFlowable(t -> t)
                .sorted((article, t1) -> t1.getPubDate().compareTo(article.getPubDate()))
                .toList()
                .cache()
                .subscribeOn(Schedulers.io());
    }

    public Single<Boolean> getFirstRunFlag() {
        return prefs
                .getFirstRunFlag()
                .subscribeOn(Schedulers.io());
    }

    public void setFirstRunFlag() {
        prefs.setFirstRunFlag();
    }

    public Single<List<Article>> selectAll() {
        return db.selectAll()
                .subscribeOn(Schedulers.io());
    }

    public Single<List<Article>> selectNavId(int navId) {
        return db.selectNavId(navId)
                .subscribeOn(Schedulers.io());
    }

    public Single<List<Article>> search(String searchTtext) {
        return db.search(searchTtext)
                .subscribeOn(Schedulers.io());
    }

    public boolean needUpdate() {
        Date lastpubDate = db.getLastPubDate();
        if (lastpubDate == null) return true;

        Date nowdate = new Date();
        int diffMinutes = (int) ((nowdate.getTime() / 60000) - (lastpubDate.getTime() / 60000));
        return diffMinutes > 30;
    }

    private List<Article> getArticles(int id, String url) {
        List<Article> items = new ArrayList<>();
        try {
            items = reader.GetHttpData(id, url);
            if (items.size() > 0) {
                db.delete(id);
                db.addListNews(items);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }

    private void updaDbAndSendNotification(List<Article> articles, Integer key) {
        db.delete(key);
        db.addListNews(articles);
        broadcastHelper.sendUpdateBroadcast();
    }
}
