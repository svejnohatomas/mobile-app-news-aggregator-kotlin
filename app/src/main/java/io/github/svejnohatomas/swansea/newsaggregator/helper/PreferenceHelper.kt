package io.github.svejnohatomas.swansea.newsaggregator.helper

import android.content.Context
import androidx.preference.PreferenceManager

/**
 * A helper class to retrieve user preferences.
 *
 * @param context A context from which to retrieve the preferences.
 *
 * @author Tomas Svejnoha (987451@swansea.ac.uk)
 * @version 1.0.0
 * */
class PreferenceHelper(val context: Context) {
    /**
     * Checks whether Auto Refresh is enabled.
     *
     * @return true if enabled; otherwise false.
     * */
    fun isAutoRefreshEnabled(): Boolean {
        return PreferenceManager
            .getDefaultSharedPreferences(context)
            .getBoolean(AUTO_REFRESH_ENABLED, AUTO_REFRESH_ENABLED_DEFAULT)
    }
    /**
     * Gets an Auto Refresh interval in minutes.
     *
     * @return An Auto Refresh interval.
     * */
    fun getAutoRefreshInterval(): Int {
        return PreferenceManager
            .getDefaultSharedPreferences(context)
            .getString(AUTO_REFRESH_INTERVAL, AUTO_REFRESH_INTERVAL_DEFAULT.toString())
            ?.toInt() ?: AUTO_REFRESH_INTERVAL_DEFAULT
    }

    /**
     * Gets user preferred application language.
     *
     * @return An application language.
     * */
    fun getApplicationLanguage(): String {
        return PreferenceManager
            .getDefaultSharedPreferences(context)
            .getString(LANGUAGE, LANGUAGE_DEFAULT) ?: LANGUAGE_DEFAULT
    }
    /**
     * Gets an Alpha-2 country code for which to fetch news articles.
     *
     * @return A country code.
     * */
    fun getCountry(): String {
        return PreferenceManager
            .getDefaultSharedPreferences(context)
            .getString(COUNTRY, COUNTRY_DEFAULT) ?: COUNTRY_DEFAULT
    }

    /**
     * Checks whether notifications are enabled.
     *
     * @return true if enabled; otherwise false.
     * */
    fun areNotificationsEnabled(): Boolean {
        return PreferenceManager
            .getDefaultSharedPreferences(context)
            .getBoolean(NOTIFICATION_ENABLED, NOTIFICATION_ENABLED_DEFAULT)
    }
    /**
     * Checks whether notification are enabled for a category.
     *
     * @param category A category for which to check whether notifications are enabled.
     *
     * @return true if enabled; otherwise false.
     * */
    fun areNotificationsEnabledForCategory(category: String): Boolean {
        return when (category) {
            "top-headlines" -> areNotificationsEnabledForTopHeadlines()
            "general" -> areNotificationsEnabledForGeneralCategory()
            "business" -> areNotificationsEnabledForBusinessCategory()
            "technology" -> areNotificationsEnabledForTechnologyCategory()
            "science" -> areNotificationsEnabledForScienceCategory()
            "health" -> areNotificationsEnabledForHealthCategory()
            "entertainment" -> areNotificationsEnabledForEntertainmentCategory()
            "sports" -> areNotificationsEnabledForSportsCategory()
            else -> throw IllegalArgumentException("Unsupported category")
        }
    }
    /**
     * Checks whether notifications are enabled for Top Headlines.
     *
     * @return true if enabled; otherwise false.
     * */
    private fun areNotificationsEnabledForTopHeadlines(): Boolean {
        return PreferenceManager
            .getDefaultSharedPreferences(context)
            .getBoolean(NOTIFICATION_TOP_HEADLINES_ENABLED, NOTIFICATION_TOP_HEADLINES_ENABLED_DEFAULT)
    }
    /**
     * Checks whether notifications are enabled for General category.
     *
     * @return true if enabled; otherwise false.
     * */
    private fun areNotificationsEnabledForGeneralCategory(): Boolean {
        return PreferenceManager
            .getDefaultSharedPreferences(context)
            .getBoolean(NOTIFICATION_GENERAL_ENABLED, NOTIFICATION_GENERAL_ENABLED_DEFAULT)
    }
    /**
     * Checks whether notifications are enabled for Business category.
     *
     * @return true if enabled; otherwise false.
     * */
    private fun areNotificationsEnabledForBusinessCategory(): Boolean {
        return PreferenceManager
            .getDefaultSharedPreferences(context)
            .getBoolean(NOTIFICATION_BUSINESS_ENABLED, NOTIFICATION_BUSINESS_ENABLED_DEFAULT)
    }
    /**
     * Checks whether notifications are enabled for Technology category.
     *
     * @return true if enabled; otherwise false.
     * */
    private fun areNotificationsEnabledForTechnologyCategory(): Boolean {
        return PreferenceManager
            .getDefaultSharedPreferences(context)
            .getBoolean(NOTIFICATION_TECHNOLOGY_ENABLED, NOTIFICATION_TECHNOLOGY_ENABLED_DEFAULT)
    }
    /**
     * Checks whether notifications are enabled for Science category.
     *
     * @return true if enabled; otherwise false.
     * */
    private fun areNotificationsEnabledForScienceCategory(): Boolean {
        return PreferenceManager
            .getDefaultSharedPreferences(context)
            .getBoolean(NOTIFICATION_SCIENCE_ENABLED, NOTIFICATION_SCIENCE_ENABLED_DEFAULT)
    }
    /**
     * Checks whether notifications are enabled for Health category.
     *
     * @return true if enabled; otherwise false.
     * */
    private fun areNotificationsEnabledForHealthCategory(): Boolean {
        return PreferenceManager
            .getDefaultSharedPreferences(context)
            .getBoolean(NOTIFICATION_HEALTH_ENABLED, NOTIFICATION_HEALTH_ENABLED_DEFAULT)
    }
    /**
     * Checks whether notifications are enabled for Entertainment category.
     *
     * @return true if enabled; otherwise false.
     * */
    private fun areNotificationsEnabledForEntertainmentCategory(): Boolean {
        return PreferenceManager
            .getDefaultSharedPreferences(context)
            .getBoolean(NOTIFICATION_ENTERTAINMENT_ENABLED, NOTIFICATION_ENTERTAINMENT_ENABLED_DEFAULT)
    }
    /**
     * Checks whether notifications are enabled for Sports category.
     *
     * @return true if enabled; otherwise false.
     * */
    private fun areNotificationsEnabledForSportsCategory(): Boolean {
        return PreferenceManager
            .getDefaultSharedPreferences(context)
            .getBoolean(NOTIFICATION_SPORTS_ENABLED, NOTIFICATION_SPORTS_ENABLED_DEFAULT)
    }

