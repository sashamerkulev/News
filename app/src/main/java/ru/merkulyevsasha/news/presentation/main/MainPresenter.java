package ru.merkulyevsasha.news.presentation.main;

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

    @Inject
    MainPresenter(NewsInteractor news) {
        this.news = news;
    }

    void onResume(boolean isRefreshing, int navId, String searchText) {
        Single<List<Article>> articles;
        if (searchText == null || searchText.isEmpty()) {
            if (navId == R.id.nav_all) articles = news.selectAll()
                    .flattenAsFlowable(t->t)
                    .switchIfEmpty(Flowable.defer(()->news.readNewsAndSaveToDb(navId).flattenAsFlowable(tt->tt))).toList();
            else articles = news.selectNavId(navId);
        } else {
            articles = news.search(searchText);
        }
        procceed(articles);
    }

    private void procceed(Single<List<Article>> articles) {
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
                                    if (view == null) return;
                                    view.showItems(items);
                                },
                                throwable -> {
                                }));
    }

    void onPrepareToSearch() {
        if (view == null) return;
        view.prepareToSearch();
    }

    void onItemClicked(Article item) {
        if (view == null) return;
        view.showDetailScreen(item);
    }

    void onRefresh(int navId) {
        procceed(news.readNewsAndSaveToDb(navId));
    }

    void onSearch(String searchText) {
        procceed(news.search(searchText));
    }

    void onCancelSearch(int navId) {
        procceed(navId == R.id.nav_all ? news.selectAll() : news.selectNavId(navId));
    }

    void onSelectSource(int navId) {
        procceed(news.selectNavId(navId));
    }

    void onCreateView() {
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
}
