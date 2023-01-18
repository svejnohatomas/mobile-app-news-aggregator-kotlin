package io.github.svejnohatomas.swansea.newsaggregator.helper

import android.content.Context
import io.github.svejnohatomas.swansea.newsaggregator.R
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * A helper class to convert ticks from epoch to text.
 *
 * @author Tomas Svejnoha (987451@swansea.ac.uk)
 * @version 1.0.0
 * */
class DateTimeHelper {
    /**
     * Converts the number of ticks since epoch to a string.
     *
     * @param ticks The number of ticks since epoch.
     * @param context The context from which to retrieve strings.
     *
     * @return A string representing the number of ticks since epoch.
     * */
    fun getTimeTextFromEpochTicks(ticks: Long?, context: Context): String {
        var output = ""

        if (ticks == null) {
            return output
        }

        val ticksNow = LocalDateTime
            .now(ZoneId.of("Z"))
            .toEpochSecond(ZoneOffset.of("Z")) * TICKS_SECOND

        val ticksDiff = ticksNow - ticks
        val secondsDiff = ticksDiff / TICKS_SECOND

        return if (secondsDiff < 60) {
            context.resources.getString(R.string.now)
        } else if (secondsDiff < SECONDS_HOUR) {
            val minutes = secondsDiff / 60
            val minutesString = context.resources.getString(R.string.minutes_ago)

            "$minutes $minutesString"
        } else if (secondsDiff < SECONDS_HOUR * 24) {
            val hours = secondsDiff / SECONDS_HOUR

            if (hours.equals(1)) {
                "$hours ${context.resources.getString(R.string.hour_ago)}"
            } else {
                "$hours ${context.resources.getString(R.string.hours_ago)}"
            }
        } else if (secondsDiff < SECONDS_HOUR * 24 * 7) {
            val days = secondsDiff / (SECONDS_HOUR * 24)

            if (days.equals(1)) {
                "$days ${context.resources.getString(R.string.day_ago)}"
            } else {
                "$days ${context.resources.getString(R.string.days_ago)}"
            }
        } else {
            val instant = Instant.ofEpochSecond(ticks).atOffset(ZoneOffset.of("Z"))
            val formatter = DateTimeFormatter.ofPattern("dd MM yyyy")

            instant.toLocalDateTime().format(formatter)
        }
    }

    companion object {
        private const val TICKS_SECOND = 10000000
        private const val SECONDS_HOUR = 3600
    }
}