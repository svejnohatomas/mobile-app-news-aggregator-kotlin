package io.github.svejnohatomas.swansea.newsaggregator.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * A ViewModel for SearchActivity.
 *
 * @author Tomas Svejnoha (987451@swansea.ac.uk)
 * @version 1.0.0
 */
class SearchViewModel : ViewModel() {

    /** A search query. */
    private val _query = MutableLiveData<String>()
    /**
     * Sets the search query.
     *
     * @param query The new search query.
     * */
    fun setQuery(query: String) {
        _query.apply { value = query }
    }
    /** The search query. */
    val query: LiveData<String> = _query

    /** A flag indicating whether to search for the query in article titles or not. */
    private val _searchInTitle = MutableLiveData<Boolean>().apply { value = true }
    /**
     * Sets a new flag indicating whether to search for the query in article titles or not.
     *
     * @param newValue The new value.
     * */
    fun setSearchInTitle(newValue: Boolean) {
        _searchInTitle.apply { value = newValue }
    }
    /** A flag indicating whether to search for the query in article titles or not. */
    val searchInTitle: LiveData<Boolean> = _searchInTitle

    /** A flag indicating whether to search for the query in article descriptions or not. */
    private val _searchInDescription = MutableLiveData<Boolean>().apply { value = true }
    /**
     * Sets a new flag indicating whether to search for the query in article descriptions or not.
     *
     * @param newValue The new value.
     * */
    fun setSearchInDescription(newValue: Boolean) {
        _searchInDescription.apply { value = newValue }
    }
    /** A flag indicating whether to search for the query in article descriptions or not. */
    val searchInDescription: LiveData<Boolean> = _searchInDescription

    /** A flag indicating whether to search for the query in article contents or not. */
    private val _searchInContent = MutableLiveData<Boolean>().apply { value = true }
    /**
     * Sets a new flag indicating whether to search for the query in article contents or not.
     *
     * @param newValue The new value.
     * */
    fun setSearchInContent(newValue: Boolean) {
        _searchInContent.apply { value = newValue }
    }
    /** A flag indicating whether to search for the query in article contents or not. */
    val searchInContent: LiveData<Boolean> = _searchInContent

    /** A date from which to search for articles. */
    private val _fromDate = MutableLiveData<LocalDate>()
    /**
     * Sets a new date from which to search for articles.
     *
     * @param fromDate The new date.
     * */
    fun setFromDate(fromDate: LocalDate) {
        _fromDate.apply { value = fromDate }
    }
    /** A date from which to search for articles. */
    val fromDate: LiveData<LocalDate> = _fromDate

    /** A time from which to search for articles. */
    private val _fromTime = MutableLiveData<LocalTime>()
    /**
     * Sets a new time from which to search for articles.
     *
     * @param fromTime The new time.
     * */
    fun setFromTime(fromTime: LocalTime) {
        _fromTime.apply { value = fromTime }
    }
    /** A time from which to search for articles. */
    val fromTime: LiveData<LocalTime> = _fromTime

    /**
     * Gets a date and time from which to search for articles.
     *
     * @return A date time from which to search for articles.
     * */
    fun from(): LocalDateTime? {
        return if (_fromDate.value == null) {
            null
        } else if (_fromTime.value == null) {
            LocalDateTime.of(_fromDate.value, LocalTime.of(0, 0))
        } else {
            LocalDateTime.of(_fromDate.value, _fromTime.value)
        }
    }

    /** A date to which search for articles. */
    private val _toDate = MutableLiveData<LocalDate>()
    /**
     * Sets a new date to which search for articles.
     *
     * @param toDate The new date.
     * */
    fun setToDate(toDate: LocalDate) {
        _toDate.apply { value = toDate }
    }
    /** A date to which search for articles. */
    val toDate: LiveData<LocalDate> = _toDate

    /** A time to which search for articles. */
    private val _toTime = MutableLiveData<LocalTime>()
    /**
     * Sets a new time to which search for articles.
     *
     * @param toTime The new time.
     * */
    fun setToTime(toTime: LocalTime) {
        _toTime.apply { value = toTime }
    }
    /** A time to which search for articles. */
    val toTime: LiveData<LocalTime> = _toTime

    /**
     * Gets a date and time to which to search for articles.
     *
     * @return A date time to which to search for articles.
     * */
    fun to(): LocalDateTime? {
        return if (_toDate.value == null) {
            null
        } else if (_toTime.value == null) {
            LocalDateTime.of(_toDate.value, LocalTime.of(0, 0))
        } else {
            LocalDateTime.of(_toDate.value, _toTime.value)
        }
    }

    /**
     * Validates the from and to date and time ranges.
     * */
    fun validate(): Boolean {
        return if (from() == null && to() == null) {
            true
        } else if (from() != null && to() != null) {
            from()!! < to()!!
        } else {
            false
        }
    }

    /** A language in which to search for articles. */
    private val _language = MutableLiveData<String>().apply {
        value = "all"
    }
    /**
     * Sets a new language in which to search for articles.
     *
     * @param language The new lanaguage.
     * */
    fun setLanguage(language: String) {
        _language.apply { value = language }
    }
    /** A language in which to search for articles. */
    val language: LiveData<String> = _language
}