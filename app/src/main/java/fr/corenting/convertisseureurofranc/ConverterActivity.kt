package fr.corenting.convertisseureurofranc

import android.os.Bundle
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.ConfigurationCompat
import fr.corenting.convertisseureurofranc.converters.ConverterAbstract
import fr.corenting.convertisseureurofranc.converters.FranceConverter
import fr.corenting.convertisseureurofranc.converters.USAConverter
import fr.corenting.convertisseureurofranc.databinding.ActivityConverterBinding
import fr.corenting.convertisseureurofranc.utils.ThemeUtils
import fr.corenting.convertisseureurofranc.utils.Utils
import java.util.*

class ConverterActivity : AppCompatActivity() {

    companion object {
        private const val converterBundleKey = "converter"
    }

    private lateinit var converter: ConverterAbstract
    private lateinit var binding: ActivityConverterBinding


    // Save position for config change
    private var currentConverter: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        // Theme and creation
        AppCompatDelegate.setDefaultNightMode(ThemeUtils.getThemeToUse(this))
        super.onCreate(savedInstanceState)

        // Binding and view
        binding = ActivityConverterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.topAppBar.setOnMenuItemClickListener(this::onMenuItemClickListener)

        //Set currency spinner content
        val currenciesList = listOf(
            getString(R.string.usa_currencies),
            getString(R.string.france_currencies)
        )
        setAutoCompleteAdapter(binding.currencyAutoComplete, currenciesList)
        binding.currencyAutoComplete.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                onCurrencyItemClickListener(position)
            }

        // Set default currency
        if (savedInstanceState == null) {
            val currentLocale: Locale =
                ConfigurationCompat.getLocales(resources.configuration).get(0)
            if (currentLocale == Locale.FRANCE) {
                binding.currencyAutoComplete.setText(getString(R.string.france_currencies), false)
                onCurrencyItemClickListener(1)
            } else {
                binding.currencyAutoComplete.setText(getString(R.string.usa_currencies), false)
                onCurrencyItemClickListener(0)
            }
        } else {
            onCurrencyItemClickListener(savedInstanceState.getInt(converterBundleKey))
        }

        binding.topAppBar.menu.findItem(ThemeUtils.getMenuIdForCurrentTheme(this)).isChecked = true
        initButtons()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt(converterBundleKey, currentConverter)
    }

    private fun onCurrencyItemClickListener(position: Int) {
        converter = when (position) {
            0 -> USAConverter(applicationContext)
            else -> FranceConverter(applicationContext)
        }
        currentConverter = position
        initInputFields()
    }

    private fun onMenuItemClickListener(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.action_about -> {
                Utils.showCredits(this)
                true
            }
            R.id.action_theme_system, R.id.action_theme_light, R.id.action_theme_dark -> {
                ThemeUtils.saveThemePreference(this, menuItem.itemId)
                AppCompatDelegate.setDefaultNightMode(ThemeUtils.getThemeToUse(this))
                menuItem.isChecked = true
                true
            }
            else -> false
        }
    }

    private fun initInputFields() {
        // Set years inputs
        val yearFields = listOf(
            Triple(
                binding.yearOfOriginAutoComplete,
                binding.yearOfOriginInput,
                R.string.yearOfOrigin
            ),
            Triple(
                binding.yearOfResultAutoComplete,
                binding.yearOfResultInput,
                R.string.yearOfResult
            )
        )

        val yearsList = (converter.latestYear downTo converter.firstYear).toList().map {
            it.toString()
        }
        for ((yearInputField, yearInputLayout, hintStringId) in yearFields) {
            yearInputField.setText(yearsList.first().toString())
            yearInputLayout.hint =
                getString(hintStringId, converter.firstYear, converter.latestYear)
            yearInputField.onFocusChangeListener = View.OnFocusChangeListener { v, _ ->
                val textContent = (v as EditText).text.toString()
                val errorString = getString(
                    R.string.invalid_year_error
                )
                if (textContent.isBlank()) {
                    yearInputLayout.error = errorString
                }

                try {
                    val year = textContent.toInt()
                    when {
                        year < converter.firstYear || year > converter.latestYear -> {
                            yearInputLayout.error = errorString
                        }
                        else -> {
                            yearInputLayout.error = null
                        }
                    }
                } catch (err: NumberFormatException) {
                    yearInputLayout.error = errorString
                }

                // TODO: refresh corresponding currency input to handle currency change
            }
        }

        refreshCurrencyInputs(converter.latestYear, converter.latestYear - 2)
    }

    private fun refreshCurrencyInputs(fromYear: Int, toYear: Int) {
        val currencyFields = listOf(
            Triple(binding.sumToConvertInput, R.string.sumToConvert, fromYear),
            Triple(binding.resultInput, R.string.resultText, toYear)
        )
        for ((sumInput, hintStringId, year) in currencyFields) {
            sumInput.hint = getString(
                hintStringId,
                converter.getCurrencyFromYear(year)
            )
        }
    }

    private fun initButtons() {
        //Convert when the button is clicked
        binding.convertButton.setImeActionLabel(
            getString(R.string.convertButton),
            KeyEvent.KEYCODE_ENTER
        )

        //Click button when using enter on the keyboard
        binding.sumToConvertText.setOnKeyListener(View.OnKeyListener { _, _, event ->
            if (event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER) {
                binding.convertButton.performClick()
                return@OnKeyListener false
            }
            false
        })

        //Setup listeners
        binding.convertButton.setOnClickListener { v ->
            try {
                binding.sumToConvertInput.error = null
                Utils.hideSoftKeyboard(v)
                val yearOfOrigin =
                    Integer.parseInt(binding.yearOfOriginAutoComplete.text.toString())
                val yearOfResult =
                    Integer.parseInt(binding.yearOfResultAutoComplete.text.toString())
                val amount = java.lang.Float.parseFloat(binding.sumToConvertText.text.toString())
                binding.resultText.setText(
                    Utils.formatNumber(
                        this,
                        converter.convertFunction(yearOfOrigin, yearOfResult, amount)
                    )
                )
            } catch (e: Exception) {
                if (binding.sumToConvertText.text == null || binding.sumToConvertText.text.toString() == "") {
                    binding.sumToConvertInput.error = getString(R.string.no_amount_entered)
                }
                val errorToast = Toast.makeText(
                    this, getString(R.string.errorToast),
                    Toast.LENGTH_SHORT
                )
                errorToast.show()
            }
        }
    }

    private fun setAutoCompleteAdapter(
        autoCompleteTextView: AutoCompleteTextView,
        items: List<String>
    ) {
        val adapter = AutocompleteAdapter(this, R.layout.list_item, items)
        autoCompleteTextView.setAdapter(adapter)
    }
}
