package ru.merkulyevsasha.sourcelist

import ru.merkulyevsasha.core.models.RssSource
import ru.merkulyevsasha.coreandroid.base.BaseView

interface SourceListView : BaseView {
    fun hideProgress()
    fun showProgress()
    fun showError()
    fun showItems(items: List<RssSource>)
}