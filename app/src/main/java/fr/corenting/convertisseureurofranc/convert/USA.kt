package fr.corenting.convertisseureurofranc.convert

import android.content.Context

import fr.corenting.convertisseureurofranc.R

class USA(context: Context) : ConvertAbstract(context, R.raw.us_values) {

    override fun convertFunction(yearOfOrigin: Int, yearOfResult: Int, amount: Float): Float {
        if (yearOfOrigin == yearOfResult) return amount
        val multiplier = values[yearOfResult]!! / values[yearOfOrigin]!!
        return amount * multiplier
    }

    override fun getCurrencyFromYear(year: Int): String {
        return context.getString(R.string.dollars)
    }
}
