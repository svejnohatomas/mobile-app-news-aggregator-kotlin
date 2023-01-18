package io.github.svejnohatomas.swansea.newsaggregator.adapter

import android.content.res.Resources.NotFoundException
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import io.github.svejnohatomas.swansea.newsaggregator.ui.mynews.MyNewsFragment
import io.github.svejnohatomas.swansea.newsaggregator.ui.topheadlines.TopHeadlinesFragment

/**
 * A ViewPager2 adapter to display pages.
 *
 * @param fragmentActivity A FragmentActivity to use in the adapter.
 *
 * @author Tomas Svejnoha (987451@swansea.ac.uk)
 * @version 1.0.0
 * */
class PagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> TopHeadlinesFragment()
            1 -> MyNewsFragment()

            else -> throw NotFoundException("Position not found")
        }
    }
}