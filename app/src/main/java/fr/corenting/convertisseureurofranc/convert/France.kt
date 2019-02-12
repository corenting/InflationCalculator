package fr.corenting.convertisseureurofranc.convert

import android.content.Context

import fr.corenting.convertisseureurofranc.R

class France(context: Context) : ConvertAbstract() {

    init {
        this.setContext(context)
        loadValuesFromCSV(R.raw.fr_values)
    }

    override fun convertFunction(yearOfOrigin: Int, yearOfResult: Int, amount: Float): Double {
        var amount = amount
        if (yearOfOrigin == yearOfResult) return amount.toDouble()
        val multiplier = getValues().get(yearOfResult) / getValues().get(yearOfOrigin)
        //Convert values if currency is different
        if (yearOfResult < 1960) {
            amount *= 100f
        }
        if (yearOfOrigin < 1960) {
            amount /= 100f
        }
        if (yearOfResult < 2002) {
            amount *= 6.55957f
        }
        if (yearOfOrigin < 2002) {
            amount *= 0.15244f
        }

        return amount * multiplier
    }

    override fun getCurrencyFromYear(year: Int): String {
        if (year >= 2002) return getContext().getString(R.string.euros)
        return if (year >= 1960) getContext().getString(R.string.francs) else getContext().getString(R.string.oldFrancs)
    }
}
