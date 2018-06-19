package ru.merkulyevsasha.news.presentation.main;

import java.util.List;

import ru.merkulyevsasha.news.pojos.Article;

interface MainView {

    void prepareToSearch();

    void showDetailScreen(Article item);

    void showProgress();
    void hideProgress();

    void showItems(List<Article> result);
    void showNoSearchResultMessage();
    void showMessageError();

    void scheduleJob();

}
