package io.github.svejnohatomas.swansea.newsaggregator.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView
import com.koushikdutta.ion.Ion
import io.github.svejnohatomas.swansea.newsaggregator.R
import io.github.svejnohatomas.swansea.newsaggregator.helper.DateTimeHelper
import io.github.svejnohatomas.swansea.newsaggregator.helper.PreferenceHelper
import io.github.svejnohatomas.swansea.newsaggregator.model.CategoryGroup
import io.github.svejnohatomas.swansea.newsaggregator.ui.category.CategoryActivity
import java.util.*

/**
 * A RecyclerView adapter to display news categories.
 *
 * @param dataset A list of news categories to display.
 *
 * @author Tomas Svejnoha (987451@swansea.ac.uk)
 * @version 1.0.0
 */
class NewsCategoryItemAdapter(private val dataset: List<CategoryGroup>)
    : RecyclerView.Adapter<NewsCategoryItemAdapter.NewsCategoryItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsCategoryItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_my_news_group, parent, false)

        return NewsCategoryItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: NewsCategoryItemViewHolder, position: Int) {
        val item = dataset[position]

        holder.categoryName.text = item.name.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.ROOT
            ) else it.toString()
        }.plus(" >")

        holder.categoryName.setOnClickListener {
            val intent = Intent(holder.itemView.context, CategoryActivity::class.java)
            intent.putExtra("category", item.name)
            intent.putExtra("country", PreferenceHelper(holder.itemView.context).getCountry())
            holder.itemView.context.startActivity(intent)
        }

        val parent = holder.categoryNewsItems as ViewGroup
        parent.removeAllViews()

        for (article in item.articles) {
            val layout = LayoutInflater.from(holder.itemView.context)
                .inflate(R.layout.recycler_view_article_item, parent, false)

            /**
             * This block uses Ion library created by Koushik Dutta (https://github.com/koush),
             * distributed under the APACHE LICENSE, VERSION 2.0 and available at https://github.com/koush/ion.
             * */
            Ion.with(layout.rootView.findViewById<ShapeableImageView>(R.id.article_item_image))
                .placeholder(R.drawable.im_loading)
                .error(R.drawable.im_error)
                .load(article.urlToImage)

            // Set the values from dataset to holder
            layout.rootView.findViewById<MaterialTextView>(R.id.article_item_title)
                .text = article.title
            layout.rootView.findViewById<MaterialTextView>(R.id.article_item_description)
                .text = article.description
            layout.rootView.findViewById<MaterialTextView>(R.id.article_item_time)
                .text = DateTimeHelper().getTimeTextFromEpochTicks(article.publishedAt, layout.context)
            layout.rootView.findViewById<MaterialTextView>(R.id.article_item_publishers)
                .text = article.sourceName

            layout.rootView.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(article.url)
                holder.itemView.context.startActivity(intent)
            }

            holder.categoryNewsItems.addView(layout)
        }
    }

    override fun getItemCount(): Int = dataset.size

    /**
     * A holder class for RecyclerView item.
     *
     * @param view The view to use in the ViewHolder.
     * */
    class NewsCategoryItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val categoryName: MaterialTextView = view.findViewById(R.id.category_name)
        val categoryNewsItems: LinearLayout = view.findViewById(R.id.category_news_items)
    }
}