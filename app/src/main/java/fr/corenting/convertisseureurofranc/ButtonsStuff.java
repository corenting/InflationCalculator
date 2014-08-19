package fr.corenting.convertisseureurofranc;

import android.app.Activity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ButtonsStuff {

    static void ButtonsInit(Activity activity) {
        //Convert when the button is clicked
        final Button convertButton = (Button) activity.findViewById(R.id.convertButton);
        convertButton.setImeActionLabel(activity.getString(R.string.convertButton), KeyEvent.KEYCODE_ENTER);
        final EditText resultEditText = (EditText) activity.findViewById(R.id.resultEditText);
        resultEditText.setKeyListener(null); //Make the EditText widget read only
    }

    public static void amountEditTextOnKeyInit(final Activity activity) {
        final EditText amountEditText = (EditText) activity.findViewById(R.id.amountEditText);
        amountEditText.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    activity.findViewById(R.id.convertButton).performClick();
                    return false;
                }

                return false;
            }
        });
    }

    public static void convertButtonOnClick(final Activity activity, final ConvertCalc convertCalc) {
        final Spinner originSpinner = (Spinner) activity.findViewById(R.id.yearOfOriginSpinner);
        final Spinner resultSpinner = (Spinner) activity.findViewById(R.id.yearOfResultSpinner);
        final TextView amountEditText = (TextView) activity.findViewById(R.id.amountEditText);
        final TextView resultEditText = (TextView) activity.findViewById(R.id.resultEditText);
        Button convertButton = (Button) activity.findViewById(R.id.convertButton);

        convertButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    Utils.hideSoftKeyboard(v);
                    int yearOfOrigin = Integer.parseInt(originSpinner.getSelectedItem().toString());
                    int yearOfResult = Integer.parseInt(resultSpinner.getSelectedItem().toString());
                    float amount = Float.parseFloat(amountEditText.getText().toString());
                    resultEditText.setText(String.valueOf(convertCalc.convertFunction(yearOfOrigin, yearOfResult, amount)));
                } catch (Exception e) {
                    Toast errorToast = Toast.makeText(activity.getApplicationContext(), activity.getString(R.string.errorToast), Toast.LENGTH_SHORT);
                    errorToast.show();
                }
            }
        });
    }
}
