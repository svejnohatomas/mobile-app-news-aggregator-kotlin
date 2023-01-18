package io.github.svejnohatomas.swansea.newsaggregator.ui.settings.fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import io.github.svejnohatomas.swansea.newsaggregator.R

/**
 * A fragment to display notification settings.
 *
 * @author Tomas Svejnoha (987451@swansea.ac.uk)
 * @version 1.0.0
 * */
class NotificationsSettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_notifications, rootKey)
    }
}