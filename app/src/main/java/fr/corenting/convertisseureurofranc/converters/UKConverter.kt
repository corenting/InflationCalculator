package fr.corenting.convertisseureurofranc.converters

import android.content.Context

import fr.corenting.convertisseureurofranc.R

class UKConverter(context: Context) : ConverterAbstract(context, R.raw.uk_values) {

    override fun convertFunction(yearOfOrigin: Int, yearOfResult: Int, amount: Float): Float {
        if (yearOfOrigin == yearOfResult) return amount

        val multiplier = getValueForYear(yearOfResult) / getValueForYear(yearOfOrigin)
        return amount * multiplier
    }

    override fun getCurrencyFromYear(year: Int): String {
        return context.getString(R.string.pounds_sterling)
    }
}
