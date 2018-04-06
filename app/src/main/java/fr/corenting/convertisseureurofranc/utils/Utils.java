package fr.corenting.convertisseureurofranc.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import fr.corenting.convertisseureurofranc.BuildConfig;
import fr.corenting.convertisseureurofranc.R;

public class Utils {

    public static void hideSoftKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public static void showCredits(Activity activity) {
        ((TextView) new AlertDialog.Builder(activity)
                .setTitle(R.string.appName)
                .setIcon(R.mipmap.ic_launcher)
                .setMessage(Html.fromHtml(activity.getString(R.string.aboutText) + BuildConfig.VERSION_NAME))
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show()
                .findViewById(android.R.id.message))
                .setMovementMethod(LinkMovementMethod.getInstance());
    }

    public static String formatNumber(Context c, double number) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(c.getResources().getConfiguration().locale);
        return formatter.format(number);
    }
}
