package ru.merkulyevsasha.coreandroid.base

import io.reactivex.disposables.CompositeDisposable

abstract class BasePresenterImpl<T : BaseView> {

    protected var view: T? = null
    protected val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private val commandViewHolder = CommandViewHolder()

    fun bindView(view: T) {
        this.view = view
        commandViewHolder.execute()
    }

    fun unbindView() {
        this.view = null
    }

    fun onDestroy() {
        compositeDisposable.dispose()
    }

    fun showProgress() {
        commandViewHolder.showProgress()
    }

    fun hideProgress() {
        commandViewHolder.hideProgress()
    }

    inner class CommandViewHolder {
        private val commands = mutableListOf<ViewCommand>()

        fun execute() {
            commands.forEach {
                it.execute()
                commands.remove(it)
            }
        }

        fun showProgress() {
            if (view == null) {
                commands.add(object : ViewCommand {
                    override fun execute() {
                        if (view is BaseProgressView) {
                            (view as BaseProgressView?)?.showProgress()
                        }
                    }
                })
            } else {
                if (view is BaseProgressView) {
                    (view as BaseProgressView?)?.showProgress()
                }
            }
        }

        fun hideProgress() {
            if (view == null) {
                commands.add(object : ViewCommand {
                    override fun execute() {
                        if (view is BaseProgressView) {
                            (view as BaseProgressView?)?.hideProgress()
                        }
                    }
                })
            } else {
                if (view is BaseProgressView) {
                    (view as BaseProgressView?)?.hideProgress()
                }
            }
        }
    }
}

interface ViewCommand {
    fun execute()
}