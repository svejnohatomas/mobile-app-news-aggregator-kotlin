package io.github.svejnohatomas.swansea.newsaggregator.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.koushikdutta.ion.Ion
import io.github.svejnohatomas.swansea.newsaggregator.R
import io.github.svejnohatomas.swansea.newsaggregator.helper.DateTimeHelper
import io.github.svejnohatomas.swansea.newsaggregator.model.Article

/**
 * A RecyclerView adapter to display news articles.
 *
 * @param dataset A list of news articles to display.
 *
 * @author Tomas Svejnoha (987451@swansea.ac.uk)
 * @version 1.0.0
 */
class NewsItemAdapter(private val dataset: MutableList<Article>)
    : RecyclerView.Adapter<NewsItemAdapter.NewsItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_article_item, parent, false)

        return NewsItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: NewsItemViewHolder, position: Int) {
        val item = dataset[position]

        /**
         * This block uses Ion library created by Koushik Dutta (https://github.com/koush),
         * distributed under the APACHE LICENSE, VERSION 2.0 and available at https://github.com/koush/ion.
         * */
        Ion.with(holder.imageView)
            .placeholder(R.drawable.im_loading)
            .error(R.drawable.im_error)
            .load(item.urlToImage)

        // Set the values from dataset to holder
        holder.titleTextView.text = item.title
        holder.descriptionTextView.text = item.description
        holder.timeTextView.text = DateTimeHelper().getTimeTextFromEpochTicks(item.publishedAt, holder.layout.context)
        holder.publisherTextView.text = item.sourceName

        holder.layout.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(item.url)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount() = dataset.size

    /**
     * A holder class for RecyclerView item.
     *
     * @param view The view to use in the ViewHolder.
     * */
    class NewsItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val layout: ConstraintLayout = view.findViewById(R.id.article_item)

        val imageView: ImageView = view.findViewById(R.id.article_item_image)
        val titleTextView: TextView = view.findViewById(R.id.article_item_title)
        val descriptionTextView: TextView = view.findViewById(R.id.article_item_description)
        val timeTextView: TextView = view.findViewById(R.id.article_item_time)
        val publisherTextView: TextView = view.findViewById(R.id.article_item_publishers)
    }

}