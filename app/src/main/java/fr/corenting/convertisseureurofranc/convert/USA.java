package fr.corenting.convertisseureurofranc.convert;

import android.content.Context;

import fr.corenting.convertisseureurofranc.R;

public class USA extends ConvertAbstract {

    public USA(Context context) {
        this.context = context;
        loadValuesFromCSV(R.raw.us_values);
    }

    public double convertFunction(int yearOfOrigin, int yearOfResult, float amount) {
        if (yearOfOrigin == yearOfResult) return amount;
        double multiplier = values.get(yearOfResult) / values.get(yearOfOrigin);
        return amount * multiplier;
    }

    public String getCurrencyFromYear(int year) {
        return context.getString(R.string.dollars);
    }
}
