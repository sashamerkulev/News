package ru.merkulyevsasha.news.presentation.main;

import java.util.List;

import ru.merkulyevsasha.news.pojos.ItemNews;

/**
 * Created by sasha_merkulev on 23.09.2017.
 */

public class MainPresenter {

    private MainView view;

    void setView(MainView view){
        this.view = view;
    }

    void onPause(){
        view.unregisterBroadcastReceiver();
        view = null;
    }

    void onResume(boolean isRefreshing, int navId, String searchText){
        if (view == null) return;

        view.registerBroadcastReceiver();

        if (isRefreshing){
            view.startLoadingNewsService();
        } else {
            if (searchText == null || searchText.isEmpty()) {
                view.readNews(navId);
            } else {
                view.searchNews(searchText);
            }
        }
    }

    void onPrepareToSearch() {
        if (view == null) return;
        view.prepareToSearch();
    }

    void onItemClicked(ItemNews item) {
        if (view == null) return;
        view.showWebViewScreen(item);
    }

    void onRefresh(int navId) {
        if (view == null) return;
        view.loadFreshNews(navId);
    }

    void onReceived(int navId, boolean updated, boolean finished) {
        if (view == null) return;
        if (updated){
            view.readNews(navId);
        }
        if (finished){
            view.hideProgress();
        }
    }

    void onSearch(String searchText) {
        if (view == null) return;
        view.searchNews(searchText);
    }

    void onCancelSearch(int navId) {
        if (view == null) return;
        view.readNews(navId);
    }

    void onSelectSource(int navId) {
        if (view == null) return;
        view.readNews(navId);
    }

    void onLoadResult(int navId, List<ItemNews> result) {
        if (view == null) return;
        if (result.size() > 0) view.showItems(result);
        else view.loadFreshNews(navId);
    }

    void onSearchResult(List<ItemNews> result) {
        if (view == null) return;
        if (result.size() > 0) view.showItems(result);
        else view.showNoSearchResultMessage();
    }
}
