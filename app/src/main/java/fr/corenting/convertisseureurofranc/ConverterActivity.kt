package fr.corenting.convertisseureurofranc

import android.content.SharedPreferences
import android.os.Bundle
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.ConfigurationCompat
import androidx.preference.PreferenceManager
import com.google.android.material.textfield.TextInputLayout
import fr.corenting.convertisseureurofranc.converters.ConverterAbstract
import fr.corenting.convertisseureurofranc.converters.FranceConverter
import fr.corenting.convertisseureurofranc.converters.USAConverter
import fr.corenting.convertisseureurofranc.utils.Utils
import kotlinx.android.synthetic.main.activity_converter.*
import java.util.*

class ConverterActivity : AppCompatActivity() {

    lateinit var converter: ConverterAbstract
    private lateinit var prefs: SharedPreferences

    private fun handleDarkTheme() {
        // Dark theme
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        when {
            prefs.getBoolean(
                getString(R.string.preferenceDarkThemeKey),
                false
            ) -> AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_YES
            )
            else -> AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_NO
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        handleDarkTheme()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_converter)
        topAppBar.setOnMenuItemClickListener(this::onMenuItemClickListener)

        // Default converter
        converter = USAConverter(applicationContext)

        //Initialize the years spinners and the buttons
        initYearInputs()
        initButtons()

        //Set currency spinner content
        val currenciesList = listOf(
            getString(R.string.usa_currencies),
            getString(R.string.france_currencies)
        )
        setAutoCompleteAdapter(currencyAutoComplete, currenciesList)
        currencyAutoComplete.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                onCurrencyItemClickListener(position)
            }

        // Set default currency
        val currentLocale: Locale = ConfigurationCompat.getLocales(resources.configuration).get(0)
        if (currentLocale == Locale.FRANCE) {
            currencyAutoComplete.setText(getString(R.string.france_currencies), false)
        } else {
            currencyAutoComplete.setText(getString(R.string.usa_currencies), false)
        }


        if (prefs.getBoolean(getString(R.string.preferenceDarkThemeKey), false)) {
            topAppBar.menu.getItem(0).isChecked = true
        }

    }

    private fun onCurrencyItemClickListener(position: Int) {
        converter = when (position) {
            0 -> USAConverter(applicationContext)
            else -> FranceConverter(applicationContext)
        }
        initYearInputs()
    }

    private fun onMenuItemClickListener(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.action_about -> {
                Utils.showCredits(this)
                return true
            }
            R.id.action_dark_theme -> {
                val editor = prefs.edit()
                editor.putBoolean(getString(R.string.preferenceDarkThemeKey), !menuItem.isChecked)
                menuItem.isChecked = !menuItem.isChecked
                editor.apply()
                when {
                    menuItem.isChecked -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    }
                    else -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    }
                }
                return true
            }
            else -> return false
        }
    }

    private fun initYearInputs() {
        // Get list of years
        val yearsList = (converter.latestYear downTo converter.firstYear).toList().map {
            it.toString()
        }

        val textFields = listOf(
            Triple(yearOfOriginAutoComplete, sumToConvertInput, R.string.sumToConvert),
            Triple(yearOfResultAutoComplete, resultInput, R.string.resultText)
        )
        for ((yearInput, sumInput, hintStringId) in textFields) {
            setAutoCompleteAdapter(yearInput, yearsList)
            setYearAutoCompleteListener(yearInput, sumInput, hintStringId)

            yearInput.setText(yearsList.first(), false)

            setCurrencyInputHint(sumInput, converter.latestYear, hintStringId)
        }
    }

    private fun initButtons() {
        //Convert when the button is clicked
        convertButton.setImeActionLabel(getString(R.string.convertButton), KeyEvent.KEYCODE_ENTER)

        //Click button when using enter on the keyboard
        sumToConvertText.setOnKeyListener(View.OnKeyListener { _, _, event ->
            if (event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER) {
                convertButton.performClick()
                return@OnKeyListener false
            }
            false
        })

        //Setup listeners
        convertButton.setOnClickListener { v ->
            try {
                sumToConvertInput.error = null
                Utils.hideSoftKeyboard(v)
                val yearOfOrigin = Integer.parseInt(yearOfOriginAutoComplete.text.toString())
                val yearOfResult = Integer.parseInt(yearOfResultAutoComplete.text.toString())
                val amount = java.lang.Float.parseFloat(sumToConvertText.text.toString())
                resultText.setText(
                    Utils.formatNumber(
                        this,
                        converter.convertFunction(yearOfOrigin, yearOfResult, amount)
                    )
                )
            } catch (e: Exception) {
                if (sumToConvertText.text == null || sumToConvertText.text.toString() == "") {
                    sumToConvertInput.error = getString(R.string.no_amount_entered)
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
        val adapter = ArrayAdapter(this, R.layout.list_item, items)
        autoCompleteTextView.setAdapter(adapter)
    }

    private fun setYearAutoCompleteListener(
        autoCompleteTextView: AutoCompleteTextView,
        textView: TextInputLayout,
        stringReference: Int
    ) {
        autoCompleteTextView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                if (parent != null) {
                    val year = Integer.parseInt(parent.getItemAtPosition(position).toString())
                    setCurrencyInputHint(textView, year, stringReference)
                }
            }
    }

    private fun setCurrencyInputHint(
        textView: TextInputLayout,
        year: Int,
        stringReference: Int,
    ) {
        textView.hint = applicationContext.getString(
            stringReference,
            converter.getCurrencyFromYear(year)
        )
    }
}
