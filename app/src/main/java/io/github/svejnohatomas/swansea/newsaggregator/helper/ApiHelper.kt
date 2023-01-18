package io.github.svejnohatomas.swansea.newsaggregator.helper

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.google.gson.JsonObject
import com.koushikdutta.ion.Ion
import io.github.svejnohatomas.swansea.newsaggregator.interfaces.IRefresh
import io.github.svejnohatomas.swansea.newsaggregator.interfaces.IRefreshObserver
import io.github.svejnohatomas.swansea.newsaggregator.model.Article
import io.github.svejnohatomas.swansea.newsaggregator.ui.category.CategoryActivity
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * A helper class to refresh news articles.
 *
 * @param context The context of the RefreshHelper instance.
 *
 * @author Tomas Svejnoha (987451@swansea.ac.uk)
 * @version 1.0.0
 * */
class ApiHelper(private val context: Context) : IRefresh {

    /** An instance of DatabaseHelper. */
    private val databaseHelper: DatabaseHelper = DatabaseHelper(context)
    /** A list of IRefresh observers. */
    override val observers: MutableList<IRefreshObserver> = ApiHelper.observers

    /**
     * Refreshes news articles.
     *
     * @param userTriggered Indicates whether the refresh was triggered by the user or the application.
     * */
    fun refresh(userTriggered: Boolean) {
        refreshTopHeadlines(context, userTriggered, getApiKey()!!)
        refreshMyNews(context, userTriggered, getApiKey()!!)
    }

    /**
     * Searches for articles.
     *
     * @param query The search query.
     * @param from From which date and time to look for articles.
     * @param to To which data and time to look for articles.
     * @param searchInTitle A flag indicating to search for the query in titles of articles.
     * @param searchInDescription A flag indicating to search for the query in description of articles.
     * @param searchInContent A flag indicating to search for the query in content of articles.
     * */
    fun search(query: String, from: LocalDateTime?, to: LocalDateTime?,
               searchInTitle: Boolean, searchInDescription: Boolean, searchInContent: Boolean,
               language: String) {
        val encodedQuery = Uri.encode(query)

        var uri = "${BASE_URL}/v2/everything?q=$encodedQuery"

        if (from != null && to != null) {
            val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(ZoneOffset.of("Z"))

            val fromQueryParam = from.format(formatter)
            val toQueryParam = to.format(formatter)
            uri += "&from=$fromQueryParam"
            uri += "&to=$toQueryParam"
        }

        if (!searchInTitle && !searchInContent && searchInDescription) {
            uri += "&searchIn=description"
        } else if (!searchInTitle && searchInContent && !searchInDescription) {
            uri += "&searchIn=content"
        } else if (!searchInTitle && searchInContent && searchInDescription) {
            uri += "&searchIn=content,description"
        } else if (searchInTitle && !searchInContent && !searchInDescription) {
            uri += "&searchIn=title"
        } else if (searchInTitle && !searchInContent && searchInDescription) {
            uri += "&searchIn=title,description"
        } else if (searchInTitle && searchInContent && !searchInDescription) {
            uri += "&searchIn=title,content"
        }

        if (language != "all") {
            uri += "&language=$language"
        }

        search(uri, SearchMode.NEW_SEARCH)
    }

    /**
     * Searches for articles.
     *
     * @param uri The search URI.
     * @param searchMode The search mode.
     * */
    fun search(uri: String, searchMode: SearchMode) {
        search(uri, searchMode, getApiKey()!!)
    }
    /**
     * Searches for articles.
     *
     * @param uri The search URI.
     * @param searchMode The search mode.
     * @param apiKey An API key for the NewsAPI.
     * */
    fun search(uri: String, searchMode: SearchMode, apiKey: String) {
        /**
         * This block uses Ion library created by Koushik Dutta (https://github.com/koush),
         * distributed under the APACHE LICENSE, VERSION 2.0 and available at https://github.com/koush/ion.
         * */
        Ion.with(context)
            .load(uri)
            .userAgent(USER_AGENT)
            .setHeader("X-Api-Key", apiKey)
            .asJsonObject()
            .setCallback { e, result ->
                if (e != null) {
                    Log.e(LOG_TAG, e.stackTraceToString())
                } else {
                    if (isRateError(result)) {
                        getApiKey(apiKey)?.let { search(uri, searchMode, it) }
                    } else {
                        // Clear existing database entries
                        databaseHelper.deleteArticles(null, "search", null)

                        val articles = mutableListOf<Article>()

                        val unsafeArticles = result?.getAsJsonArray("articles")
                        if (result != null && unsafeArticles != null && !unsafeArticles.isJsonNull) {
                            unsafeArticles.forEach { article ->
                                val item = article.asJsonObject

                                articles.add(
                                    Article(
                                        getSourceId(item),
                                        getSourceName(item),
                                        getAuthor(item),
                                        getTitle(item),
                                        getDescription(item),
                                        getUrl(item),
                                        getUrlToImage(item),
                                        getPublishedAt(item),
                                        getContent(item),
                                        null,
                                        "search"
                                    )
                                )
                            }

                            databaseHelper.addArticles(articles)

                            if (searchMode == SearchMode.NEW_SEARCH) {
                                val intent = Intent(context, CategoryActivity::class.java)
                                intent.putExtra("category", "search")
                                intent.putExtra("refreshUri", uri)
                                context.startActivity(intent)
                            } else {
                                notifyObservers(true)
                            }
                        }
                    }
                }
            }
    }

