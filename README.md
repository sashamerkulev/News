# News

Простенький проект на Android Java: новости из публичных rss источников, проект содержит две Activity.

Первая Activity отображает список новостей (android.support.v7.widget.RecyclerView).
Реализован поиск по БД Sqlite android.support.v7.widget.SearchView. А с помощью android.support.v4.widget.SwipeRefreshLayout реализован pull-to-refresh.

Вторая отображает новость более подробно, по ссылке (WebView). 

Для чтения rss из сети используется org.apache.http.

Для работы в фоне (чтения из сети) используется AsyncTaskLoader<>.

Данные кешируются в БД Sqlite (используется класс SQLiteDatabase).

