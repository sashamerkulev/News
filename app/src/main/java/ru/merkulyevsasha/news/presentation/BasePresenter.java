package ru.merkulyevsasha.news.presentation;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;


public abstract class BasePresenter<T> {

    protected T view;
    protected CompositeDisposable compositeDisposable;

    private Disposable disposableSearch;

    protected BasePresenter() {
        compositeDisposable = new CompositeDisposable();
        disposableSearch = null;
    }

    public void bindView(T view) {
        this.view = view;
    }

    public void unbindView() {
        this.view = null;
    }

    public void onDestroy() {
        compositeDisposable.dispose();
    }

    public void replaceSearchDisposable(Disposable disposable){
        if (disposableSearch != null) {
            compositeDisposable.delete(disposableSearch);
            disposableSearch.dispose();
        }
        disposableSearch = disposable;
        compositeDisposable.add(disposableSearch);
    }

}
