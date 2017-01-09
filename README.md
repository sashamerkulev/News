
# News

Простенький проект на Android Java: новости из публичных rss источников, проект содержит две Activity.

Первая Activity отображает список новостей (android.support.v7.widget.RecyclerView).
Реализован поиск по БД Sqlite android.support.v7.widget.SearchView. А с помощью android.support.v4.widget.SwipeRefreshLayout реализован pull-to-refresh.

Вторая отображает новость более подробно, по ссылке (WebView). 

Для чтения rss из сети используется org.apache.http.

Для работы в фоне (чтения из сети) используется Service. Для сообщения об окончании создавшей сервис Activity используется PendingIntent.

Данные кешируются в БД Sqlite (используется класс SQLiteDatabase).

### SQLiteDatabase (транзакции)
А вот как надо работать с транзакциями:
```java
try {
  mSqlite = SQLiteDatabase.openOrCreateDatabase();
  mSqlite.beginTransaction();
  for (ItemNews item : items) {
    mSqlite.insert(TABLE_NAME, null, values);
  }
  mSqlite.setTransactionSuccessful();
} finally {
    if (mSqlite != null && mSqlite.inTransaction())
      mSqlite.endTransaction();
    if (mSqlite != null && mSqlite.isOpen())
      mSqlite.close();
}
```  
  
### Удобный Binding
А еще используется вот такая штука:
```java
    @Bind(R.id.fab)
    public FloatingActionButton mFab;
```  
  хотя, пожалуй в этом проекте, она применяется даже там где не нужно.
  
  подключить ее можно так: 
  
  compile 'com.jakewharton:butterknife:7.0.1'
