package fr.corenting.convertisseureurofranc.converters

import android.content.Context

import fr.corenting.convertisseureurofranc.R

class FranceConverter(context: Context) : ConverterAbstract(context, R.raw.fr_values, context.getString(R.string.france_currencies)) {

    override fun convertFunction(yearOfOrigin: Int, yearOfResult: Int, amount: Float): Float {
        var newAmount = amount
        if (yearOfOrigin == yearOfResult) return amount

        val multiplier = getValueForYear(yearOfResult) / getValueForYear(yearOfOrigin)
        //Convert values if currency is different
        if (yearOfResult < 1960) {
            newAmount *= 100f
        }
        if (yearOfOrigin < 1960) {
            newAmount /= 100f
        }
        if (yearOfResult < 2002) {
            newAmount *= 6.55957f
        }
        if (yearOfOrigin < 2002) {
            newAmount *= 0.15244f
        }

        return newAmount * multiplier
    }

    override fun getCurrencyFromYear(year: Int): String {
        return when {
            year >= 2002 -> context.getString(R.string.euros)
            year >= 1960 -> context.getString(R.string.francs)
            else -> context.getString(R.string.oldFrancs)
        }
    }
}
