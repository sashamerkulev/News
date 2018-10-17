package ru.merkulyevsasha.news.helpers

import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager

import javax.inject.Inject

class BroadcastHelper @Inject
constructor(private val context: Context) {

    private fun sendBroadcast(start: Boolean, finished: Boolean) {
        val intent = Intent(ACTION_LOADING)
        intent.putExtra(KEY_START_WORKER, start)
        intent.putExtra(KEY_FINISH_WORKER, finished)
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
    }

    fun notifyWorkerFinished() {
        sendBroadcast(false, true)
    }

    fun notifyWorkerStart() {
        sendBroadcast(true, false)
    }

    companion object {

        const val ACTION_LOADING = "ru.merkulyevsasha.news.DATA_LOADING"
        const val KEY_FINISH_WORKER = "ru.merkulyevsasha.news.key_finished"
        const val KEY_START_WORKER = "ru.merkulyevsasha.news.key_start"
    }
}
