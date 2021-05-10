package fr.corenting.convertisseureurofranc.utils

import android.app.Activity
import android.content.Context
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.os.ConfigurationCompat
import fr.corenting.convertisseureurofranc.BuildConfig
import fr.corenting.convertisseureurofranc.R
import fr.corenting.convertisseureurofranc.converters.ConverterAbstract
import fr.corenting.convertisseureurofranc.converters.FranceConverter
import fr.corenting.convertisseureurofranc.converters.UKConverter
import fr.corenting.convertisseureurofranc.converters.USAConverter
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*


object Utils {

    fun getConverterForCurrentLocale(context: Context): ConverterAbstract {
        return when (ConfigurationCompat.getLocales(context.resources.configuration).get(0)) {
            Locale.FRANCE -> {
                FranceConverter(context)
            }
            Locale.UK -> {
                UKConverter(context)
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

    fun showCredits(activity: Activity): AlertDialog {
        val dialog = AlertDialog.Builder(activity)
            .setTitle(R.string.appName)
            .setIcon(R.mipmap.ic_launcher)
            .setMessage(
                fromHtmlWrapped(
                    activity.getString(R.string.aboutText) +
                            BuildConfig.VERSION_NAME
                )
            )
            .setNegativeButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()

        val messageTextView = dialog.findViewById<View>(android.R.id.message) as TextView
        messageTextView.movementMethod = LinkMovementMethod.getInstance()

        return dialog
    }

    fun formatNumber(c: Context, number: Float): String {
        val currentLocale = ConfigurationCompat.getLocales(c.resources.configuration).get(0)
        val formatter = NumberFormat.getInstance(currentLocale) as DecimalFormat
        return formatter.format(number)
    }

    private fun fromHtmlWrapped(content: String): Spanned {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(content, Html.FROM_HTML_MODE_COMPACT)
        } else {
            @Suppress("DEPRECATION")
            Html.fromHtml(content)
        }
    }

}
