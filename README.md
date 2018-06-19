
# News

Простенький проект для Android (реализован на Java): новости из публичных rss источников.

https://play.google.com/store/apps/details?id=ru.merkulyevsasha.news

Главная Activity отображает список новостей (android.support.v7.widget.RecyclerView).
Статьи сохраняются в БД, и есть поиск по сохраненным данным, используется android.support.v7.widget.SearchView.
А с помощью android.support.v4.widget.SwipeRefreshLayout реализован pull-to-refresh (впрочем кнопка обновить присутствует в ActionToolBar).

Новость более подробно, по ссылке, отображается с помощью CustomTabsIntent. 

### Архитектура MVP + Clean 


### OkHttp3, BroadcastReceiver

Для чтения rss-файлов с новостями используется OkHttp3.

Для сообщения об окончании работы сервис посылает broadcast-сообщения, которые активити перехватывает в своем локальном BroadcastReceiver.

### Room
- используется для работы с локальной бд.

### RxJava2
- используется для работы с бд и сетью.

### Dagger2

```java
    @Inject MainPresenter pres;
```  

### Android-Job от evernote
- есть джоба которая запускается раз в час и обновляет данные, если дата последней новости отстает от текущей даты более чем 30 минут

### Butterknife

А еще используется вот такая штука:
```java
    @BindView(R.id.refreshLayout) SwipeRefreshLayout refreshLayout;
```  
### Glide

