package io.github.svejnohatomas.swansea.newsaggregator.model

import java.util.Objects

/**
 * An article model.
 *
 * @param sourceId The Id of a source.
 * @param sourceName The name of a source.
 * @param author The author of the article.
 * @param title The title of the article.
 * @param description The description of the article.
 * @param url The URL of the article.
 * @param urlToImage The URL of the article image.
 * @param publishedAt The ISO Instant date time when the article was published.
 * @param content The content of the article.
 * @param country The country origin of the article.
 * @param category The category of the article.
 *
 * @author Tomas Svejnoha (987451@swansea.ac.uk)
 * @version 1.0.0
 * */
class Article(val sourceId: String?,
              val sourceName: String?,
              val author: String?,
              val title: String?,
              val description: String?,
              val url: String?,
              val urlToImage: String?,
              val publishedAt: Long?,
              val content: String?,
              val country: String?,
              val category: String?) {

    /**
     * Returns a hash code for the article.
     *
     * @return The hash code.
     * */
    override fun hashCode(): Int {
        return Objects.hash(
            sourceId, sourceName,
            author, title, description,
            url, urlToImage, publishedAt,
            content, country, category)
    }
}