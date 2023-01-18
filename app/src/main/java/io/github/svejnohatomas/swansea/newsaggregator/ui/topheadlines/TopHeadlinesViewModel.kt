package io.github.svejnohatomas.swansea.newsaggregator.ui.topheadlines

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.github.svejnohatomas.swansea.newsaggregator.model.Article

/**
 * A ViewModel for TopHeadlinesFragment.
 *
 * @author Tomas Svejnoha (987451@swansea.ac.uk)
 * @version 1.0.0
 */
class TopHeadlinesViewModel : ViewModel() {
    /** A list of articles to be displayed. */
    private val _articles = MutableLiveData<MutableList<Article>>().apply {
        value = mutableListOf()
    }
    /** A list of articles to be displayed. */
    val articles: LiveData<MutableList<Article>> = _articles
}