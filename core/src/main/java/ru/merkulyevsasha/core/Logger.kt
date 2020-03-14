package ru.merkulyevsasha.core

import android.util.Log

object Logger {
    fun log(message: String) {
        Log.i("news", message)
    }

    fun logStacktrace(stackTrace: List<StackTraceElement>) {
        stackTrace.forEach {
            log("${it.className} ${it.methodName} ${it.lineNumber}")
        }
    }

}