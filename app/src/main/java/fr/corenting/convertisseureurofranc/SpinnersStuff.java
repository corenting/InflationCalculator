package fr.corenting.convertisseureurofranc;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

public class SpinnersStuff {

    static void Init(Activity activity, int latestYear) {
        Spinner originSpinner = (Spinner) activity.findViewById(R.id.yearOfOriginSpinner);
        Spinner resultSpinner = (Spinner) activity.findViewById(R.id.yearOfResultSpinner);
        TextView currencyOriginTextView = (TextView) activity.findViewById(R.id.currencyOriginTextView);
        TextView currencyResultTextView = (TextView) activity.findViewById(R.id.currencyResultTextView);

        //Initialize the spinners
        fillSpinnerWithYears(activity, originSpinner, latestYear);
        fillSpinnerWithYears(activity, resultSpinner, latestYear);

        //Add an onItemSelected listener to change the currency text according to the year
        setListeners(originSpinner, currencyOriginTextView);
        setListeners(resultSpinner, currencyResultTextView);
    }

    private static void setListeners(Spinner spinner, final TextView textView) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                int year = Integer.parseInt(parent.getItemAtPosition(pos).toString());
                textView.setText(Utils.getCurrencyFromYear(parent.getContext(), year));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private static void fillSpinnerWithYears(Activity activity, Spinner spinner, int maxYear) {
        //Populate the spinners with a list of years
        List<Integer> yearsList = new LinkedList<>();
        for (int i = maxYear; i >= 1901; i--) {
            yearsList.add(i);
        }
        ArrayAdapter<Integer> spinnerArray = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, yearsList);
        spinnerArray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArray);
    }
}
