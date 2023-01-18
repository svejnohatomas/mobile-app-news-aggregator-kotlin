package io.github.svejnohatomas.swansea.newsaggregator.ui.mynews

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.svejnohatomas.swansea.newsaggregator.ui.main.MainActivity
import io.github.svejnohatomas.swansea.newsaggregator.adapter.NewsCategoryItemAdapter
import io.github.svejnohatomas.swansea.newsaggregator.databinding.FragmentMyNewsBinding
import io.github.svejnohatomas.swansea.newsaggregator.helper.DatabaseHelper
import io.github.svejnohatomas.swansea.newsaggregator.helper.PreferenceHelper
import io.github.svejnohatomas.swansea.newsaggregator.helper.RefreshHelper
import io.github.svejnohatomas.swansea.newsaggregator.interfaces.IConfirmationObserver
import io.github.svejnohatomas.swansea.newsaggregator.interfaces.IRefreshObserver
import io.github.svejnohatomas.swansea.newsaggregator.model.CategoryGroup

/**
 * A fragment for displaying user selected topics.
 *
 * @author Tomas Svejnoha (987451@swansea.ac.uk)
 * @version 1.0.0
 * */
class MyNewsFragment : Fragment(), IRefreshObserver, IConfirmationObserver {
    /** A ViewModel of the fragment. */
    private lateinit var viewModel: MyNewsViewModel
    /** A binding to the fragment's layout. */
    private var _binding: FragmentMyNewsBinding? = null

    /** A binding to the fragment's layout. */
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[MyNewsViewModel::class.java]

        _binding = FragmentMyNewsBinding.inflate(inflater, container, false)

        // Initiate RecyclerView
        val recyclerView: RecyclerView = binding.newsRecyclerView
        recyclerView.adapter = NewsCategoryItemAdapter(viewModel.articleCategories.value!!)
        recyclerView.setHasFixedSize(false)

        viewModel.articleCategories.observe(viewLifecycleOwner) {
            // If the mutable list changes, re-create the adapter
            recyclerView.adapter = NewsCategoryItemAdapter(it)
        }

        // Update the RecyclerView
        refresh(true)

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (activity as MainActivity).apiHelper.attach(this)
    }
    override fun onDetach() {
        super.onDetach()

        (activity as MainActivity).apiHelper.detach(this)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    override fun onResume() {
        super.onResume()
        refresh(true)
    }

    /**
     * Gets articles for each topic selected by a user grouped in categories.
     *
     * @return A list of groups of articles.
     * */
    private fun getCategoryGroups() : List<CategoryGroup> {
        val preferenceHelper = PreferenceHelper(binding.root.context)
        val databaseHelper = DatabaseHelper(binding.root.context)

        val country = preferenceHelper.getCountry()
        val categoryGroups = mutableListOf<CategoryGroup>()

        preferenceHelper.getEnabledCategories().forEach { category ->
            val articles = databaseHelper.getArticles(country, category, 2)

            categoryGroups.add(CategoryGroup(category, articles))
        }

        return categoryGroups
    }

    override fun refresh(userTriggered: Boolean) {
        if (userTriggered) {
            reload()
        } else {
            if (isVisible) {
                RefreshHelper.showConfirmationDialog(binding.root.context, this)
            } else {
                reload()
            }
        }
    }
    override fun onConfirm() {
        reload()
    }

    /**
     * Reloads the articles.
     * */
    private fun reload() {
        viewModel.articleCategories.value!!.clear()

        for (categoryGroup in getCategoryGroups()) {
            viewModel.articleCategories.value!!.add(categoryGroup)
        }

        binding.newsRecyclerView.adapter?.notifyDataSetChanged()
    }
}