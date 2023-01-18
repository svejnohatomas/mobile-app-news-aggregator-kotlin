package io.github.svejnohatomas.swansea.newsaggregator.ui.category

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.github.svejnohatomas.swansea.newsaggregator.model.Article

/**
 * A ViewModel for CategoryActivity.
 *
 * @author Tomas Svejnoha (987451@swansea.ac.uk)
 * @version 1.0.0
 */
class CategoryViewModel : ViewModel() {

    /**
     * A list of articles to be displayed.
     * */
    private val _articles = MutableLiveData<MutableList<Article>>().apply {
        value = mutableListOf()
    }

    /**
     * A list of articles to be displayed.
     * */
    val articles: LiveData<MutableList<Article>> = _articles
}