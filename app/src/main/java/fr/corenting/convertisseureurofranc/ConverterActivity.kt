package fr.corenting.convertisseureurofranc

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast

import java.util.Arrays
import java.util.LinkedList

import fr.corenting.convertisseureurofranc.convert.ConvertAbstract
import fr.corenting.convertisseureurofranc.convert.France
import fr.corenting.convertisseureurofranc.convert.USA
import fr.corenting.convertisseureurofranc.utils.Utils

class ConverterActivity : AppCompatActivity() {

    var converter: ConvertAbstract
    internal var prefs: SharedPreferences

    private var originSpinner: Spinner? = null
    private var resultSpinner: Spinner? = null
    private var currencyOriginTextView: TextView? = null
    private var currencyResultTextView: TextView? = null
    private var convertButton: Button? = null
    private var resultEditText: EditText? = null
    private var amountEditText: EditText? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        if (prefs.getBoolean(getString(R.string.preferenceDarkThemeKey), false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_converter)

        //Initialize the converter
        converter = France(this)

        // Binding
        originSpinner = findViewById(R.id.yearOfOriginSpinner)
        resultSpinner = findViewById(R.id.yearOfResultSpinner)
        val currencySpinner = findViewById<Spinner>(R.id.currencySpinner)
        currencyOriginTextView = findViewById(R.id.currencyOriginTextView)
        currencyResultTextView = findViewById(R.id.currencyResultTextView)

        //Initialize the years spinners and the buttons
        initSpinners()
        initButtons()

        //Set currency spinner content
        val currenciesList = Arrays.asList("France (euros, francs, anciens francs)", "USA (dollars)")
        setSpinnerAdapter(currencySpinner, currenciesList)
        currencySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
                if (pos == 0) {
                    converter = France(parent.context)
                    initSpinners()
                } else {
                    converter = USA(parent.context)
                    initSpinners()
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {}
        }
    }

    private fun initSpinners() {

        //Populate the spinners with a list of years
        val yearsList = LinkedList<String>()
        for (i in converter.latestYear downTo converter.firstYear) {
            yearsList.add(i.toString())
        }
        setSpinnerAdapter(originSpinner!!, yearsList)
        setSpinnerAdapter(resultSpinner!!, yearsList)

        //Add an onItemSelected listener to change the currency text according to the year
        setSpinnerListener(originSpinner!!, currencyOriginTextView)
        setSpinnerListener(resultSpinner!!, currencyResultTextView)
    }

    private fun initButtons() {
        //Convert when the button is clicked
        convertButton = findViewById(R.id.convertButton)
        convertButton!!.setImeActionLabel(getString(R.string.convertButton), KeyEvent.KEYCODE_ENTER)
        resultEditText = findViewById(R.id.resultEditText)
        resultEditText!!.keyListener = null //Make the EditText widget read only

        //Click button when using enter on the keyboard
        amountEditText = findViewById(R.id.amountEditText)
        amountEditText!!.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER) {
                convertButton!!.performClick()
                return@OnKeyListener false
            }
            false
        })

        //Setup listeners
        val c = this
        convertButton!!.setOnClickListener { v ->
            try {
                Utils.hideSoftKeyboard(v)
                val yearOfOrigin = Integer.parseInt(originSpinner!!.selectedItem.toString())
                val yearOfResult = Integer.parseInt(resultSpinner!!.selectedItem.toString())
                val amount = java.lang.Float.parseFloat(amountEditText!!.text.toString())
                resultEditText!!.setText(Utils.formatNumber(c, converter.convertFunction(yearOfOrigin, yearOfResult, amount)))
            } catch (e: Exception) {
                val errorToast = Toast.makeText(c, getString(R.string.errorToast), Toast.LENGTH_SHORT)
                errorToast.show()
            }
        }
    }

    private fun setSpinnerAdapter(s: Spinner, items: List<String>) {
        s.adapter = null
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        s.adapter = adapter
    }

    private fun setSpinnerListener(spinner: Spinner, textView: TextView?) {
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
                val year = Integer.parseInt(parent.getItemAtPosition(pos).toString())
                textView!!.text = converter.getCurrencyFromYear(year)
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
        val id = item.itemId
        when (id) {
            R.id.action_about -> Utils.showCredits(this)
            R.id.action_dark_theme -> {
                val editor = prefs.edit()
                editor.putBoolean(getString(R.string.preferenceDarkThemeKey), !item.isChecked)
                item.isChecked = !item.isChecked
                editor.apply()
                finish()
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
