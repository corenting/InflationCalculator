package fr.corenting.convertisseureurofranc.convert

import android.content.Context
import android.util.Log

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.HashMap
import java.util.LinkedHashMap

abstract class ConvertAbstract {

    var latestYear: Int = 0
    var firstYear: Int = 0
    internal var context: Context? = null
    internal var values: HashMap<Int, Float> = LinkedHashMap()

    abstract fun convertFunction(yearOfOrigin: Int, yearOfResult: Int, amount: Float): Double

    abstract fun getCurrencyFromYear(year: Int): String

    internal fun loadValuesFromCSV(fileID: Int) {
        //Load the values from the csv file
        val inputStream = context!!.resources.openRawResource(fileID)
        val reader = BufferedReader(InputStreamReader(inputStream))
        var line: String
        latestYear = Integer.MIN_VALUE
        firstYear = Integer.MAX_VALUE
        try {
            while ((line = reader.readLine()) != null) {
                val separatedLine = line.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val year = Integer.parseInt(separatedLine[0])
                if (year > latestYear)
                    latestYear = year
                if (year < firstYear)
                    firstYear = year
                val value = java.lang.Float.parseFloat(separatedLine[1])
                values[year] = value
            }
            reader.close()
        } catch (e: IOException) {
            Log.d("CSV parsing error", e.message)
            e.printStackTrace()
        }

    }
}
