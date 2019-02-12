package fr.corenting.convertisseureurofranc.convert

import android.content.Context

import fr.corenting.convertisseureurofranc.R

class USA(context: Context) : ConvertAbstract() {

    init {
        this.setContext(context)
        loadValuesFromCSV(R.raw.us_values)
    }

    override fun convertFunction(yearOfOrigin: Int, yearOfResult: Int, amount: Float): Double {
        if (yearOfOrigin == yearOfResult) return amount.toDouble()
        val multiplier = getValues().get(yearOfResult) / getValues().get(yearOfOrigin)
        return amount * multiplier
    }

    override fun getCurrencyFromYear(year: Int): String {
        return getContext().getString(R.string.dollars)
    }
}