    /**
     * Gets an API key.
     *
     * @param currentApiKey The current API key.
     *
     * @return An API key.
     * */
    private fun getApiKey(currentApiKey: String? = null): String? {
        return if (currentApiKey == null) {
            apiKeys[0]
        } else {
            val position = apiKeys.indexOf(currentApiKey)
            if (position < apiKeys.size - 1) {
                return apiKeys[position + 1]
            } else {
                return null
            }
        }
    }
    /**
     * Checks an API rate was hit.
     *
     * @param jsonObject The response.
     *
     * @return true if rate was hit; otherwise false.
     * */
    private fun isRateError(jsonObject: JsonObject): Boolean {
        return getNullableStringJsonMember(jsonObject, "code") == "rateLimited"
    }

    /**
     * Refreshes Top Headlines.
     *
     * @param context A context of the refresh.
     * @param userTriggered Indicates whether the refresh was triggered by the user or the application.
     * @param apiKey An API key for the NewsAPI.
     * */
    private fun refreshTopHeadlines(context: Context, userTriggered: Boolean, apiKey: String) {
        val country = PreferenceHelper(context).getCountry()

        /**
         * This block uses Ion library created by Koushik Dutta (https://github.com/koush),
         * distributed under the APACHE LICENSE, VERSION 2.0 and available at https://github.com/koush/ion.
         * */
        Ion.with(context)
            .load("${BASE_URL}/v2/top-headlines?country=${country}")
            .userAgent(USER_AGENT)
            .setHeader("X-Api-Key", apiKey)
            .asJsonObject()
            .setCallback { e, result ->
                if (e != null) {
                    Log.e(LOG_TAG, e.stackTraceToString())
                } else {
                    if (isRateError(result)) {
                        getApiKey(apiKey)?.let { refreshTopHeadlines(context, userTriggered, it) }
                    } else {
                        val articles = mutableListOf<Article>()

                        val unsafeArticles = result?.getAsJsonArray("articles")
                        if (result != null && unsafeArticles != null && !unsafeArticles.isJsonNull) {
                            unsafeArticles.forEach {
                                val item = it.asJsonObject

                                articles.add(Article(getSourceId(item), getSourceName(item), getAuthor(item),
                                    getTitle(item), getDescription(item), getUrl(item), getUrlToImage(item),
                                    getPublishedAt(item), getContent(item), country, "top-headlines"))
                            }

                            val newArticles = databaseHelper.addArticles(articles)

                            if (newArticles.isNotEmpty()) {
                                NotificationHelper().publishNotification(context, newArticles)

                                notifyObservers(userTriggered)
                            }
                        }
                    }
                }
            }
    }
    /**
     * Refreshes My News.
     *
     * @param context A context of the refresh.
     * @param userTriggered Indicates whether the refresh was triggered by the user or the application.
     * @param apiKey An API key for the NewsAPI.
     * */
    private fun refreshMyNews(context: Context, userTriggered: Boolean, apiKey: String) {
        val preferenceHelper = PreferenceHelper(context)
        val categories = preferenceHelper.getEnabledCategories()
        val country = preferenceHelper.getCountry()

        categories.forEach { category ->
            /**
             * This block uses Ion library created by Koushik Dutta (https://github.com/koush),
             * distributed under the APACHE LICENSE, VERSION 2.0 and available at https://github.com/koush/ion.
             * */
            Ion.with(context)
                .load("${BASE_URL}/v2/top-headlines?country=${country}&category=${category}")
                .userAgent(USER_AGENT)
                .setHeader("X-Api-Key", apiKey)
                .asJsonObject()
                .setCallback { e, result ->
                    if (e != null) {
                        Log.e(LOG_TAG, e.stackTraceToString())
                    } else {
                        if (isRateError(result)) {
                            getApiKey(apiKey)?.let { refreshMyNews(context, userTriggered, it) }
                        } else {
                            val articles = mutableListOf<Article>()

                            val unsafeArticles = result?.getAsJsonArray("articles")
                            if (result != null && unsafeArticles != null && !unsafeArticles.isJsonNull) {
                                unsafeArticles.forEach { article ->
                                    val item = article.asJsonObject

                                    articles.add(
                                        Article(
                                            getSourceId(item),
                                            getSourceName(item),
                                            getAuthor(item),
                                            getTitle(item),
                                            getDescription(item),
                                            getUrl(item),
                                            getUrlToImage(item),
                                            getPublishedAt(item),
                                            getContent(item),
                                            country,
                                            category
                                        )
                                    )
                                }

                                val newArticles = databaseHelper.addArticles(articles)

                                if (newArticles.isNotEmpty()) {
                                    NotificationHelper().publishNotification(context, newArticles)

                                    notifyObservers(userTriggered)
                                }
                            }
                        }
                    }
                }
        }
    }

