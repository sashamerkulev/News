package ru.merkulyevsasha.news.newsjobs

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class RebootActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        action?.apply {
            if (action == Intent.ACTION_BOOT_COMPLETED ||
                action == "android.intent.action.QUICKBOOT_POWERON" ||
                action == "com.htc.intent.action.QUICKBOOT_POWERON") {

                NewsWorkerPeriodicRunner().runWorker()
            }
        }
    }
}
