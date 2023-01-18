package io.github.svejnohatomas.swansea.newsaggregator.ui.settings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.github.svejnohatomas.swansea.newsaggregator.R
import io.github.svejnohatomas.swansea.newsaggregator.ui.settings.fragments.RootSettingsFragment

/**
 * An activity to display user settings.
 *
 * @author Tomas Svejnoha (987451@swansea.ac.uk)
 * @version 1.0.0
 * */
class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.preferences_content_frame_layout, RootSettingsFragment())
            .commit()
    }
}