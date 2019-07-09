
# News

Проект для Android (реализован на Kotlin): новости из публичных rss источников, которые собираются на [беке](https://github.com/sashamerkulev/rssservice).

### Архитектура MVP + Clean
- Single activity
- MVP - как можно более пассивная вьюха, которой, управляет презентер, он же взаимодействует с моделью;
- модель реализована по мотивам Clean Architecture, есть интерактор, взаимодействующий с репозиторием, который, в свою очередь взаимодействует с БД и сетью, о чем интерактор не знает и не хочет знать.

### OkHttp3
- Для чтения rss-файлов с новостями используется OkHttp3.

### Room
- используется для работы с локальной бд.

### RxJava2
- используется для работы с бд и сетью.

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
