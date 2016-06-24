package fr.corenting.convertisseureurofranc.convert;

import android.content.Context;

import fr.corenting.convertisseureurofranc.R;

public class France extends ConvertAbstract {

    public France(Context context) {
        this.context = context;
        loadValuesFromCSV(R.raw.fr_values);
    }

    public double convertFunction(int yearOfOrigin, int yearOfResult, float amount) {
        if (yearOfOrigin == yearOfResult) return amount;
        double multiplier = values.get(yearOfResult) / values.get(yearOfOrigin);
        //Convert values if currency is different
        if (yearOfResult < 1960) {
            amount *= 100;
        }
        if (yearOfOrigin < 1960) {
            amount /= 100;
        }
        if (yearOfResult < 2002) {
            amount *= 6.55957;
        }
        if (yearOfOrigin < 2002) {
            amount *= 0.15244;
        }

        return amount * multiplier;
    }

    public String getCurrencyFromYear(int year) {
        if (year >= 2002) return context.getString(R.string.euros);
        if (year >= 1960) return context.getString(R.string.francs);
        return context.getString(R.string.oldFrancs);
    }
}
