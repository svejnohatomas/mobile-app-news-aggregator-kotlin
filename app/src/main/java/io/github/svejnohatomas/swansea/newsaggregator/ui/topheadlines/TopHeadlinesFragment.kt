package io.github.svejnohatomas.swansea.newsaggregator.ui.topheadlines

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.svejnohatomas.swansea.newsaggregator.ui.main.MainActivity
import io.github.svejnohatomas.swansea.newsaggregator.adapter.NewsItemAdapter
import io.github.svejnohatomas.swansea.newsaggregator.databinding.FragmentMyNewsBinding
import io.github.svejnohatomas.swansea.newsaggregator.helper.DatabaseHelper
import io.github.svejnohatomas.swansea.newsaggregator.helper.PreferenceHelper
import io.github.svejnohatomas.swansea.newsaggregator.helper.RefreshHelper
import io.github.svejnohatomas.swansea.newsaggregator.interfaces.IConfirmationObserver
import io.github.svejnohatomas.swansea.newsaggregator.interfaces.IRefreshObserver
import io.github.svejnohatomas.swansea.newsaggregator.model.Article

/**
 * A fragment for displaying top headlines.
 *
 * @author Tomas Svejnoha (987451@swansea.ac.uk)
 * @version 1.0.0
 * */
class TopHeadlinesFragment : Fragment(), IRefreshObserver, IConfirmationObserver {
    /** A ViewModel of the fragment. */
    private lateinit var viewModel: TopHeadlinesViewModel
    /** A binding to the fragment's layout. */
    private var _binding: FragmentMyNewsBinding? = null

    /** A binding to the fragment's layout. */
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[TopHeadlinesViewModel::class.java]
        _binding = FragmentMyNewsBinding.inflate(inflater, container, false)

        // Initiate RecyclesView
        val recyclerView: RecyclerView = binding.newsRecyclerView
        recyclerView.adapter = NewsItemAdapter(viewModel.articles.value!!)
        recyclerView.setHasFixedSize(false)

        viewModel.articles.observe(viewLifecycleOwner) {
            // If the mutable list changes, re-create the adapter
            recyclerView.adapter = NewsItemAdapter(it)
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
     * Gets top headlines articles.
     *
     * @return A list of top headlines articles.
     * */
    private fun getArticles() : List<Article> {
        val country = PreferenceHelper(binding.root.context).getCountry()

        val dbHelper = DatabaseHelper(this.binding.root.context);
        return dbHelper.getArticles(country, "top-headlines", null)
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
        viewModel.articles.value!!.clear()

        for (article in getArticles()) {
            viewModel.articles.value!!.add(article)
        }

        binding.newsRecyclerView.adapter?.notifyDataSetChanged()
    }
}