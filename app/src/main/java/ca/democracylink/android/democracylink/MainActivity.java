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

package ca.democracylink.android.democracylink;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.PhoneNumberUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import ca.democracylink.android.democracylink.communication.CreateEmailActivity;
import ca.democracylink.android.democracylink.communication.MunicipalContactEmailSelector;
import ca.democracylink.android.democracylink.communication.MunicipalContactPhoneSelector;
import ca.democracylink.android.democracylink.member.Member;
import ca.democracylink.android.democracylink.member.MemberListActivity;
import ca.democracylink.android.democracylink.municipal.Contact;
import ca.democracylink.android.democracylink.municipal.MunicipalContactAdder;
import ca.democracylink.android.democracylink.setup.Setup1;
import ca.democracylink.android.democracylink.util.Utils;
import com.parse.FindCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {
    private String mProvince;
    private ArrayList<Contact> municipal_contacts;
    private int numMunicipalContacts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //anonymously track this app opening
        ParseAnalytics.trackAppOpenedInBackground(getIntent());
        
        final SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
        mProvince = sharedPref.getString(getString(R.string.province), "British Columbia");
        
        TextView fedNameText = (TextView)findViewById(R.id.federalNameMain);
        fedNameText.setText(sharedPref.getString(getString(R.string.federal_name), ""));
        
        TextView provNameText = (TextView)findViewById(R.id.provincialNameMain);
        provNameText.setText(sharedPref.getString(getString(R.string.provincial_name), ""));
        
        TextView muniContactsText = (TextView)findViewById(R.id.municipalMain);

        //list of all contacts which is stored in SharedPreferences. Yeah, I'm awesome at Android...
        //convert list to actual contacts here. We should always have current list in municipal_contacts.
        String contactString = sharedPref.getString("saved_muni_contacts", "");
        if(!contactString.isEmpty()){
            municipal_contacts = Contact.convertStringToContacts(contactString);
        } else{
            municipal_contacts = new ArrayList<>();
        }
        numMunicipalContacts = municipal_contacts.size();
        if(numMunicipalContacts == 1)
            muniContactsText.setText(numMunicipalContacts + " Contact");
        else
            muniContactsText.setText(numMunicipalContacts + " Contacts");
        
        setupPhoneButtons(sharedPref);
        setupEmailButtons(sharedPref);
        setupBottomButtons(sharedPref);
    }
    
    public void startAbout(View view)
    {
        Intent intent = new Intent(this, About.class);
        startActivity(intent);
    }
    
    public void startSetup(View view)
    {
        Intent intent = new Intent(this, Setup1.class);
        startActivity(intent);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        
        return true;
    }
    
    private void setupBottomButtons(final SharedPreferences sharedPref)
    {
        ImageView share_image = (ImageView) findViewById(R.id.share_image);
        share_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = getString(R.string.share_text);
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.share_subject));
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_intent_title)));
            }
        });
        
    }
    
    /**
     * Makes each email image a button and attaches to it the relevant email address
     * for that image from the database.
     */
    private void setupEmailButtons(final SharedPreferences sharedPref)
    {
        ImageView fed_email = (ImageView) findViewById(R.id.fed_email_image);
        fed_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                String federal_email_address = sharedPref.getString(getString(R.string.federal_email), "");
                if(federal_email_address.equals(""))
                {
                    Toast.makeText(MainActivity.this, getString(R.string.no_federal_email), Toast.LENGTH_SHORT).show();
                    return;
                }
                
                ArrayList<CharSequence> mailto = new ArrayList<CharSequence>();
                mailto.add(federal_email_address);
                
                Intent intent = new Intent(MainActivity.this, CreateEmailActivity.class);
                //add information about the recipient(s)
                intent.putExtra("sendto:", mailto);
                
                //add information about the user
                intent.putExtra("greeting:", getString(R.string.dear));
                intent.putExtra("senderName:", sharedPref.getString(getString(R.string.first_name), "") + " " + sharedPref.getString(getString(R.string.last_name), ""));
                intent.putExtra("degree:", sharedPref.getString(getString(R.string.degree), ""));
                intent.putExtra("employment:", sharedPref.getString(getString(R.string.employment), ""));
                intent.putExtra("employer:", sharedPref.getString(getString(R.string.employer), ""));
                intent.putExtra("signature:", sharedPref.getString(getString(R.string.email_signature), "Sincerely"));
                intent.putExtra("address:", sharedPref.getString(getString(R.string.street_address), ""));
                intent.putExtra("city:", sharedPref.getString(getString(R.string.city), ""));
                intent.putExtra("postalCode:", sharedPref.getString(getString(R.string.postal_code), ""));
                intent.putExtra("unitNo:", sharedPref.getString(getString(R.string.unit_no), ""));

                intent.putExtra("province:", "Canada");
                
                ArrayList<CharSequence> memberName = new ArrayList<CharSequence>();
                memberName.add(sharedPref.getString(getString(R.string.federal_name), ""));
                intent.putCharSequenceArrayListExtra("recipientNames:", memberName);
                
                startActivity(intent);
                
            }
        });
        ImageView prov_email = (ImageView) findViewById(R.id.prov_email_image);
        prov_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                String provincial_email_address = sharedPref.getString(getString(R.string.provincial_email), "");
                if(provincial_email_address.equals(""))
                {
                    Toast.makeText(MainActivity.this, getString(R.string.no_provincial_email), Toast.LENGTH_SHORT).show();
                    return;
                }
                
                ArrayList<CharSequence> mailto = new ArrayList<CharSequence>();
                mailto.add(provincial_email_address);
                
                Intent intent = new Intent(MainActivity.this, CreateEmailActivity.class);
                intent.putExtra("sendto:", mailto);

                //add information about the user
                intent.putExtra("greeting:", getString(R.string.dear));
                intent.putExtra("senderName:", sharedPref.getString(getString(R.string.first_name), "") + " " + sharedPref.getString(getString(R.string.last_name), ""));
                intent.putExtra("degree:", sharedPref.getString(getString(R.string.degree), ""));
                intent.putExtra("employment:", sharedPref.getString(getString(R.string.employment), ""));
                intent.putExtra("employer:", sharedPref.getString(getString(R.string.employer), ""));
                intent.putExtra("signature:", sharedPref.getString(getString(R.string.email_signature), "Sincerely"));
                intent.putExtra("address:", sharedPref.getString(getString(R.string.street_address), ""));
                intent.putExtra("city:", sharedPref.getString(getString(R.string.city), ""));
                intent.putExtra("postalCode:", sharedPref.getString(getString(R.string.postal_code), ""));
                intent.putExtra("unitNo:", sharedPref.getString(getString(R.string.unit_no), ""));

                intent.putExtra("province:", mProvince);
                
                ArrayList<CharSequence> memberName = new ArrayList<CharSequence>();
                memberName.add(sharedPref.getString(getString(R.string.provincial_name), ""));
                intent.putCharSequenceArrayListExtra("recipientNames:", memberName);
                
                startActivity(intent);
                
            }
        });
        ImageView muni_email = (ImageView) findViewById(R.id.muni_email_image);
        muni_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(municipal_contacts.isEmpty()){
                    Toast.makeText(getApplicationContext(), "No municipal members found.", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(MainActivity.this, MunicipalContactEmailSelector.class);
                    intent.putParcelableArrayListExtra("contacts", municipal_contacts);
                    startActivity(intent);
                }
                
            }
        });
    }

    /**
     * Makes each phone image a button and attaches to it the relevant phone number
     * for that image from the database.
     */
    public void setupPhoneButtons(final SharedPreferences sharedPref)
    {
        ImageView fed_phone = (ImageView)findViewById(R.id.fed_phone_image);
        fed_phone.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                
                
                
                String federal_phone = sharedPref.getString(getString(R.string.federal_phone), "");
                if(federal_phone.equals(""))
                {
                    Toast.makeText(MainActivity.this, getString(R.string.no_federal_phone), Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!PhoneNumberUtils.isGlobalPhoneNumber(federal_phone))
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    String title = (String)getString(R.string.bad_phone_number_title);
                    String text = ((String)getString(R.string.bad_phone_number_text));
                    builder.setTitle(title);
                    builder.setMessage(text);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.create();
                    builder.show();
                }
                else if(hasCallingPermission())
                {
                    Utils.incrementNumPhoneCalls(sharedPref);
                    
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + federal_phone));
                    startActivity(callIntent);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), getString(R.string.phone_permissions_unavailable), Toast.LENGTH_LONG).show();
                }
            }
        });

        ImageView prov_phone = (ImageView)findViewById(R.id.prov_phone_image);
        prov_phone.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                
                String provincial_phone = sharedPref.getString(getString(R.string.provincial_phone), "");
                if(provincial_phone.equals(""))
                {
                    Toast.makeText(MainActivity.this, getString(R.string.no_provincial_phone), Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!PhoneNumberUtils.isGlobalPhoneNumber(provincial_phone))
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    String title = (String)getString(R.string.bad_phone_number_title);
                    String text = ((String)getString(R.string.bad_phone_number_text));
                    builder.setTitle(title);
                    builder.setMessage(text);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.create();
                    builder.show();
                }
                else if(hasCallingPermission())
                {
                    Utils.incrementNumPhoneCalls(sharedPref);
                    
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + provincial_phone));
                    startActivity(callIntent);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), getString(R.string.phone_permissions_unavailable), Toast.LENGTH_LONG).show();
                }
            }
        });

        ImageView muni_phone = (ImageView)findViewById(R.id.muni_phone_image);
        muni_phone.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                //TODO: Get municipal phone number from database here
                
                if(hasCallingPermission())
                {
                    if(municipal_contacts.isEmpty()){
                        Toast.makeText(getApplicationContext(), "No municipal members found.", Toast.LENGTH_LONG).show();
                    } else {
                        Intent intent = new Intent(MainActivity.this, MunicipalContactPhoneSelector.class);
                        intent.putParcelableArrayListExtra("contacts", municipal_contacts);
                        startActivity(intent);
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), getString(R.string.phone_permissions_unavailable), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        
        return super.onOptionsItemSelected(item);
    }

    /**
     * Checks if the application has permission to make phone calls.
     * @return true if app can make phone calls, false otherwise
     */
    public boolean hasCallingPermission()
    {
        String permission = "android.permission.CALL_PHONE";
        
        int res = getApplicationContext().checkCallingOrSelfPermission(permission);
        return res == PackageManager.PERMISSION_GRANTED;

    }

    /**
     * Called when user taps on federal members name in the main activity. This should bring them
     * to the federal member selector so they can quickly change their federal member without
     * repeating all of setup.
     * @param view
     */
    public void reselectFederalMember(View view)
    {

        //start loading dialog with a timeout
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.loading));
        progressDialog.setMessage(getString(R.string.fetching_data));
        progressDialog.setCancelable(false);
        Utils.displayWaitingDialogWithTimeout(MainActivity.this, progressDialog);


        final String objectName = "Federal_Members";
        final String title = getString(R.string.fedMPs);
        
        /*
        This block of code will fetch members from the internet every single time. The commented
        block underneath this will first try to fetch from the Parse local datastore before internet.
         */
        if(Utils.isNetworkAvailable(MainActivity.this)){  //fetch from parse online
            ParseQuery<ParseObject> query = ParseQuery.getQuery(objectName);
            query.setLimit(500);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> parseObjects, ParseException e) {
                    if(e!=null)
                        return;
                    if(parseObjects.size() == 0) {
                        Toast.makeText(MainActivity.this, "Could not fetch MPs, try sending a bug report.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    //converts to Members then assigns them to the proper global variable
                    //convertParseObjectToMembers(objectName, parseObjects);
                    MainActivity.this.gotMembers(progressDialog, parseObjects, title, Utils.FED_MEMB, "Canada");

                    //save the newly found MPs
                    //ParseObject.pinAllInBackground(parseObjects);
                }
            });
        } else{
            Toast.makeText(MainActivity.this, "Could not fetch MPs. Check your network Connection", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
    }
    
    public void reselectProvincialMember(View view)
    {

        //start loading dialog with a timeout
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.loading));
        progressDialog.setMessage(getString(R.string.fetching_data));
        progressDialog.setCancelable(false);
        Utils.displayWaitingDialogWithTimeout(MainActivity.this, progressDialog);


        final String objectName = Utils.convertProvinceToShortProvince(mProvince)+"_Members";
        final String title = getString(R.string.provMPs) + " - " + Utils.convertProvinceToShortProvince(mProvince);
        
        /*
        This block of code will fetch members from the internet every single time. The commented
        block underneath this will first try to fetch from the Parse local datastore before internet.
         */
        if(Utils.isNetworkAvailable(MainActivity.this)){  //fetch from parse online
            ParseQuery<ParseObject> query = ParseQuery.getQuery(objectName);
            query.setLimit(250);    //should be more than enough to fetch provincial MPs from any province
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> parseObjects, ParseException e) {
                    if(e!=null)
                        return;
                    if(parseObjects.size() == 0) {
                        Toast.makeText(MainActivity.this, "Could not fetch MPs, try sending a bug report.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    //converts to Members then assigns them to the proper global variable
                    //convertParseObjectToMembers(objectName, parseObjects);
                    MainActivity.this.gotMembers(progressDialog, parseObjects, title, Utils.PROV_MEMB, mProvince);

                    //save the newly found MPs
                    //ParseObject.pinAllInBackground(parseObjects);
                }
            });
        } else{
            Toast.makeText(MainActivity.this, "Could not fetch MPs. Check your network Connection", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
    }
    
    /**
     * Members of the appropriate region have been found. Now start the Member selector so user can
     * change their member easily.
     * @param progressDialog
     * @param list
     * @param title
     * @param requestCode
     * @param province Province name, or "Canada" if federal.
     */
    private void gotMembers(ProgressDialog progressDialog, List<ParseObject> list, String title, int requestCode, String province)
    {
        //if dialog was dismissed don't go to the next activity
        if(!progressDialog.isShowing())
            return;
        
        ArrayList<Member> members = convertParseObjectToMembers(list);
        Intent intent = new Intent(MainActivity.this, MemberListActivity.class);
        intent.putParcelableArrayListExtra("members", members);
        intent.putExtra("title", title);
        intent.putExtra("province", province);
        startActivityForResult(intent, requestCode);
        progressDialog.dismiss();
    }

    /**
     * Is probably called from MemberListActivity or MunicipalMemberAdder and the intent
     * has the member selected.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        
        if(data == null)//this is the case if the user pressed the back button rather than selecting someone
            return;
        
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        if(requestCode == Utils.MUNI_MEMB){
            
            String contactString = sharedPref.getString("saved_muni_contacts", "");
            if(!contactString.isEmpty()){
                municipal_contacts = Contact.convertStringToContacts(contactString);
            } else{
                municipal_contacts = new ArrayList<>();
            }

            int numMuniMembers = municipal_contacts.size();
            TextView muniContactCount = (TextView)findViewById(R.id.municipalMain);
            if(numMuniMembers == 1)
                muniContactCount.setText(numMuniMembers + " Contact");
            else
                muniContactCount.setText(numMuniMembers + " Contacts");
        }
        else {
            String email = data.getStringExtra("email");
            String phone = data.getStringExtra("phone");
            String name = data.getStringExtra("name");
            String constituency = data.getStringExtra("constituency");

            if (requestCode == Utils.FED_MEMB) {
                TextView fedNameText = (TextView) findViewById(R.id.federalNameMain);
                fedNameText.setText(name);

                editor.putString(getString(R.string.federal_phone), phone);
                editor.putString(getString(R.string.federal_email), email);
                editor.putString(getString(R.string.federal_name), name);
                editor.putString("fedRepConstituency", constituency);
                editor.apply();
            } else if (requestCode == Utils.PROV_MEMB) {
                TextView provNameText = (TextView) findViewById(R.id.provincialNameMain);
                provNameText.setText(name);

                editor.putString(getString(R.string.provincial_phone), phone);
                editor.putString(getString(R.string.provincial_email), email);
                editor.putString(getString(R.string.provincial_name), name);
                editor.putString("provRepConstituency", constituency);
                editor.apply();
            }
        }

    }
    
    /**
     * This is basically copied from Setup4. I didn't move it to Utils because there are lots of
     * private variables in the Setup4 method and refactoring that sounded like a lot of work...
     * @param list
     */
    private ArrayList<Member> convertParseObjectToMembers(List<ParseObject> list)
    {
        ArrayList<Member> members = new ArrayList<Member>();
        Member m;
        String name = "";
        String constituency = "";
        String email = "";
        String phone = "";
        String province = "";
        boolean federal = false;
        for(ParseObject member: list)
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
        
        return members;
    }

    /**
     * Called when user presses on the text beneath 'Municipal' on the main page.
     * @param view
     */
    public void editMunicipalMembers(View view)
    {
        Intent intent = new Intent(MainActivity.this, MunicipalContactAdder.class);
        intent.putParcelableArrayListExtra("contacts", municipal_contacts);
        startActivityForResult(intent, Utils.MUNI_MEMB);
        
        
    }
}
