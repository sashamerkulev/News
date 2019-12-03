package ru.merkulyevsasha.coreandroid.base

import java.util.*

class ProgressViewCommand(
    private val view: BaseProgressView?,
    private val commands : ArrayList<ViewCommand>
) {
    private fun showProgress() {
        if (view == null) {
            commands.add(object : ViewCommand {
                override fun execute() {
                    view?.showProgress()
                }
            })
        } else {
            view.showProgress()
        }
    }

    private fun hideProgress() {
        if (view == null) {
            commands.add(object : ViewCommand {
                override fun execute() {
                    view?.hideProgress()
                }
            })
        } else {
            view.hideProgress()
        }
    }

}