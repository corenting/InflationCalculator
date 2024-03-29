package fr.corenting.convertisseureurofranc

import android.os.Bundle
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.doOnTextChanged
import fr.corenting.convertisseureurofranc.converters.ConverterAbstract
import fr.corenting.convertisseureurofranc.converters.FranceConverter
import fr.corenting.convertisseureurofranc.converters.SouthKoreaConverter
import fr.corenting.convertisseureurofranc.converters.UKConverter
import fr.corenting.convertisseureurofranc.converters.USAConverter
import fr.corenting.convertisseureurofranc.databinding.ActivityConverterBinding
import fr.corenting.convertisseureurofranc.utils.ThemeUtils
import fr.corenting.convertisseureurofranc.utils.Utils

class ConverterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityConverterBinding

    private val converters = mapOf(
        USAConverter::class.java to 0,
        UKConverter::class.java to 1,
        FranceConverter::class.java to 2,
        SouthKoreaConverter::class.java to 3,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        // Theme and creation
        AppCompatDelegate.setDefaultNightMode(ThemeUtils.getThemeToUse(this))
        super.onCreate(savedInstanceState)

        // Set toolbar
        val toolbar = findViewById<Toolbar>(R.id.topAppBar)
        setSupportActionBar(toolbar)

        // Binding and view
        binding = ActivityConverterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.topAppBar.setOnMenuItemClickListener(this::onMenuItemClickListener)
        binding.topAppBar.menu.findItem(ThemeUtils.getMenuIdForCurrentTheme(this)).isChecked = true

        // Common values
        val currenciesList = listOf(
            getString(R.string.usa_currencies),
            getString(R.string.uk_currencies),
            getString(R.string.france_currencies),
            getString(R.string.south_korea_currencies)
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
        converterViewModel.getConverter().observe(this) {
            if (it != null) {
                onConverterChange(it)

                // Update selected in list (used at app opening)
                binding.currencyTextView.setText(
                    currenciesList[converters[it::class.java] ?: 0]
                )
            }
        }

        //Set currency spinner content
        val adapter = DropdownAdapter(this, R.layout.list_item, currenciesList)
        binding.currencyTextView.setAdapter(adapter)
        binding.currencyTextView.setOnItemClickListener { _, _, position, _ ->

            val converterClass =
                converters.entries.associate { (key, value) -> value to key }[position]
                    ?: USAConverter::class.java
            val converter =
                converterClass.constructors[0].newInstance(applicationContext) as ConverterAbstract
            converterViewModel.setConverter(converter)
        }
        initButtons(converterViewModel)
    }

    private fun onConverterChange(newConverter: ConverterAbstract) {
        val latestYear = newConverter.latestYear
        val firstYear = newConverter.firstYear

        binding.yearOfOriginEditText.setText(latestYear.toString())
        binding.yearOfOriginInput.hint = getString(R.string.yearOfOrigin, firstYear, latestYear)
        binding.yearOfOriginEditText.doOnTextChanged { text, _, _, _ ->
            YearInputTextHandler.doOnTextChanged(
                applicationContext,
                newConverter,
                binding.yearOfOriginEditText,
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

        binding.yearOfResultEditText.setText(latestYear.toString())
        binding.yearOfResultInput.hint = getString(R.string.yearOfResult, firstYear, latestYear)
        binding.yearOfResultEditText.doOnTextChanged { text, _, _, _ ->
            YearInputTextHandler.doOnTextChanged(
                applicationContext,
                newConverter,
                binding.yearOfResultEditText,
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
                AboutDialogFragment().show(
                    supportFragmentManager, AboutDialogFragment.TAG
                )
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
            binding.yearOfOriginEditText.clearFocus()
            binding.yearOfResultEditText.clearFocus()
            Utils.hideSoftKeyboard(v)
            doConversion(converterViewModel)
        }
    }

    private fun doConversion(converterViewModel: ConverterViewModel) {
        try {
            binding.sumToConvertInput.error = null
            val yearOfOrigin =
                Integer.parseInt(binding.yearOfOriginEditText.text.toString())
            val yearOfResult =
                Integer.parseInt(binding.yearOfResultEditText.text.toString())
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
