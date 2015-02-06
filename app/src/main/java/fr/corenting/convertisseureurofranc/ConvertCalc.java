package fr.corenting.convertisseureurofranc;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class ConvertCalc {

    HashMap<Integer, Float> values = new LinkedHashMap<>();

    ConvertCalc(Context context) {
        //Load the values from the csv file
        InputStream inputStream = context.getResources().openRawResource(R.raw.converter_values);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                String[] separatedLine = line.split(";");
                values.put(Integer.parseInt(separatedLine[0]), Float.parseFloat(separatedLine[1]));
            }
            reader.close();
        } catch (IOException e) {
            Log.d("ConvertCalc", e.getMessage());
            e.printStackTrace();
        }

    }

    public double convertFunction(int yearOfOrigin, int yearOfResult, float amount) {
        if (yearOfOrigin == yearOfResult) return amount;
        double multiplier = values.get(yearOfResult) / values.get(yearOfOrigin);
        //Convert values to €
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
}
