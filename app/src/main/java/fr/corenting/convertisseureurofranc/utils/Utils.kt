package fr.corenting.convertisseureurofranc.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.os.ConfigurationCompat
import fr.corenting.convertisseureurofranc.converters.ConverterAbstract
import fr.corenting.convertisseureurofranc.converters.FranceConverter
import fr.corenting.convertisseureurofranc.converters.SouthKoreaConverter
import fr.corenting.convertisseureurofranc.converters.UKConverter
import fr.corenting.convertisseureurofranc.converters.USAConverter
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale


object Utils {

    fun getConverterForCurrentLocale(context: Context): ConverterAbstract {
        return when (ConfigurationCompat.getLocales(context.resources.configuration).get(0)) {
            Locale.FRANCE -> {
                FranceConverter(context)
            }

            Locale.UK -> {
                UKConverter(context)
            }

            Locale.US -> {
                USAConverter(context)
            }

            Locale.KOREA -> {
                SouthKoreaConverter(context)
            }

            else -> {
                USAConverter(context)
            }
        }
    }

    fun hideSoftKeyboard(v: View): Boolean {
        val imm = v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        return imm.hideSoftInputFromWindow(v.applicationWindowToken, 0)
    }

    fun formatNumber(c: Context, number: Float): String {
        val currentLocale = ConfigurationCompat.getLocales(c.resources.configuration).get(0)
        val formatter = NumberFormat.getInstance(currentLocale) as DecimalFormat
        return formatter.format(number)
    }


}
