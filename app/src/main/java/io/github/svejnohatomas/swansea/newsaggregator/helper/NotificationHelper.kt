package io.github.svejnohatomas.swansea.newsaggregator.helper

import android.app.*
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import io.github.svejnohatomas.swansea.newsaggregator.R
import io.github.svejnohatomas.swansea.newsaggregator.model.Article
import java.util.stream.Collectors

/**
 * A helper class to manage and publish notifications.
 *
 * @author Tomas Svejnoha (987451@swansea.ac.uk)
 * @version 1.0.0
 * */
class NotificationHelper {

    /**
     * Publishes a notification.
     *
     * @param context A context from which to publish the notification.
     * @param articles A list of articles for which to publish notifications.
     * */
    fun publishNotification(context: Context, articles: List<Article>) {
        val preferenceHelper = PreferenceHelper(context)

        if (!preferenceHelper.areNotificationsEnabled())
            return

        // Get distinct list of categories (except Top Headlines)
        val categories: List<String?> = articles.stream()
            .map { it.category }
            .distinct()
            .collect(Collectors.toList())

        // Group notifications together (not all devices support group rendering)
        for (category in categories) {

            // Ignore null values if some magically occur
            if (category == null || !preferenceHelper.areNotificationsEnabledForCategory(category))
                continue

            createNotificationChannelForCategoryIfNotExists(context, category)

            articles.filter { it.category.equals(category) }.forEach {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = (Uri.parse(it.url))
                val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

                val newArticleNotification = NotificationCompat.Builder(context, getCategoryNotificationChannelId(category))
                    .setSmallIcon(R.drawable.ic_baseline_newspaper_24)
                    .setContentTitle(it.title)
                    .setContentText(it.description)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setGroup(getCategoryNotificationGroupId(category))
                    .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY)
                    .setGroupSummary(false)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build()

                NotificationManagerCompat.from(context).notify(it.hashCode(), newArticleNotification)
            }

            // https://developer.android.com/develop/ui/views/notifications/group#set_a_group_summary
            val categorySummaryNotification = NotificationCompat.Builder(context, getCategoryNotificationChannelId(category))
                .setSmallIcon(R.drawable.ic_baseline_newspaper_24)
                .setContentTitle("") // TODO("Add Content title")
                .setGroup(getCategoryNotificationGroupId(category))
                .setGroupSummary(true)
                .build()

            NotificationManagerCompat.from(context).notify(category.hashCode(), categorySummaryNotification)
        }
    }

    /**
     * Creates a notification channel for a category if it does not already exist.
     *
     * @param context A context from which to create the notification channel.
     * @param category A category for which to create the notification channel.
     * */
    private fun createNotificationChannelForCategoryIfNotExists(context: Context, category: String) {
        // Creating an existing channel performs no action
        // https://developer.android.com/develop/ui/views/notifications/build-notification

        val channelId = getCategoryNotificationChannelId(category)
        val name = getCategoryNotificationChannelName(context, category)

        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create a new channel
        val channel = NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_DEFAULT).apply {
            //description = getCategoryNotificationChannelDescription(context, category)
            group = createNotificationChannelGroup(context, category)
        }
        notificationManager.createNotificationChannel(channel)
    }

    /**
     * Gets a group Id for a notification category.
     *
     * @param category A category for which to return the notification group Id.
     *
     * @return A notification group Id.
     * */
    private fun getCategoryNotificationGroupId(category: String): String {
        return "$GROUP_ID_BASE.$category"
    }
    /**
     * Gets a notification channel Id for a category.
     *
     * @param category A category for which to return a notification channel Id.
     *
     * @return A notification channel Id.
     * */
    private fun getCategoryNotificationChannelId(category: String): String {
        return "$CHANNEL_ID_BASE.$category"
    }
    /**
     * Gets a notification channel name for a category.
     *
     * @param context A context from which to take the notification channel name.
     * @param category A category for which to return a notification channel name.
     *
     * @throws IllegalArgumentException If category is not supported.
     *
     * @return A notification channel name.
     * */
    private fun getCategoryNotificationChannelName(context: Context, category: String): String {
        return when (category) {
            "top-headlines" -> context.getString(R.string.top_headlines)
            "general" -> context.getString(R.string.general)
            "business" -> context.getString(R.string.business)
            "technology" -> context.getString(R.string.technology)
            "science" -> context.getString(R.string.science)
            "health" -> context.getString(R.string.health)
            "entertainment" -> context.getString(R.string.entertainment)
            "sports" -> context.getString(R.string.sports)
            else -> throw IllegalArgumentException("Unsupported category")
        }
    }
    /**
     * Gets a notification channel description for a category.
     *
     * @param context A context from which to take the notification channel description.
     * @param category A category for which to return a notification channel description.
     *
     * @throws IllegalArgumentException If category is not supported.
     *
     * @return A notification channel description.
     * */
    private fun getCategoryNotificationChannelDescription(context: Context, category: String): String {
        TODO("Implement")

        return when (category) {
            "" -> ""
            else -> throw IllegalArgumentException("Unsupported category")
        }
    }

    /**
     * Creates a notification channel group.
     *
     * @param context A context from which to get notification channel's group name.
     * @param category A category for which to create a notification channel group.
     *
     * @return A notification channel group Id.
     * */
    private fun createNotificationChannelGroup(context: Context, category: String): String {
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        return if (category == "top-headlines") {
            // Top Headlines
            val groupName = context.getString(R.string.top_headlines)
            val notificationChannelGroup = NotificationChannelGroup(CHANNEL_GROUP_ID_TOP_HEADLINES, groupName)
            notificationManager.createNotificationChannelGroup(notificationChannelGroup)

            CHANNEL_GROUP_ID_TOP_HEADLINES
        } else {
            // My News
            val groupName = context.getString(R.string.my_news)
            val notificationChannelGroup = NotificationChannelGroup(CHANNEL_GROUP_ID_MY_NEWS, groupName)
            notificationManager.createNotificationChannelGroup(notificationChannelGroup)

            CHANNEL_GROUP_ID_MY_NEWS
        }
    }

    companion object {
        private const val CHANNEL_ID_BASE = "io.github.svejnohatomas.swansea.newsaggregator.notification.channel"
        private const val GROUP_ID_BASE = "io.github.svejnohatomas.swansea.newsaggregator.notification.group"

        private const val CHANNEL_GROUP_ID_MY_NEWS = "notification-group-my-news"
        private const val CHANNEL_GROUP_ID_TOP_HEADLINES = "notification-group-top-headlines"
    }
}