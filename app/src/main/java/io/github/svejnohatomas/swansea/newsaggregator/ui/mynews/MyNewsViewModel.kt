package io.github.svejnohatomas.swansea.newsaggregator.ui.mynews

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.github.svejnohatomas.swansea.newsaggregator.model.CategoryGroup

/**
 * A ViewModel for MyNewsFragment.
 *
 * @author Tomas Svejnoha (987451@swansea.ac.uk)
 * @version 1.0.0
 * */
class MyNewsViewModel : ViewModel() {

    /** A list of article groups to be displayed. */
    private val _articleCategories = MutableLiveData<MutableList<CategoryGroup>>().apply {
        value = mutableListOf()
    }

    /** A list of article groups to be displayed. */
    val articleCategories: LiveData<MutableList<CategoryGroup>> = _articleCategories
}