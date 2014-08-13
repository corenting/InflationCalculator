package fr.corenting.convertisseureurofranc;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;


public class Converter extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_converter);

        final Spinner originSpinner = (Spinner) findViewById(R.id.yearOfOriginSpinner);
        final Spinner resultSpinner = (Spinner) findViewById(R.id.yearOfResultSpinner);

        //Populate the spinners with a list of years
        List<Integer> yearsList = new LinkedList<Integer>();
        for (int i = 2013; i >= 1901; i--) {
            yearsList.add(i);
        }
        ArrayAdapter<Integer> spinnerArray = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, yearsList);
        spinnerArray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        originSpinner.setAdapter(spinnerArray);
        resultSpinner.setAdapter(spinnerArray);

        //Add an onItemSelected listener to change the currency text according to the year
        Utils.setSpinnerListener(this, originSpinner, (TextView) findViewById(R.id.currencyOriginTextView));
        Utils.setSpinnerListener(this, resultSpinner,(TextView) findViewById(R.id.currencyResultTextView));

        //Convert when the button is clicked
        Button convertButton = (Button) findViewById(R.id.convertButton);
        final EditText resultEditText = (EditText) findViewById(R.id.resultEditText);
        resultEditText.setKeyListener(null);
        final EditText amountEditText = (EditText) findViewById(R.id.amountEditText);
        convertButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int yearOfOrigin = Integer.parseInt(originSpinner.getSelectedItem().toString());
                int yearOfResult = Integer.parseInt(resultSpinner.getSelectedItem().toString());
                float amount = Float.parseFloat(amountEditText.getText().toString());
                resultEditText.setText(String.valueOf(Utils.convertFunction(yearOfOrigin,yearOfResult,amount)));
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.converter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (item.getItemId() == R.id.action_about) {
            ((TextView) new AlertDialog.Builder(this)
                    .setTitle(R.string.appName)
                    .setIcon(R.drawable.ic_launcher)
                    .setMessage(Html.fromHtml(getString(R.string.about_text) + BuildConfig.VERSION_NAME))
                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show()
                    .findViewById(android.R.id.message))
                    .setMovementMethod(LinkMovementMethod.getInstance());
        }
        return super.onOptionsItemSelected(item);
    }
}
