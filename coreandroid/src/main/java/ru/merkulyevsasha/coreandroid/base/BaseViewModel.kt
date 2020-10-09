package ru.merkulyevsasha.coreandroid.base

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.MainScope

abstract class BaseViewModel : ViewModel(), LifecycleObserver {

    val compositeDisposable: CompositeDisposable = CompositeDisposable()
    val progress = MutableLiveData<Boolean>()
    val messages = MutableLiveData<String>()

    protected var mainScope = MainScope()

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }

}