    /**
     * Gets a list of enabled news categories.
     *
     * @return A list of categories.
     * */
    fun getEnabledCategories(): List<String> {
        val categories = mutableListOf<String>()

        if (isTopicEnabledForGeneralCategory())
            categories.add("general")
        if (isTopicEnabledForBusinessCategory())
            categories.add("business")
        if (isTopicEnabledForTechnologyCategory())
            categories.add("technology")
        if (isTopicEnabledForScienceCategory())
            categories.add("science")
        if (isTopicEnabledForHealthCategory())
            categories.add("health")
        if (isTopicEnabledForEntertainmentCategory())
            categories.add("entertainment")
        if (isTopicEnabledForSportsCategory())
            categories.add("sports")

        return categories
    }
    /**
     * Checks whether General category topic is enabled.
     *
     * @return true if enabled; otherwise false.
     * */
    private fun isTopicEnabledForGeneralCategory(): Boolean {
        return PreferenceManager
            .getDefaultSharedPreferences(context)
            .getBoolean(TOPIC_GENERAL_ENABLED, TOPIC_GENERAL_ENABLED_DEFAULT)
    }
    /**
     * Checks whether Business category topic is enabled.
     *
     * @return true if enabled; otherwise false.
     * */
    private fun isTopicEnabledForBusinessCategory(): Boolean {
        return PreferenceManager
            .getDefaultSharedPreferences(context)
            .getBoolean(TOPIC_BUSINESS_ENABLED, TOPIC_BUSINESS_ENABLED_DEFAULT)
    }
    /**
     * Checks whether Technology category topic is enabled.
     *
     * @return true if enabled; otherwise false.
     * */
    private fun isTopicEnabledForTechnologyCategory(): Boolean {
        return PreferenceManager
            .getDefaultSharedPreferences(context)
            .getBoolean(TOPIC_TECHNOLOGY_ENABLED, TOPIC_TECHNOLOGY_ENABLED_DEFAULT)
    }
    /**
     * Checks whether Science category topic is enabled.
     *
     * @return true if enabled; otherwise false.
     * */
    private fun isTopicEnabledForScienceCategory(): Boolean {
        return PreferenceManager
            .getDefaultSharedPreferences(context)
            .getBoolean(TOPIC_SCIENCE_ENABLED, TOPIC_SCIENCE_ENABLED_DEFAULT)
    }
    /**
     * Checks whether Health category topic is enabled.
     *
     * @return true if enabled; otherwise false.
     * */
    private fun isTopicEnabledForHealthCategory(): Boolean {
        return PreferenceManager
            .getDefaultSharedPreferences(context)
            .getBoolean(TOPIC_HEALTH_ENABLED, TOPIC_HEALTH_ENABLED_DEFAULT)
    }
    /**
     * Checks whether Entertainment category topic is enabled.
     *
     * @return true if enabled; otherwise false.
     * */
    private fun isTopicEnabledForEntertainmentCategory(): Boolean {
        return PreferenceManager
            .getDefaultSharedPreferences(context)
            .getBoolean(TOPIC_ENTERTAINMENT_ENABLED, TOPIC_ENTERTAINMENT_ENABLED_DEFAULT)
    }
    /**
     * Checks whether Sports category topic is enabled.
     *
     * @return true if enabled; otherwise false.
     * */
    private fun isTopicEnabledForSportsCategory(): Boolean {
        return PreferenceManager
            .getDefaultSharedPreferences(context)
            .getBoolean(TOPIC_SPORTS_ENABLED, TOPIC_SPORTS_ENABLED_DEFAULT)
    }

