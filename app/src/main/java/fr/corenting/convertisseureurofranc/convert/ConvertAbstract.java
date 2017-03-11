package fr.corenting.convertisseureurofranc.convert;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedHashMap;

public abstract class ConvertAbstract {

    public int latestYear;
    public int firstYear;
    Context context;
    HashMap<Integer, Float> values = new LinkedHashMap<>();

    public abstract double convertFunction(int yearOfOrigin, int yearOfResult, float amount);

    public abstract String getCurrencyFromYear(int year);

    void loadValuesFromCSV(int fileID) {
        //Load the values from the csv file
        InputStream inputStream = context.getResources().openRawResource(fileID);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        latestYear = Integer.MIN_VALUE;
        firstYear = Integer.MAX_VALUE;
        try {
            while ((line = reader.readLine()) != null) {
                String[] separatedLine = line.split(";");
                int year = Integer.parseInt(separatedLine[0]);
                if (year > latestYear)
                    latestYear = year;
                if (year < firstYear)
                    firstYear = year;
                float value = Float.parseFloat(separatedLine[1]);
                values.put(year, value);
            }
            reader.close();
        } catch (IOException e) {
            Log.d("CSV parsing error", e.getMessage());
            e.printStackTrace();
        }
    }
}
