package fr.corenting.convertisseureurofranc;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import fr.corenting.convertisseureurofranc.convert.ConvertAbstract;
import fr.corenting.convertisseureurofranc.convert.France;
import fr.corenting.convertisseureurofranc.convert.USA;
import fr.corenting.convertisseureurofranc.utils.Utils;

public class ConverterActivity extends AppCompatActivity {

    public ConvertAbstract converter;
    SharedPreferences prefs;

    private Spinner originSpinner;
    private Spinner resultSpinner;
    private TextView currencyOriginTextView;
    private TextView currencyResultTextView;
    private Button convertButton;
    private EditText resultEditText;
    private EditText amountEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean(getString(R.string.preferenceDarkThemeKey), false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_converter);

        //Initialize the converter
        converter = new France(this);

        // Binding
        originSpinner = findViewById(R.id.yearOfOriginSpinner);
        resultSpinner = findViewById(R.id.yearOfResultSpinner);
        Spinner currencySpinner = findViewById(R.id.currencySpinner);
        currencyOriginTextView = findViewById(R.id.currencyOriginTextView);
        currencyResultTextView = findViewById(R.id.currencyResultTextView);

        //Initialize the years spinners and the buttons
        initSpinners();
        initButtons();

        //Set currency spinner content
        List<String> currenciesList = Arrays.asList("France (euros, francs, anciens francs)", "USA (dollars)");
        setSpinnerAdapter(currencySpinner, currenciesList);
        currencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (pos == 0) {
                    converter = new France(parent.getContext());
                    initSpinners();
                } else {
                    converter = new USA(parent.getContext());
                    initSpinners();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void initSpinners() {

        //Populate the spinners with a list of years
        List<String> yearsList = new LinkedList<>();
        for (int i = converter.latestYear; i >= converter.firstYear; i--) {
            yearsList.add(String.valueOf(i));
        }
        setSpinnerAdapter(originSpinner, yearsList);
        setSpinnerAdapter(resultSpinner, yearsList);

        //Add an onItemSelected listener to change the currency text according to the year
        setSpinnerListener(originSpinner, currencyOriginTextView);
        setSpinnerListener(resultSpinner, currencyResultTextView);
    }

    private void initButtons() {
        //Convert when the button is clicked
        convertButton = findViewById(R.id.convertButton);
        convertButton.setImeActionLabel(getString(R.string.convertButton), KeyEvent.KEYCODE_ENTER);
        resultEditText = findViewById(R.id.resultEditText);
        resultEditText.setKeyListener(null); //Make the EditText widget read only

        //Click button when using enter on the keyboard
        amountEditText = findViewById(R.id.amountEditText);
        amountEditText.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    convertButton.performClick();
                    return false;
                }
                return false;
            }
        });

        //Setup listeners
        final Context c = this;
        convertButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    Utils.hideSoftKeyboard(v);
                    int yearOfOrigin = Integer.parseInt(originSpinner.getSelectedItem().toString());
                    int yearOfResult = Integer.parseInt(resultSpinner.getSelectedItem().toString());
                    float amount = Float.parseFloat(amountEditText.getText().toString());
                    resultEditText.setText(Utils.formatNumber(c, converter.convertFunction(yearOfOrigin, yearOfResult, amount)));
                } catch (Exception e) {
                    Toast errorToast = Toast.makeText(c, getString(R.string.errorToast), Toast.LENGTH_SHORT);
                    errorToast.show();
                }
            }
        });
    }

    private void setSpinnerAdapter(Spinner s, List<String> items) {
        s.setAdapter(null);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);
    }

    private void setSpinnerListener(Spinner spinner, final TextView textView) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                int year = Integer.parseInt(parent.getItemAtPosition(pos).toString());
                textView.setText(converter.getCurrencyFromYear(year));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.converter, menu);
        if (prefs.getBoolean(getString(R.string.preferenceDarkThemeKey), false)) {
            menu.getItem(0).setChecked(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_about:
                Utils.showCredits(this);
                break;
            case R.id.action_dark_theme:
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(getString(R.string.preferenceDarkThemeKey), !item.isChecked());
                item.setChecked(!item.isChecked());
                editor.apply();
                finish();
                startActivity(getIntent());
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
