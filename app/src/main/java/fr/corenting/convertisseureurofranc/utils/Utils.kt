package fr.corenting.convertisseureurofranc.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView

import java.text.DecimalFormat
import java.text.NumberFormat

import fr.corenting.convertisseureurofranc.BuildConfig
import fr.corenting.convertisseureurofranc.R

object Utils {

    fun hideSoftKeyboard(v: View) {
        val imm = v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken, 0)
    }

    fun showCredits(activity: Activity) {
        (AlertDialog.Builder(activity)
                .setTitle(R.string.appName)
                .setIcon(R.mipmap.ic_launcher)
                .setMessage(Html.fromHtml(activity.getString(R.string.aboutText) + BuildConfig.VERSION_NAME))
                .setNegativeButton("OK") { dialog, which -> dialog.dismiss() }
                .show()
                .findViewById<View>(android.R.id.message) as TextView).movementMethod = LinkMovementMethod.getInstance()
    }

    fun formatNumber(c: Context, number: Double): String {
        val formatter = NumberFormat.getInstance(c.resources.configuration.locale) as DecimalFormat
        return formatter.format(number)
    }
}
