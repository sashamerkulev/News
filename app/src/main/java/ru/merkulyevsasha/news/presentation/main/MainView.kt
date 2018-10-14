package ru.merkulyevsasha.news.presentation.main

import ru.merkulyevsasha.news.models.Article

interface MainView {

    fun prepareToSearch()

    fun showDetailScreen(item: Article)

    fun showProgress()
    fun hideProgress()

    fun showItems(result: List<Article>)
    fun showNoSearchResultMessage()
    fun showMessageError()
}