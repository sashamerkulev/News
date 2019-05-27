package ru.merkulyevsasha.apprate

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AlertDialog
import ru.merkulyevsasha.preferences.KeyValueStorageImpl
import java.util.*

object AppRateRequester {

    private val MAX_COUNT = 10
    private val MAX_DAYS_WAIT = 3

    fun Run(context: Context, packagename: String) {

        val sharedPreferences = KeyValueStorageImpl(context)

        if (!sharedPreferences.isApplicationAlreadyRatedFlag()) {

            val count = sharedPreferences.getApplicationRunNumber()
            val date = sharedPreferences.getLastApplicationRunDate()
            sharedPreferences.updateApplicationRunNumber()

            if (count > MAX_COUNT) {

                if (date > 0) {
                    val calendar = Calendar.getInstance()
                    val now = calendar.timeInMillis

                    val days = (now - date) / (24 * 60 * 60 * 1000)
                    if (days < MAX_DAYS_WAIT)
                        return
                }

                val builder = AlertDialog.Builder(context)
                builder.setTitle(R.string.rate_title)
                builder.setMessage(R.string.rate_message)
                builder.setCancelable(false)

                builder.setNeutralButton(R.string.remind_later_message) { dialog, which ->
                    sharedPreferences.updateLastApplicationRunDate()
                    dialog.dismiss()
                }

                builder.setNegativeButton(R.string.no_thanks_message) { dialog, which ->
                    sharedPreferences.setApplicationRatedFlag()
                    dialog.dismiss()
                }

                builder.setPositiveButton(R.string.rate_now_message) { dialog, which ->
                    sharedPreferences.setApplicationRatedFlag()
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packagename")))
                    dialog.dismiss()
                }

                builder.show()
            }
        }

    }

}
