package fr.corenting.convertisseureurofranc;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Hashtable;

public class Utils {

    static String getCurrencyFromYear(int year)
    {
        return (year >= 2002)?"€":"F";
    }

    static void setSpinnerListener(final Activity activity, Spinner spinner, final TextView textView)
    {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                int year = Integer.parseInt(parent.getItemAtPosition(pos).toString());
                textView.setText(Utils.getCurrencyFromYear(year));
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    static float getMultiplier(int year)
    {
        float multiplier;
        switch(year)
        {
            case 2013:
                multiplier = 1f;
                break;
            case 2012:
                multiplier =  1.009f;
                break;
            case 2011:
                multiplier = 1.028f;
                break;
            case 2010:
                multiplier = 1.050f;
                break;
            case 2009:
                multiplier = 1.066f;
                break;
            case 2008:
                multiplier = 1.067f;
                break;
            case 2007:
                multiplier = 1.097f;
                break;
            case 2006:
                multiplier = 1.114f;
                break;
            case 2005:
                multiplier = 1.132f;
                break;
            case 2004:
                multiplier = 1.152f;
                break;
            case 2003:
                multiplier = 1.177f;
                break;
            case 2002:
                multiplier = 1.201f;
                break;
            case 2001:
                multiplier = 0.18665f;
                break;
            case 2000:
                multiplier = 0.18976f;
                break;
            default:
                multiplier = 1f;
        }
        return  multiplier;
    }

    static float convertFunction(int yearOfOrigin, int yearOfResult,float amount)
    {
        float amountInEuroToday;
        float multiplier = getMultiplier(yearOfOrigin);
        amountInEuroToday = amount * multiplier;
        float sub =  (amountInEuroToday * (1/getMultiplier(yearOfResult)));
        return amountInEuroToday - (amountInEuroToday - sub);
    }
}
