
# News

Простенький проект для Android (реализован на Java/Kotlin): новости из публичных rss источников.

https://play.google.com/store/apps/details?id=ru.merkulyevsasha.news

- Главная Activity отображает список новостей (android.support.v7.widget.RecyclerView).
- Статьи сохраняются в БД (для работы с Sqlite используется Room), и есть поиск по сохраненным данным, используется android.support.v7.widget.SearchView.
- А с помощью android.support.v4.widget.SwipeRefreshLayout реализован pull-to-refresh (впрочем, кнопка обновить присутствует и в ActionToolBar).

Новость более подробно, по ссылке, отображается с помощью CustomTabsIntent. 

### Архитектура MVP + Clean 
- MVP - как можно более пассивная вьюха, которой, управляет презентер, он же взаимодействует с моделью;
- модель реализована по мотивам Clean Architecture, есть интерактор, взаимодействующий с репозиторием, который, в свою очередь взаимодействует с БД и сетью, о чем интерактор не знает и не хочет знать.
- для маппига используется [MapperJ](https://github.com/sashamerkulev/MapperJ)

### OkHttp3
- Для чтения rss-файлов с новостями используется OkHttp3.

### Room
- используется для работы с локальной бд.

### RxJava2
- используется для работы с бд и сетью.

### Dagger2
- используется для внедрения зависимостей

### WorkerManager
- запускается раз в час и обновляет данные, если дата последней новости отстает от текущей даты более чем 30 минут

### Glide
- используется для отображения картинок

License
-------

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
