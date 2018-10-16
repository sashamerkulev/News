package ru.merkulyevsasha.news.newsjobs

interface BackgroundWorker {
    fun runWorker(navId: Int, searchText: String?)
}