package ru.merkulyevsasha.news.data.http

import ru.merkulyevsasha.news.models.Article

interface HttpDataSource {
    fun getHttpData(navId: Int, url: String): List<Article>
}
