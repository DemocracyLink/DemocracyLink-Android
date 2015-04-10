/*
Copyright 2015 Ben Armstrong, Geoffrey De Ruiter

This file is part of Democracy Link.

Democracy Link is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ca.democracylink.android.democracylink.setup;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import ca.democracylink.android.democracylink.util.Utils;


public class Setup3 extends Activity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ca.democracylink.android.democracylink.R.layout.activity_setup3);

        addDataToFields();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(ca.democracylink.android.democracylink.R.menu.setup2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }

    /**
     * Moves to next phase of setup.
     * @param view
     */
    public void startSetup4(View view)
    {
        if(isValid())
        {
            saveData();

            Intent intent = new Intent(this, Setup4.class);
            startActivity(intent);
        }
    }

    /**
     * TODO: have this check required fields for values
     * @return true if all required fields are filled in, false otherwise.
     */
    public boolean isValid()
    {
        return true;
    }

    /**
     * Loads saved data from SharedPreferences into each field if it exists.
     */
    public void addDataToFields()
    {
        SharedPreferences sharedPref = getSharedPreferences(getString(ca.democracylink.android.democracylink.R.string.preference_file_key), MODE_PRIVATE);

        //Street Address
        String street_address = sharedPref.getString(getString(ca.democracylink.android.democracylink.R.string.street_address), "");
        EditText address_edittext = (EditText) findViewById(ca.democracylink.android.democracylink.R.id.streetAddressText);
        address_edittext.setText(street_address);

        //City/Town
        String city = sharedPref.getString(getString(ca.democracylink.android.democracylink.R.string.city), "");
        EditText city_edittext = (EditText) findViewById(ca.democracylink.android.democracylink.R.id.cityText);
        city_edittext.setText(city);

        //Unit Number
        String unit_no = sharedPref.getString(getString(ca.democracylink.android.democracylink.R.string.unit_no), "");
        EditText unit_edittext = (EditText) findViewById(ca.democracylink.android.democracylink.R.id.unitText);
        unit_edittext.setText(unit_no);

        //Postal Code
        String postal_code = sharedPref.getString(getString(ca.democracylink.android.democracylink.R.string.postal_code), "");
        EditText postal_code_edittext = (EditText) findViewById(ca.democracylink.android.democracylink.R.id.postalCodeText);
        postal_code_edittext.setText(postal_code);

        //Province/Territory
        String province = sharedPref.getString(getString(ca.democracylink.android.democracylink.R.string.province), "British Columbia");
        Spinner province_spinner = (Spinner) findViewById(ca.democracylink.android.democracylink.R.id.provinceSpinner);
        province_spinner.setSelection(Utils.getIndex(province_spinner, province));
    }

    /**
     * Saves the data that the user has entered into the application's shared preferences.
     */
    public void saveData()
    {
        SharedPreferences sharedPref = getSharedPreferences(getString(ca.democracylink.android.democracylink.R.string.preference_file_key), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        //Street Address
        EditText address_edittext = (EditText) findViewById(ca.democracylink.android.democracylink.R.id.streetAddressText);
        String street_address = address_edittext.getText().toString();
        editor.putString(getString(ca.democracylink.android.democracylink.R.string.street_address), street_address);

        //City/Town
        EditText city_edittext = (EditText) findViewById(ca.democracylink.android.democracylink.R.id.cityText);
        String city = city_edittext.getText().toString();
        editor.putString(getString(ca.democracylink.android.democracylink.R.string.city), city);

        //Unit Number
        EditText unit_edittext = (EditText) findViewById(ca.democracylink.android.democracylink.R.id.unitText);
        String unit_no = unit_edittext.getText().toString();
        editor.putString(getString(ca.democracylink.android.democracylink.R.string.unit_no), unit_no);

        //Postal Code
        EditText postal_code_edittext = (EditText) findViewById(ca.democracylink.android.democracylink.R.id.postalCodeText);
        String postal_code = postal_code_edittext.getText().toString();
        editor.putString(getString(ca.democracylink.android.democracylink.R.string.postal_code), postal_code);

        //Province/Territory
        Spinner province_spinner = (Spinner) findViewById(ca.democracylink.android.democracylink.R.id.provinceSpinner);
        String province = province_spinner.getSelectedItem().toString();
        editor.putString(getString(ca.democracylink.android.democracylink.R.string.province), province);

        editor.apply();
    }
}
