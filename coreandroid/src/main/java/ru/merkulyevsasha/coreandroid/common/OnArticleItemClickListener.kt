package ru.merkulyevsasha.coreandroid.common

import ru.merkulyevsasha.core.models.Article

interface OnArticleItemClickListener {
    fun onArticleClicked(item: Article)
}