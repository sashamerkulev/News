
# News

Простенький проект на Android Java: новости из публичных rss источников, проект содержит две Activity.

https://play.google.com/store/apps/details?id=ru.merkulyevsasha.news

Первая Activity отображает список новостей (android.support.v7.widget.RecyclerView).
Реализован поиск по БД Sqlite android.support.v7.widget.SearchView. А с помощью android.support.v4.widget.SwipeRefreshLayout реализован pull-to-refresh.

Вторая отображает новость более подробно, по ссылке (WebView). 

Переход между активити анимируется методом overridePendingTransition.

### Service, OkHttp3, BroadcastReceiver

Для работы в фоне (чтения новостей из сети) используется Service. 

Для чтения rss-файлов с новостями используется OkHttp3.

Для сообщения об окончании работы сервис посылает broadcast-сообщения, которые активити перехватывает в своем локальном BroadcastReceiver.

### SQLiteDatabase (транзакции)

Данные хранятся в БД Sqlite (используется класс SQLiteDatabase).

Для чтения данных из БД используется AsyncTask :-)

А вот как надо работать с транзакциями:
```java
SQLiteDatabase mSqlite = SQLiteDatabase.openOrCreateDatabase();
try {
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
  
### Удобный Butterknife

А еще используется вот такая штука:
```java
    @Bind(R.id.fab) public FloatingActionButton mFab;
```  
  хотя, пожалуй в этом проекте, она применяется даже там где не нужно.
  
  подключить ее можно так: 
```java  
    compile 'com.jakewharton:butterknife:8.5.1'
    annotationProcessor 'com.jakewharton:butterknife-compiler:8.5.1'
```
### Сохранение состояния (Icepick)

Есть библиотека для автоматического сохранения/восстановления состояний активити/фрагментов в bundle.

```java
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Icepick.saveInstanceState(this, outState);
    }
```
Восстановления состояния происходит так:
```java
    Icepick.restoreInstanceState(this, savedInstanceState);
```

Для того, что бы все работало, поля состояние которых нужно сохранить помечаются аннотацией:
```java
    @State int mNavId;
```  
Подключается так:
```java  
    compile 'frankiesardo:icepick:3.2.0'
    provided 'frankiesardo:icepick-processor:3.2.0'
```
