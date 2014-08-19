package fr.corenting.convertisseureurofranc;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class ConvertCalc {

    HashMap<Integer, Float> values = new LinkedHashMap<Integer, Float>(115);

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

    public float convertFunction(int yearOfOrigin, int yearOfResult, float amount) {
        float amountInEuroToday = amount * values.get(yearOfOrigin);
        return amountInEuroToday / values.get(yearOfResult);
    }
}
