package fr.corenting.convertisseureurofranc.convert;

import android.content.Context;

public abstract class ConvertAbstract {

    public int latestYear;
    Context context;

    public abstract double convertFunction(int yearOfOrigin, int yearOfResult, float amount);
    public abstract String getCurrencyFromYear(int year);
}