    companion object {
        private const val AUTO_REFRESH_ENABLED = "preferences_general_refresh_enabled"
        private const val AUTO_REFRESH_ENABLED_DEFAULT = true
        private const val AUTO_REFRESH_INTERVAL = "preferences_general_refresh_interval"
        private const val AUTO_REFRESH_INTERVAL_DEFAULT = 60

        private const val LANGUAGE = "preferences_language"
        private const val LANGUAGE_DEFAULT = "english_uk"
        private const val COUNTRY = "preferences_country"
        private const val COUNTRY_DEFAULT = "gb"

        private const val NOTIFICATION_ENABLED = "enable_notifications"
        private const val NOTIFICATION_ENABLED_DEFAULT = true
        private const val NOTIFICATION_TOP_HEADLINES_ENABLED = "preferences_notification_top_headlines"
        private const val NOTIFICATION_TOP_HEADLINES_ENABLED_DEFAULT = true
        private const val NOTIFICATION_GENERAL_ENABLED = "preferences_notification_general"
        private const val NOTIFICATION_GENERAL_ENABLED_DEFAULT = true
        private const val NOTIFICATION_BUSINESS_ENABLED = "preferences_notification_business"
        private const val NOTIFICATION_BUSINESS_ENABLED_DEFAULT = true
        private const val NOTIFICATION_TECHNOLOGY_ENABLED = "preferences_notification_technology"
        private const val NOTIFICATION_TECHNOLOGY_ENABLED_DEFAULT = true
        private const val NOTIFICATION_SCIENCE_ENABLED = "preferences_notification_science"
        private const val NOTIFICATION_SCIENCE_ENABLED_DEFAULT = true
        private const val NOTIFICATION_HEALTH_ENABLED = "preferences_notification_health"
        private const val NOTIFICATION_HEALTH_ENABLED_DEFAULT = true
        private const val NOTIFICATION_ENTERTAINMENT_ENABLED = "preferences_notification_entertainment"
        private const val NOTIFICATION_ENTERTAINMENT_ENABLED_DEFAULT = true
        private const val NOTIFICATION_SPORTS_ENABLED = "preferences_notification_sports"
        private const val NOTIFICATION_SPORTS_ENABLED_DEFAULT = true

        private const val TOPIC_GENERAL_ENABLED = "preferences_topic_general"
        private const val TOPIC_GENERAL_ENABLED_DEFAULT = true
        private const val TOPIC_BUSINESS_ENABLED = "preferences_topic_business"
        private const val TOPIC_BUSINESS_ENABLED_DEFAULT = true
        private const val TOPIC_TECHNOLOGY_ENABLED = "preferences_topic_technology"
        private const val TOPIC_TECHNOLOGY_ENABLED_DEFAULT = true
        private const val TOPIC_SCIENCE_ENABLED = "preferences_topic_science"
        private const val TOPIC_SCIENCE_ENABLED_DEFAULT = true
        private const val TOPIC_HEALTH_ENABLED = "preferences_topic_health"
        private const val TOPIC_HEALTH_ENABLED_DEFAULT = true
        private const val TOPIC_ENTERTAINMENT_ENABLED = "preferences_topic_entertainment"
        private const val TOPIC_ENTERTAINMENT_ENABLED_DEFAULT = true
        private const val TOPIC_SPORTS_ENABLED = "preferences_topic_sports"
        private const val TOPIC_SPORTS_ENABLED_DEFAULT = true
    }
}