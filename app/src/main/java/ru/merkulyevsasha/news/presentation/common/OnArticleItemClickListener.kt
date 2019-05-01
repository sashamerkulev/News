package ru.merkulyevsasha.news.presentation.common

import ru.merkulyevsasha.core.models.Article

interface OnArticleItemClickListener {
    fun onArticleClicked(item: Article)
}