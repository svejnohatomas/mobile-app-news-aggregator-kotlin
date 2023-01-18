package io.github.svejnohatomas.swansea.newsaggregator.ui.category

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.github.svejnohatomas.swansea.newsaggregator.R
import io.github.svejnohatomas.swansea.newsaggregator.adapter.NewsItemAdapter
import io.github.svejnohatomas.swansea.newsaggregator.databinding.ActivityCategoryBinding
import io.github.svejnohatomas.swansea.newsaggregator.helper.DatabaseHelper
import io.github.svejnohatomas.swansea.newsaggregator.helper.ApiHelper
import io.github.svejnohatomas.swansea.newsaggregator.helper.RefreshHelper
import io.github.svejnohatomas.swansea.newsaggregator.interfaces.IConfirmationObserver
import io.github.svejnohatomas.swansea.newsaggregator.interfaces.IRefreshObserver
import io.github.svejnohatomas.swansea.newsaggregator.model.Article
import io.github.svejnohatomas.swansea.newsaggregator.ui.login.LoginActivity
import io.github.svejnohatomas.swansea.newsaggregator.ui.search.SearchActivity
import io.github.svejnohatomas.swansea.newsaggregator.ui.settings.SettingsActivity
import java.util.*

/**
 * An activity to display articles in a category.
 *
 * @author Tomas Svejnoha (987451@swansea.ac.uk)
 * @version 1.0.0
 * */
class CategoryActivity : AppCompatActivity(), IRefreshObserver, IConfirmationObserver {
    /** A binding to the activity's layout. */
    private lateinit var binding: ActivityCategoryBinding
    /** A ViewModel of the activity. */
    private lateinit var viewModel: CategoryViewModel
    /** A RefreshHelper instantiated by the activity. */
    private lateinit var apiHelper: ApiHelper

    /** A category for which to display articles. */
    private var category: String? = null
    /** A country for which to display articles. */
    private var country: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[CategoryViewModel::class.java]
        category = intent.getStringExtra("category")
        if (intent.hasExtra("country")) {
            country = intent.getStringExtra("country")
        }

        if (category == null) {
            finish()
        } else {
            supportActionBar?.title = category!!.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.ROOT
                ) else it.toString()
            }

            // Initiate RecyclerView
            var recyclerView: RecyclerView = binding.categoryDetailRecyclerView
            recyclerView.adapter = NewsItemAdapter(viewModel.articles.value!!)
            recyclerView.setHasFixedSize(false)

            viewModel.articles.observe(this) {
                // If the mutable list changes, re-create the adapter
                recyclerView.adapter = NewsItemAdapter(it)
            }

            apiHelper = ApiHelper(this)
            apiHelper.attach(this)

            reload()

            if (category != "search") {
                // Refreshes TopHeadlines and My News
                apiHelper.refresh(true)
            }
        }
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
                if (category == "search") {
                    val refreshUri = intent.getStringExtra("refreshUri")
                    if (!refreshUri.isNullOrBlank()) {
                        apiHelper.search(refreshUri, ApiHelper.Companion.SearchMode.REFRESH)
                    }
                } else {
                    apiHelper.refresh(true)
                }

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
                // Add the following flags to remove the CategoryActivity from the stack
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)

                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }
    override fun onDestroy() {
        super.onDestroy()
        apiHelper.detach(this)
    }

    /**
     * Gets articles for a category.
     *
     * @param category A category for which to retrieve articles.
     *
     * @return A list of articles in a given category.
     * */
    private fun getArticles(country: String?, category: String?): List<Article> {
        val dbHelper = DatabaseHelper(this.binding.root.context);
        return dbHelper.getArticles(country, category, null)
    }

    override fun refresh(userTriggered: Boolean) {
        if (userTriggered) {
            reload()
        } else {
            RefreshHelper.showConfirmationDialog(binding.root.context, this)
        }
    }
    override fun onConfirm() {
        reload()
    }

    /**
     * Reloads the articles.
     * */
    private fun reload() {
        viewModel.articles.value!!.clear()

        for (article in getArticles(country, category)) {
            viewModel.articles.value!!.add(article)
        }

        binding.categoryDetailRecyclerView.adapter?.notifyDataSetChanged()
    }
}