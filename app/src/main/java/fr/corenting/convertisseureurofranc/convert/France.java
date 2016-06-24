package fr.corenting.convertisseureurofranc.convert;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedHashMap;

import fr.corenting.convertisseureurofranc.R;

public class France extends ConvertAbstract {

    private HashMap<Integer, Float> values = new LinkedHashMap<>();

    public France(Context context) {
        this.context = context;
        //Load the values from the csv file
        InputStream inputStream = context.getResources().openRawResource(R.raw.converter_values);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        boolean first = true;
        try {
            while ((line = reader.readLine()) != null) {
                String[] separatedLine = line.split(";");
                int year = Integer.parseInt(separatedLine[0]);
                float value = Float.parseFloat(separatedLine[1]);
                values.put(year, value);

                //For the first entry, get the year to use for the spinners
                if (first) {
                    first = false;
                    latestYear = Integer.parseInt(separatedLine[0]);
                }
            }
            reader.close();
        } catch (IOException e) {
            Log.d("France", e.getMessage());
            e.printStackTrace();
        }
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
