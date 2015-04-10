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


public class Setup2 extends Activity {

    private boolean validFields;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ca.democracylink.android.democracylink.R.layout.activity_setup2);
        
        validFields = true;
        
        addDataToFields();
    }

    /**
     * Loads saved data from SharedPreferences into each field if it exists.
     */
    public void addDataToFields()
    {
        SharedPreferences sharedPref = getSharedPreferences(getString(ca.democracylink.android.democracylink.R.string.preference_file_key), MODE_PRIVATE);

        //First name
        String first_name = sharedPref.getString(getString(ca.democracylink.android.democracylink.R.string.first_name), "");
        EditText first_edittext = (EditText) findViewById(ca.democracylink.android.democracylink.R.id.firstNameText);
        first_edittext.setText(first_name);

        //Last name
        String last_name = sharedPref.getString(getString(ca.democracylink.android.democracylink.R.string.last_name), "");
        EditText last_edittext = (EditText) findViewById(ca.democracylink.android.democracylink.R.id.lastNameText);
        last_edittext.setText(last_name);

        //Degree
        String degree = sharedPref.getString(getString(ca.democracylink.android.democracylink.R.string.degree), "");
        EditText degree_edittext = (EditText) findViewById(ca.democracylink.android.democracylink.R.id.degreeText);
        degree_edittext.setText(degree);

        //Employment Position
        String employment_pos = sharedPref.getString(getString(ca.democracylink.android.democracylink.R.string.employment), "");
        EditText employment_pos_edittext = (EditText) findViewById(ca.democracylink.android.democracylink.R.id.employmentPositionText);
        employment_pos_edittext.setText(employment_pos);

        //Employer
        String employer = sharedPref.getString(getString(ca.democracylink.android.democracylink.R.string.employer), "");
        EditText employer_edittext = (EditText) findViewById(ca.democracylink.android.democracylink.R.id.employerText);
        employer_edittext.setText(employer);

        //Signature
        String signature = sharedPref.getString(getString(ca.democracylink.android.democracylink.R.string.email_signature), "Sincerely");
        Spinner signature_spinner = (Spinner) findViewById(ca.democracylink.android.democracylink.R.id.signatureSpinner);
        signature_spinner.setSelection(Utils.getIndex(signature_spinner, signature));
    }

    /**
     * Saves the data that the user has entered into the application's shared preferences.
     */
    public void saveData()
    {
        SharedPreferences sharedPref = getSharedPreferences(getString(ca.democracylink.android.democracylink.R.string.preference_file_key), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        
        //First name
        EditText first_edittext = (EditText) findViewById(ca.democracylink.android.democracylink.R.id.firstNameText);
        String first = first_edittext.getText().toString();
        editor.putString(getString(ca.democracylink.android.democracylink.R.string.first_name), first);
        
        //Last name
        EditText last_edittext = (EditText) findViewById(ca.democracylink.android.democracylink.R.id.lastNameText);
        String last = last_edittext.getText().toString();
        editor.putString(getString(ca.democracylink.android.democracylink.R.string.last_name), last);
        
        //Degree
        EditText degree_edittext = (EditText) findViewById(ca.democracylink.android.democracylink.R.id.degreeText);
        String degree = degree_edittext.getText().toString();
        editor.putString(getString(ca.democracylink.android.democracylink.R.string.degree), degree);
        
        //Employment Position
        EditText employment_pos_edittext = (EditText) findViewById(ca.democracylink.android.democracylink.R.id.employmentPositionText);
        String employment_pos = employment_pos_edittext.getText().toString();
        editor.putString(getString(ca.democracylink.android.democracylink.R.string.employment), employment_pos);
        
        //Employer
        EditText employer_edittext = (EditText) findViewById(ca.democracylink.android.democracylink.R.id.employerText);
        String employer = employer_edittext.getText().toString();
        editor.putString(getString(ca.democracylink.android.democracylink.R.string.employer), employer);
        
        //Signature
        Spinner signature_spinner = (Spinner) findViewById(ca.democracylink.android.democracylink.R.id.signatureSpinner);
        String my_signature = signature_spinner.getSelectedItem().toString();
        editor.putString(getString(ca.democracylink.android.democracylink.R.string.email_signature), my_signature);
        
        editor.apply();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(ca.democracylink.android.democracylink.R.menu.setup1, menu);
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
     * Checks if all fields are valid and, if so, moves on to next phase of setup. Called when
     * continue button is pressed.
     * @param view
     */
    public void startSetup3(View view)
    {
        checkRequiredFields();
        
        if(isValid())
        {
            saveData();

            Intent intent = new Intent(this, Setup3.class);
            startActivity(intent);
        }
    }

    /**
     * Checks if required fields are empty and if they are sets an error on them.
     */
    private void checkRequiredFields()
    {
        EditText firstNameText = (EditText)findViewById(ca.democracylink.android.democracylink.R.id.firstNameText);
        EditText lastNameText = (EditText)findViewById(ca.democracylink.android.democracylink.R.id.lastNameText);
        
        boolean allGoodFields = true;
        
        if(firstNameText.getText().toString().trim().isEmpty())
        {
            firstNameText.setError(getString(ca.democracylink.android.democracylink.R.string.first_name_error));
            allGoodFields = false;
        }
        if(lastNameText.getText().toString().trim().isEmpty())
        {
            lastNameText.setError(getString(ca.democracylink.android.democracylink.R.string.last_name_error));;
            allGoodFields = false;
        }
        
        validFields = allGoodFields;
    }

    /**
     * TODO: have this check required fields for values
     * @return true if all required fields are filled in, false otherwise.
     */
    public boolean isValid()
    {
        return validFields;
    }
}
