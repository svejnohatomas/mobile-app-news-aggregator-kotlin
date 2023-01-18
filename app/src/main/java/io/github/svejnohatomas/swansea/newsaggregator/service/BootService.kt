package io.github.svejnohatomas.swansea.newsaggregator.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import io.github.svejnohatomas.swansea.newsaggregator.helper.PreferenceHelper
import java.util.concurrent.TimeUnit

/**
 * A broadcast receiver to start the AutoRefresh service.
 *
 * @author Tomas Svejnoha (987451@swansea.ac.uk)
 * @version 1.0.0
 * */
class BootService : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null) {
            if (intent?.action == "android.intent.action.BOOT_COMPLETED") {
                startOrUpdateAutoRefreshService(context)
            }
        }
    }

    companion object {

        /**
         * Starts or updates an auto-refresh service.
         *
         * @param context The context of the service.
         * */
        fun startOrUpdateAutoRefreshService(context: Context) {
            val refreshIntervalMinutes = PreferenceHelper(context).getAutoRefreshInterval().toLong()

            val updateIntent = Intent(context, AutoRefreshService::class.java)
            val updatePendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.getBroadcast(
                    context,
                    0,
                    updateIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
            } else {
                PendingIntent.getBroadcast(
                    context,
                    0,
                    updateIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT)
            }

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setExact(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                TimeUnit.MINUTES.toMillis(refreshIntervalMinutes),
                updatePendingIntent
            )
        }
    }
}
