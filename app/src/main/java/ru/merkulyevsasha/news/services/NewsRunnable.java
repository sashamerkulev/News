package ru.merkulyevsasha.news.services;

import android.content.Context;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;

import ru.merkulyevsasha.news.data.db.DatabaseHelper;
import ru.merkulyevsasha.news.data.http.HttpReader;
import ru.merkulyevsasha.news.helpers.Const;
import ru.merkulyevsasha.news.pojos.ItemNews;


public class NewsRunnable implements Runnable{

    private DatabaseHelper mHelper;
    private HttpReader reader;

    private WeakReference<Context> context;

    public NewsRunnable(Context context){
        this.context = new WeakReference<Context>(context);
    }

    @Override
    public void run() {

        Context cont = context.get();
        if (cont == null)
            return;

        mHelper = DatabaseHelper.getInstance(DatabaseHelper.getDbPath(cont));
        reader = new HttpReader();

        final Const mConst = new Const();
        for (Map.Entry<Integer, String> entry : mConst.getLinks().entrySet()) {
            readHttpDataAndSaveToDb(entry.getKey(), entry.getValue());
        }

        ServicesHelper.setNotification(cont, "Есть свежие новости!");
    }

    private void readHttpDataAndSaveToDb(int id, String url){
        try {
            List<ItemNews> items = reader.GetHttpData(id, url);
            if (items.size() > 0) {
                mHelper.delete(id);
                mHelper.addListNews(items);
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

}
