package ru.merkulyevsasha.news.presentation.main;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import ru.merkulyevsasha.news.R;
import ru.merkulyevsasha.news.domain.NewsInteractor;
import ru.merkulyevsasha.news.pojos.Article;
import ru.merkulyevsasha.news.presentation.BasePresenter;

public class MainPresenter extends BasePresenter<MainView> {

    private final NewsInteractor news;

    private final List<Article> cached = new ArrayList<>();

    public MainPresenter(NewsInteractor news) {
        this.news = news;
    }

    void onResume(boolean isRefreshing, int navId, String searchText) {

        if (isRefreshing) return;

        boolean isSearch = false;
        Single<List<Article>> articles;
        if (searchText == null || searchText.isEmpty()) {
            articles = getArticlesByNavId(navId);
        } else {
            isSearch = true;
            articles = news.search(searchText);
        }
        procceed(articles, isSearch);
    }

    void onPrepareToSearch() {
        if (view == null) return;
        view.prepareToSearch();
    }

    void onItemClicked(Article item) {
        if (view == null) return;
        view.showDetailScreen(item);
    }

    void onReceived(int navId, boolean updated, boolean finished) {
        if (view == null) return;
        if (updated) {
            onCancelSearch(navId);
        }
        if (finished) {
            view.hideProgress();
        }
    }

    void onRefresh(int navId) {
        procceed(news.readNewsAndSaveToDb(navId), false);
    }

    void onSearch(String searchText) {
        procceed(news.search(searchText), true);
    }

    void onCancelSearch(int navId) {
        procceed(navId == R.id.nav_all ? news.selectAll() : news.selectNavId(navId), false);
    }

    void onSelectSource(int navId) {
        procceed(news.selectNavId(navId), false);
    }

    void onCreateView() {
        view.showItems(cached);
        compositeDisposable.add(
                news
                        .getFirstRunFlag()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(first -> {
                                    if (view == null) return;
                                    view.scheduleJob();
                                    news.setFirstRunFlag();
                                },
                                throwable -> {
                                })
        );
    }

    private Single<List<Article>> getArticlesByNavId(int navId) {
        Single<List<Article>> articles;
        if (navId == R.id.nav_all) articles = news.selectAll()
                .flattenAsFlowable(t -> t)
                .switchIfEmpty(Flowable.defer(() -> news.readNewsAndSaveToDb(navId).flattenAsFlowable(tt -> tt)))
                .toList();
        else articles = news.selectNavId(navId);
        return articles;
    }

    private void procceed(Single<List<Article>> articles, boolean isSearch) {
        compositeDisposable.add(
                articles
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(unused -> {
                                    if (view == null) return;
                                    view.showProgress();
                                }
                        )
                        .doFinally(() -> {
                            if (view == null) return;
                            view.hideProgress();
                        })
                        .subscribe(items -> {
                                    cached.clear();
                                    cached.addAll(items);
                                    if (view == null) return;
                                    if (isSearch && items.isEmpty()) {
                                        view.showNoSearchResultMessage();
                                        return;
                                    }
                                    view.showItems(items);
                                },
                                throwable -> {
                                    if (view == null) return;
                                    view.showMessageError();
                                }));
    }
}
