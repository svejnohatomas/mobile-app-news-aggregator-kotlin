package io.github.svejnohatomas.swansea.newsaggregator.ui.main

import android.content.Context
import android.content.Intent
import android.content.res.Resources.NotFoundException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.view.MenuCompat
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.github.svejnohatomas.swansea.newsaggregator.R
import io.github.svejnohatomas.swansea.newsaggregator.adapter.PagerAdapter
import io.github.svejnohatomas.swansea.newsaggregator.databinding.ActivityMainBinding
import io.github.svejnohatomas.swansea.newsaggregator.helper.ApiHelper
import io.github.svejnohatomas.swansea.newsaggregator.service.BootService
import io.github.svejnohatomas.swansea.newsaggregator.ui.login.LoginActivity
import io.github.svejnohatomas.swansea.newsaggregator.ui.search.SearchActivity
import io.github.svejnohatomas.swansea.newsaggregator.ui.settings.SettingsActivity

/**
 * A main activity of the application.
 *
 * @author Tomas Svejnoha (987451@swansea.ac.uk)
 * @version 1.0.0
 * */
class MainActivity : AppCompatActivity() {

    /** A binding to the activity's layout. */
    private lateinit var binding: ActivityMainBinding

    /** A RefreshHelper instantiated by the activity. */
    lateinit var apiHelper: ApiHelper // TODO: Use Singleton pattern

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        apiHelper = ApiHelper(this)
        apiHelper.refresh(true)

        initialiseTabLayout()

        BootService.startOrUpdateAutoRefreshService(this)
    }

    /**
     * Initialises the TabLayout in the activity.
     * */
    private fun initialiseTabLayout() {
        binding.pager.adapter = PagerAdapter(this)

        TabLayoutMediator(binding.mainTabLayout, binding.pager) { tab, index ->
            tab.text = when(index) {
                0 -> getString(R.string.top_headlines)
                1 -> getString(R.string.my_news)

                else -> throw NotFoundException("Position not found")
            }
        }.attach()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        if (menu != null)
            MenuCompat.setGroupDividerEnabled(menu, true)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_main_search -> {
                val searchActivityIntent = Intent(this, SearchActivity::class.java)
                startActivity(searchActivityIntent)

                return true
            }
            R.id.menu_main_refresh -> {
                apiHelper.refresh(true)

                return true
            }
            R.id.menu_main_settings -> {
                val settingsActivityIntent = Intent(this, SettingsActivity::class.java)
                startActivity(settingsActivityIntent)

                return true
            }
            R.id.menu_main_logout -> {
                Firebase.auth.signOut()

                val intent = Intent(this, LoginActivity::class.java)
                // Add the following flags to remove the MainActivity from the stack
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)

                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()

        apiHelper.refresh(true)
    }

    companion object {
        /**
         * Gets an intent to start the MainActivity.
         *
         * @param packageContext A context from which to start the activity.
         *
         * @return the intent.
         * */
        fun getIntent(packageContext: Context): Intent {
            val intent = Intent(packageContext, MainActivity::class.java)
            // Add the following flags to remove any activity starting this intent from stack
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            return intent
        }
    }
}