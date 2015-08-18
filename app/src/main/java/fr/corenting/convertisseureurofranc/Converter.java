package fr.corenting.convertisseureurofranc;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class Converter extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_converter);

        //Initialize the spinners with the Init class
        SpinnersStuff.Init(this);

        //Initialize the ConvertCalc class
        final ConvertCalc convertCalc = new ConvertCalc(getApplicationContext());

        //Convert when the button is clicked
        ButtonsStuff.ButtonsInit(this);
        ButtonsStuff.amountEditTextOnKeyInit(this);
        ButtonsStuff.convertButtonOnClick(this, convertCalc);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.converter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (item.getItemId() == R.id.action_about) {
            Utils.showCredits(this);
        }
        return super.onOptionsItemSelected(item);
    }
}
