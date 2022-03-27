package fr.corenting.convertisseureurofranc

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment


class AboutDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(R.string.appName)
            .setIcon(R.mipmap.ic_launcher)
            .setMessage(
                fromHtmlWrapped(
                    requireContext().getString(R.string.aboutText) +
                            BuildConfig.VERSION_NAME
                )
            )
            .setNegativeButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()

        val messageTextView = dialog.findViewById<View>(android.R.id.message) as TextView
        messageTextView.movementMethod = LinkMovementMethod.getInstance()

        return dialog
    }

    private fun fromHtmlWrapped(content: String): Spanned {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(content, Html.FROM_HTML_MODE_COMPACT)
        } else {
            @Suppress("DEPRECATION")
            Html.fromHtml(content)
        }
    }

    companion object {
        const val TAG = "AboutDialog"
    }

}