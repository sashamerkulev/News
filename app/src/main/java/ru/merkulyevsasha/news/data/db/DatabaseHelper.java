package ru.merkulyevsasha.news.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.firebase.crash.FirebaseCrash;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.merkulyevsasha.news.pojos.ItemNews;

public class DatabaseHelper extends SQLiteOpenHelper{
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "news.db";
    private static final String TABLE_NAME = "news";

    private static final String SOURCE_NAV_ID = "sourceNavId";
    private static final String TITLE = "title";
    private static final String LINK = "link";
    private static final String DESCRIPTION = "description";
    private static final String PUBDATE = "pubDate";
    private static final String CATEGORY = "category";
    private static final String THUMB = "picture_url";
    private static final String SEARCH = "search";

    private static final String DATABASE_CREATE = "create table " + TABLE_NAME + " ( " +
            SOURCE_NAV_ID + " long, " +
            TITLE + " string, " +
            LINK + " string, " +
            DESCRIPTION + " string, " +
            PUBDATE + " long, " +
            CATEGORY + " string, " +
            THUMB + " string, " +
            SEARCH + " string " +
            " ); create index search_index on " + TABLE_NAME + "(" + SEARCH + "); create index pubdate_index on" + TABLE_NAME + "(" + PUBDATE + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void addListNews(List<ItemNews> items) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.beginTransaction();
            for (ItemNews item : items) {
                ContentValues values = new ContentValues();
                values.put(SOURCE_NAV_ID, item.getSourceNavId());
                values.put(TITLE, item.getTitle());
                values.put(LINK, item.getLink());
                values.put(DESCRIPTION, item.getDescription() == null ? "" : item.getDescription());
                values.put(CATEGORY, item.getCategory());
                values.put(THUMB, item.getPictureUrl());
                values.put(SEARCH, item.getSearch());
                if (item.getPubDate() != null) {
                    values.put(PUBDATE, item.getPubDate().getTime());
                    db.insert(TABLE_NAME, null, values);
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            FirebaseCrash.report(e);
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    public void delete(int navId) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.delete(TABLE_NAME, SOURCE_NAV_ID + " = ?", new String[]{String.valueOf(navId)});
        } catch (Exception e) {
            FirebaseCrash.report(e);
            e.printStackTrace();
        }
    }

    private ItemNews getItemNews(Cursor cursor) {
        ItemNews item = new ItemNews();
        item.setSourceNavId(cursor.getInt(cursor.getColumnIndex(SOURCE_NAV_ID)));
        item.setTitle(cursor.getString(cursor.getColumnIndex(TITLE)));
        item.setLink(cursor.getString(cursor.getColumnIndex(LINK)));
        item.setDescription(cursor.getString(cursor.getColumnIndex(DESCRIPTION)));
        item.setPubDate(new Date(cursor.getLong(cursor.getColumnIndex(PUBDATE))));
        item.setCategory(cursor.getString(cursor.getColumnIndex(CATEGORY)));
        item.setSearch(cursor.getString(cursor.getColumnIndex(SEARCH)));
        item.setPictureUrl(cursor.getString(cursor.getColumnIndex(THUMB)));
        return item;
    }

    public List<ItemNews> selectAll() {
        List<ItemNews> items = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_NAME + " order by " + PUBDATE + " desc";

        SQLiteDatabase db = getReadableDatabase();
        try {

                Cursor cursor = db.rawQuery(selectQuery, null);

                if (cursor.moveToFirst()) {
                    do {
                        ItemNews item = getItemNews(cursor);
                        items.add(item);
                    } while (cursor.moveToNext());
                }
        } catch (Exception e) {
            FirebaseCrash.report(e);
            e.printStackTrace();
        }
        return items;
    }

    public List<ItemNews> select(int navId) {
        List<ItemNews> items = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_NAME + " where " + SOURCE_NAV_ID + "=? order by " + PUBDATE + " desc";

        SQLiteDatabase db = getReadableDatabase();
        try {
                Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(navId)});

                if (cursor.moveToFirst()) {
                    do {
                        ItemNews item = getItemNews(cursor);
                        items.add(item);
                    } while (cursor.moveToNext());
                }
        } catch (Exception e) {
            FirebaseCrash.report(e);
            e.printStackTrace();
        }

        return items;
    }

    public List<ItemNews> query(String searchText) {
        List<ItemNews> items = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_NAME + " where " + SEARCH + " like @search order by " + PUBDATE + " desc";

        SQLiteDatabase db = getReadableDatabase();
        try {
                Cursor cursor = db.rawQuery(selectQuery, new String[]{"%" + searchText.toLowerCase() + "%"});

                if (cursor.moveToFirst()) {
                    do {
                        ItemNews item = getItemNews(cursor);
                        items.add(item);
                    } while (cursor.moveToNext());
                }
        } catch (Exception e) {
            FirebaseCrash.report(e);
            e.printStackTrace();
        }
        return items;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 1) {
            String addingThumbColumn = "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + THUMB + " STRING";
            db.execSQL(addingThumbColumn);
        }

    }
}
