package fr.corenting.convertisseureurofranc.converters

import android.content.Context

import fr.corenting.convertisseureurofranc.R

class USAConverter(context: Context) :
    ConverterAbstract(context, R.raw.us_values, context.getString(R.string.usa_currencies)) {

    override fun convertFunction(yearOfOrigin: Int, yearOfResult: Int, amount: Float): Float {
        if (yearOfOrigin == yearOfResult) return amount

        val multiplier = getValueForYear(yearOfResult) / getValueForYear(yearOfOrigin)
        return amount * multiplier
    }

    override fun getCurrencyFromYear(year: Int): String {
        return context.getString(R.string.dollars)
    }
}
