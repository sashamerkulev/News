package ru.merkulyevsasha.core

import ru.merkulyevsasha.core.routers.MainFragmentRouter

interface ServiceLocator {
    fun <T> get(clazz: Class<T>): T
    fun <T> set(clazz: Class<T>, instance: Any)
    fun <T> release(clazz: Class<T>)
    fun releaseAll()
    fun addFragmentRouter(mainFragmentRouter: MainFragmentRouter)
    fun releaseFragmentRouter()
}