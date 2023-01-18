package io.github.svejnohatomas.swansea.newsaggregator.ui.search

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import io.github.svejnohatomas.swansea.newsaggregator.R
import io.github.svejnohatomas.swansea.newsaggregator.databinding.ActivitySearchBinding
import io.github.svejnohatomas.swansea.newsaggregator.helper.ApiHelper
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Calendar

/**
 * An activity to enter search terms.
 *
 * @author Tomas Svejnoha (987451@swansea.ac.uk)
 * @version 1.0.0
 * */
class SearchActivity : AppCompatActivity() {
    /** A binding to the activity's layout. */
    private lateinit var binding: ActivitySearchBinding
    /** A ViewModel of the activity. */
    private lateinit var viewModel: SearchViewModel
    /** A map of languages. */
    private lateinit var languages: Map<String, String>
    /** A DateTime formatter. */
    private val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[SearchViewModel::class.java]

        initialiseSearchView()
        initialiseCheckBoxes()
        initialisePickers()
        initialiseLanguageSelection()
        initialiseSearchButton()
    }

    override fun onResume() {
        super.onResume()

        // Search
        viewModel.query.value?.let { binding.searchInput.setQuery(it, false) }
        // CheckBoxes
        binding.searchInTitle.isChecked = viewModel.searchInTitle.value ?: true
        binding.searchInDescription.isChecked = viewModel.searchInDescription.value ?: true
        binding.searchInContent.isChecked = viewModel.searchInContent.value ?: true
        // Dates
        viewModel.fromDate.value?.let { binding.searchFromDate.text = it.format(formatter) }
        viewModel.toDate.value?.let { binding.searchToDate.text = it.format(formatter) }
        // Times
        viewModel.fromTime.value?.let { binding.searchFromTime.text = localTimeToString(it) }
        viewModel.toTime.value?.let { binding.searchToTime.text = localTimeToString(it) }
        // Languages
        viewModel.language.value?.let {
            val adapter = ArrayAdapter(this, R.layout.list_item, languages.values.stream().toArray())

            val autoCompleteTextView = binding.searchLanguage.editText as? AutoCompleteTextView
            autoCompleteTextView?.setAdapter(adapter)
            autoCompleteTextView?.setText(languages.getValue(viewModel.language.value ?: "all"), false)
        }
    }

    /**
     * Initialises SearchView event listeners.
     * */
    private fun initialiseSearchView() {
        binding.searchInput.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    search(query)
                }

                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                // TODO: Auto complete for search query

                viewModel.setQuery(newText)
                return true
            }
        })
    }
    /** Initialises MaterialCheckBox event listeners. */
    private fun initialiseCheckBoxes() {
        binding.searchInTitle.setOnCheckedChangeListener { _, isChecked -> viewModel.setSearchInTitle(isChecked) }
        binding.searchInDescription.setOnCheckedChangeListener { _, isChecked -> viewModel.setSearchInDescription(isChecked) }
        binding.searchInContent.setOnCheckedChangeListener { _, isChecked -> viewModel.setSearchInContent(isChecked) }
    }
    /**
     * Initialises data and time event listeners.
     * */
    private fun initialisePickers() {
        binding.searchFromDate.setOnClickListener {
            val calendar = Calendar.getInstance()

            val dialog = DatePickerDialog(this, { _, year, month, dayOfMonth ->
                val localDate = LocalDate.of(year, month + 1, dayOfMonth)
                viewModel.setFromDate(localDate)
                binding.searchFromDate.text = localDate.format(formatter)
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

            dialog.datePicker.maxDate = calendar.timeInMillis

            dialog.show()
        }
        binding.searchFromTime.setOnClickListener {
            val time = LocalDateTime.now()

            TimePickerDialog(this, { _, hourOfDay, minute ->
                viewModel.setFromTime(LocalTime.of(hourOfDay, minute))
                binding.searchFromTime.text = localTimeToString(viewModel.fromTime.value!!)
            }, time.hour, time.minute, true).show()
        }

        binding.searchToDate.setOnClickListener {
            val calendar = Calendar.getInstance()

            val dialog = DatePickerDialog(this, { _, year, month, dayOfMonth ->
                val localDate = LocalDate.of(year, month + 1, dayOfMonth)
                viewModel.setToDate(localDate)
                binding.searchToDate.text = localDate.format(formatter)
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

            dialog.datePicker.maxDate = calendar.timeInMillis

            dialog.show()
        }
        binding.searchToTime.setOnClickListener {
            val time = LocalDateTime.now()

            TimePickerDialog(this, { _, hourOfDay, minute ->
                viewModel.setToTime(LocalTime.of(hourOfDay, minute))
                binding.searchToTime.text = localTimeToString(viewModel.toTime.value!!)
            }, time.hour, time.minute, true).show()
        }
    }
    /** Initialises language selection. */
    private fun initialiseLanguageSelection() {
        languages = mapOf(
            "all" to getString(R.string.all_languages),
            "ar" to getString(R.string.arabic),
            "de" to getString(R.string.german),
            "en" to getString(R.string.english),
            "es" to getString(R.string.spanish),
            "fr" to getString(R.string.french),
            "he" to getString(R.string.hebrew),
            "it" to getString(R.string.italian),
            "nl" to getString(R.string.dutch),
            "no" to getString(R.string.norwegian),
            "pt" to getString(R.string.portuguese),
            "ru" to getString(R.string.russian),
            "sv" to getString(R.string.swedish),
            "zh" to getString(R.string.chinese),
        )

        val adapter = ArrayAdapter(this, R.layout.list_item, languages.values.stream().toArray())
        
        val autoCompleteTextView = binding.searchLanguage.editText as? AutoCompleteTextView
        autoCompleteTextView?.setAdapter(adapter)
        autoCompleteTextView?.setText(getString(R.string.all_languages), false)
        autoCompleteTextView?.doAfterTextChanged { text ->
            val key = languages.entries.first { it.value == text.toString() }.key
            viewModel.setLanguage(key)
        }
    }
    /** Initialises search button. */
    private fun initialiseSearchButton() {
        binding.buttonSearch.setOnClickListener {
            if (!viewModel.query.value.isNullOrBlank()) {
                search(viewModel.query.value!!)
            }
        }
    }

    /**
     * Converts LocalTime to String.
     *
     * @param time The LocalTime to convert.
     *
     * @return A string representation of LocalTime.
     * */
    private fun localTimeToString(time: LocalTime): String {
        return "${time.hour.toString().padStart(2, '0')}:${time.minute.toString().padStart(2, '0')}"
    }

    /**
     * Searches for the articles.
     *
     * @param query The search query.
     * */
    private fun search(query: String) {
        if (!viewModel.validate()) {
            Toast.makeText(this, getString(R.string.invalid_input), Toast.LENGTH_LONG).show()
        } else {
            ApiHelper(this).search(
                query,
                viewModel.from(),
                viewModel.to(),
                viewModel.searchInTitle.value ?: true,
                viewModel.searchInDescription.value ?: true,
                viewModel.searchInContent.value ?: true,
                viewModel.language.value ?: "all")
        }
    }
}