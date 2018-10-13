package ru.merkulyevsasha.news.helpers

import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager

import javax.inject.Inject

/**
 * Created by sasha_merkulev on 22.09.2017.
 */

class BroadcastHelper @Inject
constructor(private val context: Context) {

    private fun sendBroadcast(updated: Boolean, finished: Boolean) {
        val intent = Intent(ACTION_LOADING)
        intent.putExtra(KEY_UPDATE_NAME, updated)
        intent.putExtra(KEY_FINISH_NAME, finished)
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
    }

    fun sendFinishBroadcast() {
        sendBroadcast(false, true)
    }

    fun sendUpdateBroadcast() {
        sendBroadcast(true, false)
    }

    companion object {

        const val ACTION_LOADING = "ru.merkulyevsasha.news.DATA_LOADING"
        const val KEY_FINISH_NAME = "ru.merkulyevsasha.news.key_finished"
        const val KEY_UPDATE_NAME = "ru.merkulyevsasha.news.key_updated"
    }
}
