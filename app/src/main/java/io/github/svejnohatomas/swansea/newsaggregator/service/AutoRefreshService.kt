package io.github.svejnohatomas.swansea.newsaggregator.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.github.svejnohatomas.swansea.newsaggregator.helper.ApiHelper
import io.github.svejnohatomas.swansea.newsaggregator.helper.PreferenceHelper

/**
 * A service to auto-refresh news articles.
 *
 * @author Tomas Svejnoha (987451@swansea.ac.uk)
 * @version 1.0.0
 * */
class AutoRefreshService : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null) {
            val preferenceHelper = PreferenceHelper(context)

            if (preferenceHelper.isAutoRefreshEnabled()) {
                // Update AlarmManager for the AutoRefresh service
                BootService.startOrUpdateAutoRefreshService(context)

                ApiHelper(context).refresh(false)
            }
        }
    }
}
