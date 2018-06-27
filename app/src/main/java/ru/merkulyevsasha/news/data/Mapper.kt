package ru.merkulyevsasha.news.data


interface Mapper<From, To> {
    fun map(item: From): To
}
