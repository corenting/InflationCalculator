package fr.corenting.convertisseureurofranc

import ConverterViewModel
import ConverterViewModelFactory
import android.os.Bundle
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.widget.doOnTextChanged
import fr.corenting.convertisseureurofranc.converters.ConverterAbstract
import fr.corenting.convertisseureurofranc.converters.FranceConverter
import fr.corenting.convertisseureurofranc.converters.USAConverter
import fr.corenting.convertisseureurofranc.databinding.ActivityConverterBinding
import fr.corenting.convertisseureurofranc.utils.ThemeUtils
import fr.corenting.convertisseureurofranc.utils.Utils
import java.util.*

class ConverterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityConverterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        // Theme and creation
        AppCompatDelegate.setDefaultNightMode(ThemeUtils.getThemeToUse(this))
        super.onCreate(savedInstanceState)

        // Binding and view
        binding = ActivityConverterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.topAppBar.setOnMenuItemClickListener(this::onMenuItemClickListener)
        binding.topAppBar.menu.findItem(ThemeUtils.getMenuIdForCurrentTheme(this)).isChecked = true

        // Common values
        val currenciesList = listOf(
            getString(R.string.usa_currencies),
            getString(R.string.france_currencies)
        )

        // Sum input
        binding.sumToConvertText.doOnTextChanged { text, _, _, _ ->
            try {
                if (text.toString().toFloat() > 0) {
                    binding.sumToConvertInput.error = null
                }
            } catch (err: NumberFormatException) {
            }
        }

        // Viewmodel and converter setup
        val converterViewModel: ConverterViewModel by viewModels {
            ConverterViewModelFactory(Utils.getConverterForCurrentLocale(applicationContext))
        }
        converterViewModel.getConverter().observe(this, {
            if (it != null) {
                onConverterChange(it)

                // Update selected in list (used at app opening)
                var selectedCurrencyPosition = 0
                if (it is FranceConverter) {
                    selectedCurrencyPosition = 1
                }
                binding.currencyAutoComplete.setText(currenciesList[selectedCurrencyPosition])
            }
        })

        //Set currency spinner content
        val adapter = AutoCompleteAdapter(this, R.layout.list_item, currenciesList)
        binding.currencyAutoComplete.setAdapter(adapter)
        binding.currencyAutoComplete.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> converterViewModel.setConverter(USAConverter(applicationContext))
                else -> converterViewModel.setConverter(FranceConverter(applicationContext))
            }
        }
        initButtons(converterViewModel)
    }

    private fun onConverterChange(newConverter: ConverterAbstract) {
        val latestYear = newConverter.latestYear
        val firstYear = newConverter.firstYear

        binding.yearOfOriginAutoComplete.setText(latestYear.toString())
        binding.yearOfOriginInput.hint = getString(R.string.yearOfOrigin, firstYear, latestYear)
        binding.yearOfOriginAutoComplete.doOnTextChanged { text, _, _, _ ->
            YearInputTextHandler.doOnTextChanged(
                applicationContext,
                newConverter,
                binding.yearOfOriginAutoComplete,
                binding.yearOfOriginInput
            )
            try {
                val year = text.toString().toInt()
                binding.sumToConvertInput.hint = getString(
                    R.string.sumToConvert, newConverter.getCurrencyFromYear(year)
                )
            } catch (exc: NumberFormatException) {
            }
        }
        binding.sumToConvertInput.hint = getString(
            R.string.sumToConvert,
            newConverter.getCurrencyFromYear(latestYear)
        )

        binding.yearOfResultAutoComplete.setText(latestYear.toString())
        binding.yearOfResultInput.hint = getString(R.string.yearOfOrigin, firstYear, latestYear)
        binding.yearOfResultAutoComplete.doOnTextChanged { text, _, _, _ ->
            YearInputTextHandler.doOnTextChanged(
                applicationContext,
                newConverter,
                binding.yearOfResultAutoComplete,
                binding.yearOfResultInput
            )
            try {
                val year = text.toString().toInt()
                binding.resultInput.hint = getString(
                    R.string.resultText,
                    newConverter.getCurrencyFromYear(year)
                )
            } catch (exc: NumberFormatException) {
            }
        }
        binding.resultInput.hint = getString(
            R.string.resultText,
            newConverter.getCurrencyFromYear(latestYear)
        )
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

    private fun initButtons(converterViewModel: ConverterViewModel) {
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
            binding.convertButton.requestFocus()
            binding.sumToConvertInput.clearFocus()
            binding.yearOfOriginAutoComplete.clearFocus()
            binding.yearOfResultAutoComplete.clearFocus()
            Utils.hideSoftKeyboard(v)
            doConversion(converterViewModel)
        }
    }

    private fun doConversion(converterViewModel: ConverterViewModel) {
        try {
            binding.sumToConvertInput.error = null
            val yearOfOrigin =
                Integer.parseInt(binding.yearOfOriginAutoComplete.text.toString())
            val yearOfResult =
                Integer.parseInt(binding.yearOfResultAutoComplete.text.toString())
            val amount = java.lang.Float.parseFloat(binding.sumToConvertText.text.toString())
            val convertedAmount =
                converterViewModel.doConversion(yearOfOrigin, yearOfResult, amount)
            binding.resultText.setText(Utils.formatNumber(this, convertedAmount))
        } catch (e: Exception) {
            if (binding.sumToConvertText.text == null || binding.sumToConvertText.text.toString() == "") {
                binding.sumToConvertInput.error = getString(R.string.no_amount_entered)
            }
            Toast.makeText(
                this, getString(R.string.errorToast),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
