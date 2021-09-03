package fr.corenting.convertisseureurofranc.converters

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.math.max
import kotlin.math.min

abstract class ConverterAbstract(protected val context: Context, private val fileId: Int) {

    var latestYear: Int = 0
    var firstYear: Int = 0
    private var values: HashMap<Int, Float> = LinkedHashMap()

    init {
        loadValuesFromCSV()
    }

    abstract fun convertFunction(yearOfOrigin: Int, yearOfResult: Int, amount: Float): Float

    abstract fun getCurrencyFromYear(year: Int): String

    protected fun getValueForYear(year: Int): Float {
        val yearValue = values[year]
        requireNotNull(yearValue)
        return yearValue
    }

    private fun loadValuesFromCSV() {
        //Load the values from the csv file
        val inputStream = context.resources.openRawResource(fileId)
        val iterator = BufferedReader(InputStreamReader(inputStream)).lineSequence().iterator()

        latestYear = Integer.MIN_VALUE
        firstYear = Integer.MAX_VALUE

        while (iterator.hasNext()) {
            val splittedLine = iterator.next()
                .split(";")
                .toTypedArray()

            val year = Integer.parseInt(splittedLine[0])

            latestYear = max(year, latestYear)
            firstYear = min(year, firstYear)

            values[year] = splittedLine[1].toFloat()
        }
    }
}
