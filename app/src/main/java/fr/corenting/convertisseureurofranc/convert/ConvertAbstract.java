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

public abstract class ConvertAbstract {

    public int latestYear;
    Context context;
    HashMap<Integer, Float> values = new LinkedHashMap<>();

    public abstract double convertFunction(int yearOfOrigin, int yearOfResult, float amount);
    public abstract String getCurrencyFromYear(int year);

    void loadValuesFromCSV(int fileID) {
        //Load the values from the csv file
        InputStream inputStream = context.getResources().openRawResource(fileID);
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
            Log.d("CSV parsing error", e.getMessage());
            e.printStackTrace();
        }
    }
}
