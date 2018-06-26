package ru.merkulyevsasha.news.data.http;

import java.util.List;

import ru.merkulyevsasha.news.pojos.Article;

public interface HttpReader {
    List<Article> GetHttpData(int navId, String url);
}
