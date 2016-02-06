package fr.corenting.convertisseureurofranc;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class ConverterActivity extends AppCompatActivity {

    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean(getString(R.string.preferenceDarkThemeKey), false)) {
            setTheme(R.style.AppThemeDark);
        } else {
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_converter);

        //Initialize the ConvertCalc class
        final ConvertCalc convertCalc = new ConvertCalc(this);

        //Initialize the spinners with the Init class
        SpinnersStuff.Init(this, convertCalc.latestYear);

        //Convert when the button is clicked
        ButtonsStuff.ButtonsInit(this);
        ButtonsStuff.amountEditTextOnKeyInit(this);
        ButtonsStuff.convertButtonOnClick(this, convertCalc);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.converter, menu);
        if (prefs.getBoolean(getString(R.string.preferenceDarkThemeKey), false)) {
            menu.getItem(0).setChecked(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_about:
                Utils.showCredits(this);
                break;
            case R.id.action_dark_theme:
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(getString(R.string.preferenceDarkThemeKey), !item.isChecked());
                item.setChecked(!item.isChecked());
                editor.apply();
                finish();
                startActivity(getIntent());
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
