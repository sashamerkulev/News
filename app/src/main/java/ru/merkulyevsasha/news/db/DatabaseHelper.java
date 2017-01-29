package ru.merkulyevsasha.news.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.firebase.crash.FirebaseCrash;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.merkulyevsasha.news.models.ItemNews;

public class DatabaseHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "news.db";
    private static final String TABLE_NAME = "news";
    private static final String FOLDER_NAME = "news";

    private static final String SOURCE_NAV_ID = "sourceNavId";
    private static final String TITLE = "title";
    private static final String LINK = "link";
    private static final String DESCRIPTION = "description";
    private static final String PUBDATE = "pubDate";
    private static final String CATEGORY = "category";
    private static final String SEARCH = "search";

    private static final String DATABASE_CREATE = "create table " + TABLE_NAME + " ( " +
            SOURCE_NAV_ID + " long, " +
            TITLE + " string, " +
            LINK + " string, " +
            DESCRIPTION + " string, " +
            PUBDATE + " long, " +
            CATEGORY + " string, " +
            SEARCH + " string " +
            " ); create index search_index on " + TABLE_NAME + "(" + SEARCH + "); create index pubdate_index on" + TABLE_NAME + "(" + PUBDATE + ")";

    private SQLiteDatabase mSqlite;
    private final String mDbPath;

    // https://habrahabr.ru/post/27108/
    private static volatile DatabaseHelper mInstance;

    public static DatabaseHelper getInstance(final String dbPath) {
        if (mInstance == null) {
            synchronized (DatabaseHelper.class) {
                if (mInstance == null) {
                    mInstance = new DatabaseHelper(dbPath);
                }
            }
        }
        return mInstance;
    }

    public static String getDbPath(Context context){
        File subFolder = new File(context.getFilesDir(), FOLDER_NAME);
        subFolder.mkdirs();
        File mFile = new File(subFolder, DATABASE_NAME);
        return mFile.getPath();
    }

    private SQLiteDatabase openOrCreateDatabase() {
        return SQLiteDatabase.openOrCreateDatabase(mDbPath, null);
    }

    private DatabaseHelper(final String dbPath) {
        mDbPath = dbPath;
        mSqlite = openOrCreateDatabase();
        if (mSqlite !=null && mSqlite.getVersion() == 0) {
            mSqlite.execSQL(DATABASE_CREATE);
            mSqlite.setVersion(DATABASE_VERSION);
            mSqlite.close();
        }
    }

    public void addListNews(List<ItemNews> items) {
        try {
            mSqlite = openOrCreateDatabase();
            if (mSqlite != null) {
                mSqlite.beginTransaction();
                for (ItemNews item : items) {
                    ContentValues values = new ContentValues();
                    values.put(SOURCE_NAV_ID, item.SourceNavId);
                    values.put(TITLE, item.Title);
                    values.put(LINK, item.Link);
                    values.put(DESCRIPTION, item.Description == null ? "" : item.Description);
                    values.put(CATEGORY, item.Category);
                    values.put(SEARCH, item.Search);
                    if (item.PubDate != null) {
                        values.put(PUBDATE, item.PubDate.getTime());
                        mSqlite.insert(TABLE_NAME, null, values);
                    }
                }
                mSqlite.setTransactionSuccessful();
            }
        } catch (Exception e) {
            FirebaseCrash.report(e);
            e.printStackTrace();
        } finally {
            if (mSqlite != null && mSqlite.inTransaction())
                mSqlite.endTransaction();
            if (mSqlite != null && mSqlite.isOpen())
                mSqlite.close();
        }
    }

    public void deleteAll() {
        try {
            mSqlite = openOrCreateDatabase();
            if (mSqlite != null) {
                mSqlite.delete(TABLE_NAME, null, null);
            }
        } catch (Exception e) {
            FirebaseCrash.report(e);
            e.printStackTrace();
        } finally {
            if (mSqlite != null && mSqlite.isOpen())
                mSqlite.close();
        }
    }

    public void delete(int navId) {
        try {
            mSqlite = openOrCreateDatabase();
            if (mSqlite != null) {
                mSqlite.delete(TABLE_NAME, SOURCE_NAV_ID + " = ?", new String[]{String.valueOf(navId)});
            }
        } catch (Exception e) {
            FirebaseCrash.report(e);
            e.printStackTrace();
        } finally {
            if (mSqlite != null && mSqlite.isOpen())
                mSqlite.close();
        }
    }

    private ItemNews getItemNews(Cursor cursor) {
        ItemNews item = new ItemNews();
        item.SourceNavId = cursor.getInt(cursor.getColumnIndex(SOURCE_NAV_ID));
        item.Title = cursor.getString(cursor.getColumnIndex(TITLE));
        item.Link = cursor.getString(cursor.getColumnIndex(LINK));
        item.Description = cursor.getString(cursor.getColumnIndex(DESCRIPTION));
        item.PubDate = new Date(cursor.getLong(cursor.getColumnIndex(PUBDATE)));
        item.Category = cursor.getString(cursor.getColumnIndex(CATEGORY));
        item.Search = cursor.getString(cursor.getColumnIndex(SEARCH));
        return item;
    }

    public List<ItemNews> selectAll() {
        List<ItemNews> items = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_NAME + " order by " + PUBDATE + " desc";

        try {
            mSqlite = openOrCreateDatabase();
            if (mSqlite != null) {

                Cursor cursor = mSqlite.rawQuery(selectQuery, null);

                if (cursor.moveToFirst()) {
                    do {
                        ItemNews item = getItemNews(cursor);
                        items.add(item);
                    } while (cursor.moveToNext());
                }
            }
        } catch (Exception e) {
            FirebaseCrash.report(e);
            e.printStackTrace();
        } finally {
            if (mSqlite != null && mSqlite.isOpen())
                mSqlite.close();
        }
        return items;
    }

    public List<ItemNews> select(int navId) {
        List<ItemNews> items = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_NAME + " where " + SOURCE_NAV_ID + "=? order by " + PUBDATE + " desc";

        try {
            mSqlite = openOrCreateDatabase();
            if (mSqlite != null) {
                Cursor cursor = mSqlite.rawQuery(selectQuery, new String[]{String.valueOf(navId)});

                if (cursor.moveToFirst()) {
                    do {
                        ItemNews item = getItemNews(cursor);
                        items.add(item);
                    } while (cursor.moveToNext());
                }
            }
        } catch (Exception e) {
            FirebaseCrash.report(e);
            e.printStackTrace();
        } finally {
            if (mSqlite != null && mSqlite.isOpen())
                mSqlite.close();
        }

        return items;
    }

    public List<ItemNews> query(String searchText) {
        List<ItemNews> items = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_NAME + " where " + SEARCH + " like @search";

        try {
            mSqlite = openOrCreateDatabase();
            if (mSqlite != null) {
                Cursor cursor = mSqlite.rawQuery(selectQuery, new String[]{"%" + searchText.toLowerCase() + "%"});

                if (cursor.moveToFirst()) {
                    do {
                        ItemNews item = getItemNews(cursor);
                        items.add(item);
                    } while (cursor.moveToNext());
                }
            }
        } catch (Exception e) {
            FirebaseCrash.report(e);
            e.printStackTrace();
        } finally {
            if (mSqlite != null && mSqlite.isOpen())
                mSqlite.close();
        }
        return items;
    }

}
