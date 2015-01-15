package fr.corenting.convertisseureurofranc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

public class Utils {

    static String getCurrencyFromYear(int year) {
        if(year >= 2002) return "€";
        if(year >= 1960) return "F";
        return "AF";
    }

    static void hideSoftKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    static void showCredits(Activity activity) {
        ((TextView) new AlertDialog.Builder(activity)
                .setTitle(R.string.appName)
                .setIcon(R.drawable.ic_launcher)
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
}
