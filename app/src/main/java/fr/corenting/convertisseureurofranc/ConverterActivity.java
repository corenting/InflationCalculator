package fr.corenting.convertisseureurofranc;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
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

import java.util.LinkedList;
import java.util.List;

import fr.corenting.convertisseureurofranc.convert.ConvertAbstract;
import fr.corenting.convertisseureurofranc.convert.France;
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
            setTheme(R.style.AppThemeDark);
        } else {
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_converter);

        //Initialize the converter
        converter = new France(this);

        //Initialize the spinners and buttons
        initSpinners();
        initButtons();
    }

    private void initSpinners() {
        originSpinner = (Spinner) findViewById(R.id.yearOfOriginSpinner);
        resultSpinner = (Spinner) findViewById(R.id.yearOfResultSpinner);
        currencyOriginTextView = (TextView) findViewById(R.id.currencyOriginTextView);
        currencyResultTextView = (TextView) findViewById(R.id.currencyResultTextView);

        //Populate the spinners with a list of years
        List<Integer> yearsList = new LinkedList<>();
        for (int i = converter.latestYear; i >= 1901; i--) {
            yearsList.add(i);
        }
        setSpinnerAdapter(originSpinner, yearsList);
        setSpinnerAdapter(resultSpinner, yearsList);

        //Add an onItemSelected listener to change the currency text according to the year
        setSpinnerListener(originSpinner, currencyOriginTextView);
        setSpinnerListener(resultSpinner, currencyResultTextView);
    }

    private void initButtons() {
        //Convert when the button is clicked
        convertButton = (Button) findViewById(R.id.convertButton);
        convertButton.setImeActionLabel(getString(R.string.convertButton), KeyEvent.KEYCODE_ENTER);
        resultEditText = (EditText) findViewById(R.id.resultEditText);
        resultEditText.setKeyListener(null); //Make the EditText widget read only

        //Click button when using enter on the keyboard
        amountEditText = (EditText) findViewById(R.id.amountEditText);
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

    private void setSpinnerAdapter(Spinner s, List<Integer> items) {
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
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
