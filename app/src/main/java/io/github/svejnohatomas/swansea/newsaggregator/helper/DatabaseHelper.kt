package io.github.svejnohatomas.swansea.newsaggregator.helper

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import io.github.svejnohatomas.swansea.newsaggregator.model.Article

/**
 * A helper class to access SQLite database.
 *
 * @param context A Context for which to create the DatabaseHelper.
 *
 * @author Tomas Svejnoha (987451@swansea.ac.uk)
 * @version 1.0.0
 * */
class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        val createTableArticlesSql = "CREATE TABLE $TABLE_ARTICLES(" +
                "$TABLE_ARTICLES_COLUMN_SOURCE_ID TEXT," +
                "$TABLE_ARTICLES_COLUMN_SOURCE_NAME TEXT," +
                "$TABLE_ARTICLES_COLUMN_AUTHOR TEXT," +
                "$TABLE_ARTICLES_COLUMN_TITLE TEXT," +
                "$TABLE_ARTICLES_COLUMN_DESCRIPTION TEXT," +
                "$TABLE_ARTICLES_COLUMN_URL TEXT," +
                "$TABLE_ARTICLES_COLUMN_URL_TO_IMAGE TEXT," +
                "$TABLE_ARTICLES_COLUMN_PUBLISHED_AT INTEGER," +
                "$TABLE_ARTICLES_COLUMN_CONTENT TEXT," +
                "$TABLE_ARTICLES_COLUMN_COUNTRY TEXT," +
                "$TABLE_ARTICLES_COLUMN_CATEGORY TEXT)"

        db?.execSQL(createTableArticlesSql)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_ARTICLES")
        onCreate(db)
    }

    /**
     * Adds articles to the database.
     *
     * @param articles A list of articles to add to the database.
     *
     * @return A list of articles added to the database.
     * */
    fun addArticles(articles: List<Article>): List<Article> {
        val db = writableDatabase

        val addedArticles = mutableListOf<Article>()

        articles.forEach {
            val sqlQuery = "SELECT * FROM $TABLE_ARTICLES WHERE $TABLE_ARTICLES_COLUMN_URL = '${it.url}' AND $TABLE_ARTICLES_COLUMN_CATEGORY = '${it.category}'"

            val cursor = db.rawQuery(sqlQuery, null)
            if (!cursor.moveToFirst()) {
                val values = ContentValues()
                values.put(TABLE_ARTICLES_COLUMN_SOURCE_ID, it.sourceId)
                values.put(TABLE_ARTICLES_COLUMN_SOURCE_NAME, it.sourceName)
                values.put(TABLE_ARTICLES_COLUMN_AUTHOR, it.author)
                values.put(TABLE_ARTICLES_COLUMN_TITLE, it.title)
                values.put(TABLE_ARTICLES_COLUMN_DESCRIPTION, it.description)
                values.put(TABLE_ARTICLES_COLUMN_URL, it.url)
                values.put(TABLE_ARTICLES_COLUMN_URL_TO_IMAGE, it.urlToImage)
                values.put(TABLE_ARTICLES_COLUMN_PUBLISHED_AT, it.publishedAt)
                values.put(TABLE_ARTICLES_COLUMN_CONTENT, it.content)

                // Metadata created by the application
                values.put(TABLE_ARTICLES_COLUMN_COUNTRY, it.country)
                values.put(TABLE_ARTICLES_COLUMN_CATEGORY, it.category)

                db.insert(TABLE_ARTICLES, null, values)

                addedArticles.add(it)
            }
            cursor.close()
        }

        return addedArticles
    }

    /**
     * Gets articles from the database, ordered from the latest to the oldest, using the provided parameters.
     *
     * @param country A country for which to get the articles. All countries if null.
     * @param category A category for which to get the articles. All categories if null.
     * @param limit A maximum number of articles to return. No limit if null.
     *
     * @return A list of matching articles.
     * */
    fun getArticles(country: String?, category: String?, limit: Int?) : List<Article> {
        var sqlQuery = "SELECT * FROM $TABLE_ARTICLES"

        if (country == null && category == null) {
            // Do nothing
        } else if (country == null && category != null) {
            sqlQuery += " WHERE $TABLE_ARTICLES_COLUMN_CATEGORY = '$category'"
        } else if (country != null && category == null) {
            sqlQuery += " WHERE $TABLE_ARTICLES_COLUMN_COUNTRY = '$country'"
        } else {
            sqlQuery += " WHERE $TABLE_ARTICLES_COLUMN_COUNTRY = '$country' AND $TABLE_ARTICLES_COLUMN_CATEGORY = '$category'"
        }

        sqlQuery += " ORDER BY $TABLE_ARTICLES_COLUMN_PUBLISHED_AT DESC"

        if (limit != null && limit > 0) {
            sqlQuery += " LIMIT $limit"
        }

        val db = readableDatabase
        val articles = arrayListOf<Article>()
        val cursor = db.rawQuery(sqlQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val entitySourceId: String? = cursor.getStringOrNull(0)
                val entitySourceName: String? = cursor.getStringOrNull(1)
                val entityAuthor: String? = cursor.getStringOrNull(2)
                val entityTitle: String? = cursor.getStringOrNull(3)
                val entityDescription: String? = cursor.getStringOrNull(4)
                val entityUrl: String? = cursor.getStringOrNull(5)
                val entityUrlToImage: String? = cursor.getStringOrNull(6)
                val entityPublishedAt: Long? = cursor.getLongOrNull(7)
                val entityContent: String? = cursor.getStringOrNull(8)
                val entityCountry: String? = cursor.getStringOrNull(9)
                val entityCategory: String? = cursor.getStringOrNull(10)

                val article = Article(
                    entitySourceId, entitySourceName, entityAuthor, entityTitle, entityDescription, entityUrl,
                    entityUrlToImage, entityPublishedAt, entityContent, entityCountry, entityCategory)
                articles.add(article)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return articles
    }

    /**
     * Deletes articles from the database matching the given parameters.
     *
     * @param country if provided, all articles from this country will be deleted.
     * @param category if provided, all articles in this category will be deleted.
     * @param epochTicks the time represented in ticks since the epoch. When provided, all articles below this epoch will be deleted.
     * */
    fun deleteArticles(country: String?, category: String?, epochTicks: Long?) {
        var sqlQuery = "DELETE FROM $TABLE_ARTICLES"

        if (country == null && category == null && epochTicks == null) {
            // Do nothing
        } else if (country == null && category == null && epochTicks != null) {
            sqlQuery += " WHERE $TABLE_ARTICLES_COLUMN_PUBLISHED_AT < $epochTicks"
        } else if (country == null && category != null && epochTicks == null) {
            sqlQuery += " WHERE $TABLE_ARTICLES_COLUMN_CATEGORY = '$category'"
        } else if (country == null && category != null && epochTicks != null) {
            sqlQuery += " WHERE $TABLE_ARTICLES_COLUMN_CATEGORY = '$category'" +
                    " AND $TABLE_ARTICLES_COLUMN_PUBLISHED_AT < $epochTicks"
        } else if (country != null && category == null && epochTicks == null) {
            sqlQuery += " WHERE $TABLE_ARTICLES_COLUMN_COUNTRY = '$country'"
        } else if (country != null && category == null && epochTicks != null) {
            sqlQuery += " WHERE $TABLE_ARTICLES_COLUMN_COUNTRY = '$country'" +
                    " AND $TABLE_ARTICLES_COLUMN_PUBLISHED_AT < $epochTicks"
        } else if (country != null && category != null && epochTicks == null) {
            sqlQuery += " WHERE $TABLE_ARTICLES_COLUMN_COUNTRY = '$country'" +
                    " AND $TABLE_ARTICLES_COLUMN_CATEGORY = '$category'"
        } else {
            sqlQuery += " WHERE $TABLE_ARTICLES_COLUMN_COUNTRY = '$country'" +
                    " AND $TABLE_ARTICLES_COLUMN_CATEGORY = '$category'" +
                    " AND $TABLE_ARTICLES_COLUMN_PUBLISHED_AT < $epochTicks"
        }

        val db = writableDatabase
        db.execSQL(sqlQuery)
    }

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "DbNewsArticles"

        private const val TABLE_ARTICLES = "TbArticles"
        private const val TABLE_ARTICLES_COLUMN_SOURCE_ID = "source_id"
        private const val TABLE_ARTICLES_COLUMN_SOURCE_NAME = "source_name"
        private const val TABLE_ARTICLES_COLUMN_AUTHOR = "author"
        private const val TABLE_ARTICLES_COLUMN_TITLE = "title"
        private const val TABLE_ARTICLES_COLUMN_DESCRIPTION = "description"
        private const val TABLE_ARTICLES_COLUMN_URL = "url"
        private const val TABLE_ARTICLES_COLUMN_URL_TO_IMAGE = "url_to_image"
        private const val TABLE_ARTICLES_COLUMN_PUBLISHED_AT = "published_at"
        private const val TABLE_ARTICLES_COLUMN_CONTENT = "content"

        private const val TABLE_ARTICLES_COLUMN_COUNTRY = "country"
        private const val TABLE_ARTICLES_COLUMN_CATEGORY = "category"
    }
}