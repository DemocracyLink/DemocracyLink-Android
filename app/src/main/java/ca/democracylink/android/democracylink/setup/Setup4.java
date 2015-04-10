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
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import ca.democracylink.android.democracylink.MainActivity;
import ca.democracylink.android.democracylink.R;
import ca.democracylink.android.democracylink.member.MemberListActivity;
import ca.democracylink.android.democracylink.member.Member;
import ca.democracylink.android.democracylink.municipal.Contact;
import ca.democracylink.android.democracylink.municipal.MunicipalContactAdder;
import ca.democracylink.android.democracylink.util.Utils;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


public class Setup4 extends Activity {

    private String mProvince;
    
    private ArrayList<Member> federal_members;
    private ArrayList<Member> provincial_members;
    private ArrayList<Contact> municipal_contacts;
    
    private ProgressDialog provincialWaitingDialog;
    private ProgressDialog federalWaitingDialog;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ca.democracylink.android.democracylink.R.layout.activity_setup4);

        SharedPreferences sharedPref = getSharedPreferences(getString(ca.democracylink.android.democracylink.R.string.preference_file_key), MODE_PRIVATE);
        mProvince = sharedPref.getString(getString(ca.democracylink.android.democracylink.R.string.province), "British Columbia");
        String shortProvince = Utils.convertProvinceToShortProvince(mProvince);

        provincialWaitingDialog = new ProgressDialog(Setup4.this, ProgressDialog.STYLE_SPINNER);
        provincialWaitingDialog.setTitle(getString(ca.democracylink.android.democracylink.R.string.loading));
        provincialWaitingDialog.setMessage(getString(ca.democracylink.android.democracylink.R.string.fetching_data));
        provincialWaitingDialog.setIndeterminate(true);
        provincialWaitingDialog.setCancelable(false);
        federalWaitingDialog = new ProgressDialog(Setup4.this, ProgressDialog.STYLE_SPINNER);
        federalWaitingDialog.setTitle(getString(ca.democracylink.android.democracylink.R.string.loading));
        federalWaitingDialog.setMessage(getString(ca.democracylink.android.democracylink.R.string.fetching_data));
        federalWaitingDialog.setIndeterminate(true);
        federalWaitingDialog.setCancelable(false);
        
        addDataToFields();
    }
    
    /**
     * This is called when we press the search button beside the Federal fields.
     * @param view
     */
    public void chooseFederalMember(View view)
    {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.loading));
        progressDialog.setMessage(getString(R.string.fetching_data));
        progressDialog.setCancelable(false);
        Utils.displayWaitingDialogWithTimeout(Setup4.this, progressDialog);
        
        final String objectName = "Federal_Members";
        
        /*
        This block of code will fetch members from the internet every single time. The commented
        block underneath this will first try to fetch from the Parse local datastore before internet.
         */
        if(Utils.isNetworkAvailable(Setup4.this)){  //fetch from parse online
            ParseQuery<ParseObject> query = ParseQuery.getQuery(objectName);
            query.setLimit(500);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> parseObjects, ParseException e) {
                    if(e!=null)
                        return;
                    if(parseObjects.size() == 0) {
                        Toast.makeText(Setup4.this, "No MPs found, try sending a bug report.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    
                    //converts to Members then assigns them to the proper global variable
                    convertParseObjectToMembers(objectName, parseObjects);
                    
                    //only start the next activity if user hasn't cancelled the loading dialog
                    //and it hasn't timed out
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        Intent intent = new Intent(Setup4.this, MemberListActivity.class);
                        intent.putParcelableArrayListExtra("members", federal_members);
                        intent.putExtra("title", getString(R.string.fedMPs));
                        intent.putExtra("province", "Canada");
                        startActivityForResult(intent, Utils.FED_MEMB);
                    }
                }
            });
        } else{
            Toast.makeText(Setup4.this, "Could not fetch MPs. Check your network Connection", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
        
    }
    
    
    
    /**
     * This is called when we press the search button beside the Provincial fields.
     * @param view
     */
    public void chooseProvincialMember(View view)
    {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.loading));
        progressDialog.setMessage(getString(R.string.fetching_data));
        progressDialog.setCancelable(false);
        Utils.displayWaitingDialogWithTimeout(Setup4.this, progressDialog);
        
        
        final String objectName = Utils.convertProvinceToShortProvince(mProvince)+"_Members";
        
        /*
        This block of code will fetch members from the internet every single time. The commented
        block underneath this will first try to fetch from the Parse local datastore before internet.
         */
        if(Utils.isNetworkAvailable(Setup4.this)){  //fetch from parse online
            ParseQuery<ParseObject> query = ParseQuery.getQuery(objectName);
            query.setLimit(250);    //should be more than enough to fetch provincial MPs from any province
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> parseObjects, ParseException e) {
                    if(e!=null)
                        return;
                    if(parseObjects.size() == 0) {
                        Toast.makeText(Setup4.this, "Could not fetch MPs, try sending a bug report.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    //converts to Members then assigns them to the proper global variable
                    convertParseObjectToMembers(objectName, parseObjects);
                    
                    //only start the next activity if user hasn't cancelled the loading dialog
                    //and it hasn't timed out
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        Intent intent = new Intent(Setup4.this, MemberListActivity.class);
                        intent.putParcelableArrayListExtra("members", provincial_members);
                        intent.putExtra("title", getString(R.string.provMPs) + " - " + Utils.convertProvinceToShortProvince(mProvince));
                        intent.putExtra("province", mProvince);
                        startActivityForResult(intent, Utils.PROV_MEMB);
                    }
                }
            });
        } else{
            Toast.makeText(Setup4.this, "Could not fetch MPs. Check your network Connection", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
        
        
    }
    
    /**
     * This is called when we press the search button beside the Municipal fields.
     * @param view
     */
    public void chooseMunicipalMember(View view)
    {
        Intent intent = new Intent(Setup4.this, MunicipalContactAdder.class);
        intent.putParcelableArrayListExtra("contacts", municipal_contacts);
        startActivityForResult(intent, Utils.MUNI_MEMB);
    }
    
    /**
     * Is probably called from MemberListActivity or MunicipalMemberAdder and the intent
     * has the member that was selected.
     * This will set the text fields of the appropriate member (federal or provincial) and save the
     * data to sharedPreferences. We save here so that if we go back or go to municipal adder we 
     * still have the data.
     * If we're coming from MunicipalMemberAdder we do nothing.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        
        if(data == null)//this is the case if the user pressed the back button rather than selecting someone
            return;
        
        SharedPreferences sharedPref = getSharedPreferences(getString(ca.democracylink.android.democracylink.R.string.preference_file_key), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        
        if(requestCode == Utils.MUNI_MEMB){
            municipal_contacts = data.getParcelableArrayListExtra("muni_contacts");
        }
        else {

            String email = data.getStringExtra("email");
            String phone = data.getStringExtra("phone");
            String name = data.getStringExtra("name");
            String constituency = data.getStringExtra("constituency");

            if (requestCode == Utils.FED_MEMB) {
                EditText fed_email = (EditText) findViewById(ca.democracylink.android.democracylink.R.id.federalEmail);
                EditText fed_phone = (EditText) findViewById(ca.democracylink.android.democracylink.R.id.federalPhone);
                EditText fed_name = (EditText) findViewById(ca.democracylink.android.democracylink.R.id.federalName);

                //we really don't even need to save constituency but maybe sometime it will be useful...
                editor.putString("fedRepConstituency", constituency);
                editor.putString(getString(ca.democracylink.android.democracylink.R.string.federal_name), name);
                editor.putString(getString(ca.democracylink.android.democracylink.R.string.federal_email), email);
                editor.putString(getString(ca.democracylink.android.democracylink.R.string.federal_phone), phone);
                editor.apply();

                fed_email.setText(email);
                fed_phone.setText(phone);
                fed_name.setText(name);
            } else if (requestCode == Utils.PROV_MEMB) {
                EditText prov_email = (EditText) findViewById(ca.democracylink.android.democracylink.R.id.provincialEmail);
                EditText prov_phone = (EditText) findViewById(ca.democracylink.android.democracylink.R.id.provincialPhone);
                EditText prov_name = (EditText) findViewById(ca.democracylink.android.democracylink.R.id.provincialName);

                editor.putString("provRepConstituency", constituency);
                editor.putString(getString(ca.democracylink.android.democracylink.R.string.provincial_name), name);
                editor.putString(getString(ca.democracylink.android.democracylink.R.string.provincial_email), email);
                editor.putString(getString(ca.democracylink.android.democracylink.R.string.provincial_phone), phone);
                editor.apply();

                prov_email.setText(email);
                prov_phone.setText(phone);
                prov_name.setText(name);
            }
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(ca.democracylink.android.democracylink.R.menu.setup4, menu);
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
     * Finishes setup and goes to the main page.
     * @param view
     */
    public void startMainActivity(View view)
    {
        if(!validateData())
            return;
        
        SharedPreferences sharedPref = getSharedPreferences(getString(ca.democracylink.android.democracylink.R.string.preference_file_key), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        
        editor.putString("Done Setup", "Yup, we sure are done setup");//WARNING: This is a hardcoded value that must match the value in onCreate() in WelcomeActivity
        editor.apply();
        
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Checks if required fields are filled in and if not, sets an error message on each field.
     * @return
     */
    private boolean validateData() {

        boolean allFieldsGood = true;
        
        EditText fedName = (EditText)findViewById(ca.democracylink.android.democracylink.R.id.federalName);
        EditText fedPhone = (EditText)findViewById(ca.democracylink.android.democracylink.R.id.federalPhone);
        EditText fedEmail = (EditText)findViewById(ca.democracylink.android.democracylink.R.id.federalEmail);
        EditText provName = (EditText)findViewById(ca.democracylink.android.democracylink.R.id.provincialName);
        EditText provPhone = (EditText)findViewById(ca.democracylink.android.democracylink.R.id.provincialPhone);
        EditText provEmail = (EditText)findViewById(ca.democracylink.android.democracylink.R.id.provincialEmail);
        
        if(!fedName.getText().toString().trim().equals(""))//if a federal name is entered, there must be either a federal phone number or a federal email
        {
            if(fedPhone.getText().toString().trim().equals("") && fedEmail.getText().toString().trim().equals(""))
            {
                Toast.makeText(this, getString(ca.democracylink.android.democracylink.R.string.needFederalInfo), Toast.LENGTH_LONG).show();
                allFieldsGood = false;
            }
        }
        
        if(!provName.getText().toString().trim().equals(""))//if a federal name is entered, there must be either a federal phone number or a federal email
        {
            if(provEmail.getText().toString().trim().equals("") && provPhone.getText().toString().trim().equals(""))
            {
                Toast.makeText(this, getString(ca.democracylink.android.democracylink.R.string.needProvincialInfo), Toast.LENGTH_LONG).show();
                allFieldsGood = false;
            }
        }
        
        
        return allFieldsGood;
    }

    /**
     * Loads saved data from SharedPreferences into each field if it exists.
     */
    public void addDataToFields()
    {
        SharedPreferences sharedPref = getSharedPreferences(getString(ca.democracylink.android.democracylink.R.string.preference_file_key), MODE_PRIVATE);
        
        String federal_email = sharedPref.getString(getString(ca.democracylink.android.democracylink.R.string.federal_email), "");
        EditText fedEmailText = (EditText) findViewById(ca.democracylink.android.democracylink.R.id.federalEmail);
        fedEmailText.setText(federal_email);
        String provincial_email = sharedPref.getString(getString(ca.democracylink.android.democracylink.R.string.provincial_email), "");
        EditText provEmailText = (EditText) findViewById(ca.democracylink.android.democracylink.R.id.provincialEmail);
        provEmailText.setText(provincial_email);

        String federal_name = sharedPref.getString(getString(ca.democracylink.android.democracylink.R.string.federal_name), "");
        EditText fedNameText = (EditText) findViewById(ca.democracylink.android.democracylink.R.id.federalName);
        fedNameText.setText(federal_name);
        String provincial_name = sharedPref.getString(getString(ca.democracylink.android.democracylink.R.string.provincial_name), "");
        EditText provNameText = (EditText) findViewById(ca.democracylink.android.democracylink.R.id.provincialName);
        provNameText.setText(provincial_name);
        
        String federal_phone = sharedPref.getString(getString(ca.democracylink.android.democracylink.R.string.federal_phone), "");
        EditText fedPhoneText = (EditText) findViewById(ca.democracylink.android.democracylink.R.id.federalPhone);
        fedPhoneText.setText(federal_phone);
        String provincial_phone = sharedPref.getString(getString(ca.democracylink.android.democracylink.R.string.provincial_phone), "");
        EditText provPhoneText = (EditText) findViewById(ca.democracylink.android.democracylink.R.id.provincialPhone);
        provPhoneText.setText(provincial_phone);
    }
    
   /**
     * Converts a list of given Parse objects (either provincial or federal members) into 
     * corresponding Member objects and saves them to the corresponding local variable.
     * Then clears the ProgressDialog if it is active.
     * @param objectName
     * @param fetchedObjects
     */
    public void convertParseObjectToMembers(String objectName, List<ParseObject> fetchedObjects)
    {
        ArrayList<Member> members = new ArrayList<Member>();
        Member m;
        String name = "";
        String constituency = "";
        String email = "";
        String phone = "";
        String province = "";
        boolean federal = false;
        for(ParseObject member: fetchedObjects)
        {
            name = member.getString("Name");
            constituency = member.getString("Constituency");
            phone = member.getString("Phone");
            email = member.getString("Email");
            province = member.getString("Province");
            federal = member.getString("Province") != null;
            
            if(!federal || (federal && Utils.convertProvinceToShortProvince(province).equals(Utils.convertProvinceToShortProvince(mProvince))))
            {
                m = new Member(name, constituency, email, phone, province, federal);
                members.add(m);
            }
        }
        if(objectName.equals("Federal_Members")) {
            Setup4.this.federal_members = members;
            //if waiting dialog is showing the user has tried to find a member so start that activity
//            if(federalWaitingDialog.isShowing())
//            {
//                federalWaitingDialog.dismiss();
//                chooseFederalMember(null);
//            }
        }
        else {
            Setup4.this.provincial_members = members;
            //if waiting dialog is showing the user has tried to find a member so start that activity
//            if(provincialWaitingDialog.isShowing())
//            {
//                provincialWaitingDialog.dismiss();
//                chooseProvincialMember(null);
//            }
        }
        
        Log.i("Parse", "Found " + fetchedObjects.size() + " objects with type " + objectName);
    }
}
