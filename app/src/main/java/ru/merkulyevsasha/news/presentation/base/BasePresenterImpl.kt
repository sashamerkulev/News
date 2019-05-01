package ru.merkulyevsasha.news.presentation.base

import io.reactivex.disposables.CompositeDisposable

abstract class BasePresenterImpl<T : BaseView> {

    protected var view: T? = null
    protected val compositeDisposable: CompositeDisposable = CompositeDisposable()

    fun bindView(view: T) {
        this.view = view
    }

    fun unbindView() {
        this.view = null
    }

    fun onDestroy() {
        compositeDisposable.dispose()
    }
}
