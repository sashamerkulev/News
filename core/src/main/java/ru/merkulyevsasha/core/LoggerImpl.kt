package ru.merkulyevsasha.core

import android.util.Log

class LoggerImpl : Logger {
    override fun i(tag: String, message: String) {
        Log.i(tag, message)
    }

    override fun v(tag: String, message: String) {
        Log.v(tag, message)
    }

    override fun e(tag: String, e: Throwable) {
        Log.e(tag, e.message ?: e.javaClass.simpleName)
    }

}