    /**
     * Gets a source Id of an article.
     *
     * @param jsonObject A source JSON object.
     *
     * @return A source Id or null.
     * */
    private fun getSourceId(jsonObject: JsonObject): String? {
        val sourceJsonObject: JsonObject = jsonObject.getAsJsonObject("source")
        return getNullableStringJsonMember(sourceJsonObject, "id")
            ?: getSourceName(jsonObject)?.lowercase()?.replace(" ", "-")
    }
    /**
     * Gets a source name of an article.
     *
     * @param jsonObject A source JSON object.
     *
     * @return A source name or null.
     * */
    private fun getSourceName(jsonObject: JsonObject): String? {
        val sourceJsonObject: JsonObject = jsonObject.getAsJsonObject("source")
        return getNullableStringJsonMember(sourceJsonObject, "name")
    }

    /**
     * Gets an author of an article.
     *
     * @param jsonObject A source JSON object.
     *
     * @return An author or null.
     * */
    private fun getAuthor(jsonObject: JsonObject): String? {
        return getNullableStringJsonMember(jsonObject, "author")
    }
    /**
     * Gets a title of an article.
     *
     * @param jsonObject A source JSON object.
     *
     * @return A title or null.
     * */
    private fun getTitle(jsonObject: JsonObject): String? {
        return getNullableStringJsonMember(jsonObject, "title")
    }
    /**
     * Gets a description of an article.
     *
     * @param jsonObject A source JSON object.
     *
     * @return A description or null.
     * */
    private fun getDescription(jsonObject: JsonObject): String? {
        return getNullableStringJsonMember(jsonObject, "description")
    }
    /**
     * Gets a URL of an article.
     *
     * @param jsonObject A source JSON object.
     *
     * @return A URL or null.
     * */
    private fun getUrl(jsonObject: JsonObject): String? {
        return getNullableStringJsonMember(jsonObject, "url")
    }
    /**
     * Gets an image URL of an article.
     *
     * @param jsonObject A source JSON object.
     *
     * @return An image URL or null.
     * */
    private fun getUrlToImage(jsonObject: JsonObject): String? {
        return getNullableStringJsonMember(jsonObject, "urlToImage")
    }
    /**
     * Gets a number of ticks since epoch when the article was published.
     *
     * @param jsonObject A source JSON object.
     *
     * @return A number of ticks or null.
     * */
    private fun getPublishedAt(jsonObject: JsonObject): Long? {
        val publishedAt = getNullableStringJsonMember(jsonObject,"publishedAt")
        return if (publishedAt == null) {
            null
        } else {
            convertIsoInstantToEpochTicks(publishedAt)
        }
    }
    /**
     * Gets a content of an article.
     *
     * @param jsonObject A source JSON object.
     *
     * @return A content or null.
     * */
    private fun getContent(jsonObject: JsonObject): String? {
        return getNullableStringJsonMember(jsonObject,"content")
    }

    /**
     * Safely return a string value of possibly non-existent JSON member.
     *
     * @param jsonObject A source JSON object.
     * @param memberName A name of the JSON member to retrieve.
     *
     * @return A value of the JSON member or null.
     * */
    private fun getNullableStringJsonMember(jsonObject: JsonObject, memberName: String): String? {
        val member = jsonObject.get(memberName)
        return if (member != null && !member.isJsonNull) {
            member.asString
        } else {
            null
        }
    }

    /**
     * Converts an ISO Instant to a number of ticks since epoch.
     *
     * @param isoInstant An ISO Instant value.
     *
     * @return A number of ticks.
     * */
    private fun convertIsoInstantToEpochTicks(isoInstant: String): Long {
        return try {
            val formatter = DateTimeFormatter.ISO_INSTANT.withZone(ZoneOffset.of("Z"))
            val parsedDateTime = LocalDateTime.parse(isoInstant, formatter)
            parsedDateTime.toEpochSecond(ZoneOffset.of("Z")) * NUMBER_OF_TICKS_IN_SECOND
        } catch (e: Exception) {
            Log.e(LOG_TAG, e.message ?: "")
            0
        }
    }

    companion object {
        // I will destroy this API when the marking deadline is over.
        private const val BASE_URL = "https://newsapi.org/v2"
        private const val USER_AGENT = "Mozilla/5.0 (Windows NT 5.1; rv:7.0.1) Gecko/20100101 Firefox/7.0.1" // Pretend Firefox 7 on Windows XP
        private const val LOG_TAG = "ApiHelper"
        private const val NUMBER_OF_TICKS_IN_SECOND = 10000000

        // API limit? Which API limit?
        // Also, never store your API keys like this.
        private val apiKeys = listOf("your-api-keys")

        private val observers: MutableList<IRefreshObserver> = mutableListOf()

        enum class SearchMode {
            NEW_SEARCH,
            REFRESH,
        }
    }
}