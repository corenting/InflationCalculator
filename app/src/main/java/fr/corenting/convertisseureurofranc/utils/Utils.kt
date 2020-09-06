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
import fr.corenting.convertisseureurofranc.BuildConfig
import fr.corenting.convertisseureurofranc.R
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*


object Utils {

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
        val formatter = NumberFormat.getInstance(getCurrentLocale(c)) as DecimalFormat
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

    private fun getCurrentLocale(ctx: Context): Locale {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ctx.resources.configuration.locales.get(0)
        } else {
            @Suppress("DEPRECATION")
            ctx.resources.configuration.locale
        }
    }
}
