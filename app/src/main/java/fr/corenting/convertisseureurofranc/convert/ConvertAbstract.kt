package fr.corenting.convertisseureurofranc.convert

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*

abstract class ConvertAbstract(protected val context: Context, private val fileId: Int) {

    var latestYear: Int = 0
    var firstYear: Int = 0
    protected var values: HashMap<Int, Float> = LinkedHashMap()

    init {
        loadValuesFromCSV()
    }

    abstract fun convertFunction(yearOfOrigin: Int, yearOfResult: Int, amount: Float): Float

    abstract fun getCurrencyFromYear(year: Int): String

    private fun loadValuesFromCSV() {
        //Load the values from the csv file
        val inputStream = context.resources.openRawResource(fileId)
        val iterator = BufferedReader(InputStreamReader(inputStream)).lineSequence().iterator()

        latestYear = Integer.MIN_VALUE
        firstYear = Integer.MAX_VALUE

        while (iterator.hasNext()) {
            val line = iterator.next()

            val separatedLine = line
                    .split(";".toRegex())
                    .dropLastWhile { it.isEmpty() }
                    .toTypedArray()

            val year = Integer.parseInt(separatedLine[0])
            if (year > latestYear)
                latestYear = year
            if (year < firstYear)
                firstYear = year
            val value = java.lang.Float.parseFloat(separatedLine[1])
            values[year] = value
        }
    }
}
