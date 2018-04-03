package ru.merkulyevsasha.news.domain;


import com.google.firebase.crash.FirebaseCrash;

import java.util.List;

import ru.merkulyevsasha.news.data.db.DatabaseHelper;
import ru.merkulyevsasha.news.data.http.HttpReader;
import ru.merkulyevsasha.news.pojos.ItemNews;

/**
 * Created by sasha_merkulev on 22.09.2017.
 */

public class NewsInteractor {

    private final HttpReader reader;
    private final DatabaseHelper db;

    public NewsInteractor(HttpReader reader, DatabaseHelper db){
        this.reader = reader;
        this.db = db;
    }

    public boolean readNewsAndSaveToDb(int id, String url){
        boolean result = false;
        try {
            List<ItemNews> items = reader.GetHttpData(id, url);
            if (items.size() > 0) {
                db.delete(id);
                db.addListNews(items);
                result = true;
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }

}
