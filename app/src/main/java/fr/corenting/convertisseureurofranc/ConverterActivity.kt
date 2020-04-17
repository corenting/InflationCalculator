package fr.corenting.convertisseureurofranc

import android.content.SharedPreferences
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import fr.corenting.convertisseureurofranc.convert.ConvertAbstract
import fr.corenting.convertisseureurofranc.convert.France
import fr.corenting.convertisseureurofranc.convert.USA
import fr.corenting.convertisseureurofranc.utils.Utils
import kotlinx.android.synthetic.main.activity_converter.*

class ConverterActivity : AppCompatActivity() {

    lateinit var converter: ConvertAbstract
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
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
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_converter)

        // Default converter
        converter = France(applicationContext)

        //Initialize the years spinners and the buttons
        initSpinners()
        initButtons()

        //Set currency spinner content
        val currenciesList = listOf(
            getString(R.string.france_currencies),
            getString(R.string.usa_currencies)
        )
        setSpinnerAdapter(currencySpinner, currenciesList)
        currencySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                converter = when (pos) {
                    0 -> France(applicationContext)
                    else -> USA(applicationContext)
                }
                initSpinners()
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {}
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.converter, menu)
        if (prefs.getBoolean(getString(R.string.preferenceDarkThemeKey), false)) {
            menu.getItem(0).isChecked = true
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_about -> Utils.showCredits(this)
            R.id.action_dark_theme -> {
                val editor = prefs.edit()
                editor.putBoolean(getString(R.string.preferenceDarkThemeKey), !item.isChecked)
                item.isChecked = !item.isChecked
                editor.apply()
                when {
                    item.isChecked -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    }
                    else -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initSpinners() {
        //Populate the spinners with a list of years
        val yearsList = (converter.latestYear downTo converter.firstYear).toList().map {
            it.toString()
        }
        setSpinnerAdapter(yearOfOriginSpinner, yearsList)
        setSpinnerAdapter(yearOfResultSpinner, yearsList)

        //Add an onItemSelected listener to change the currency text according to the year
        setSpinnerListener(yearOfOriginSpinner, currencyOriginTextView)
        setSpinnerListener(yearOfResultSpinner, currencyResultTextView)
    }

    private fun initButtons() {
        //Convert when the button is clicked
        convertButton.setImeActionLabel(getString(R.string.convertButton), KeyEvent.KEYCODE_ENTER)
        resultEditText.keyListener = null //Make the EditText widget read only

        //Click button when using enter on the keyboard
        amountEditText.setOnKeyListener(View.OnKeyListener { _, _, event ->
            if (event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER) {
                convertButton.performClick()
                return@OnKeyListener false
            }
            false
        })

        //Setup listeners
        convertButton.setOnClickListener { v ->
            try {
                Utils.hideSoftKeyboard(v)
                val yearOfOrigin = Integer.parseInt(yearOfOriginSpinner.selectedItem.toString())
                val yearOfResult = Integer.parseInt(yearOfResultSpinner.selectedItem.toString())
                val amount = java.lang.Float.parseFloat(amountEditText.text.toString())
                resultEditText.setText(
                    Utils.formatNumber(
                        this,
                        converter.convertFunction(yearOfOrigin, yearOfResult, amount)
                    )
                )
            } catch (e: Exception) {
                val errorToast = Toast.makeText(
                    this, getString(R.string.errorToast),
                    Toast.LENGTH_SHORT
                )
                errorToast.show()
            }
        }
    }

    private fun setSpinnerAdapter(s: Spinner, items: List<String>) {
        s.adapter = null
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item, items
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        s.adapter = adapter
    }

    private fun setSpinnerListener(spinner: Spinner, textView: TextView) {
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                if (parent != null) {
                    val year = Integer.parseInt(parent.getItemAtPosition(pos).toString())
                    textView.text = converter.getCurrencyFromYear(year)
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {}
        }
    }
}
