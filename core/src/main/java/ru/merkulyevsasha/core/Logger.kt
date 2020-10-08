package ru.merkulyevsasha.core

interface Logger {
    fun i(tag: String, message: String)
    fun v(tag: String, message: String)
    fun e(tag: String, e: Throwable)
}