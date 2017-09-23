package ru.merkulyevsasha.news.presentation.main;

import java.util.List;

import ru.merkulyevsasha.news.pojos.ItemNews;

/**
 * Created by sasha_merkulev on 23.09.2017.
 */

interface MainView {

    void loadFreshNews(int navId);
    void readNews(int navId);

    void prepareToSearch();
    void searchNews(String searchText);

    void startLoadingNewsService();

    void registerBroadcastReceiver();
    void unregisterBroadcastReceiver();


    void showWebViewScreen(ItemNews item);

    void hideProgress();

    void showItems(List<ItemNews> result);

    void showNoSearchResultMessage();
}
