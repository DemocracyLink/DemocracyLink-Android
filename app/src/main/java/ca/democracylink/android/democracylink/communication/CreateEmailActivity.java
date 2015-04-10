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

package ca.democracylink.android.democracylink.communication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import ca.democracylink.android.democracylink.member.minister.Minister;
import ca.democracylink.android.democracylink.member.minister.MinisterListActivity;
import ca.democracylink.android.democracylink.util.Utils;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


public class CreateEmailActivity extends Activity {

    private String mProvince;
    private ArrayList<CharSequence> recipientEmails;//emails of initial recipients of the message
    private ArrayList<CharSequence> recipientNames;//names of initial recipients of the message
    private String greeting;
    private ArrayList<Minister> checkedMinisters;//ministers that were selected by the user
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ca.democracylink.android.democracylink.R.layout.activity_create_email);
        Intent intent = getIntent();
        checkedMinisters = new ArrayList<>();
        
        recipientEmails = intent.getCharSequenceArrayListExtra("sendto:");
        recipientNames = intent.getCharSequenceArrayListExtra("recipientNames:");
        String formattedRecipientNames = formatRecipientNames(recipientNames);
        
        mProvince = intent.getStringExtra("province:");
        
        boolean municipal = intent.getBooleanExtra("municipal:", false);
        if(municipal)
        {
            TextView addMinistersText = (TextView)findViewById(ca.democracylink.android.democracylink.R.id.addMinistersText);
            addMinistersText.setVisibility(View.INVISIBLE);
        }
        
        greeting = intent.getStringExtra("greeting:");
        String senderName = intent.getStringExtra("senderName:");
        String degree = intent.getStringExtra("degree:");
        String employment = intent.getStringExtra("employment:");
        String employer = intent.getStringExtra("employer:");
        String signature = intent.getStringExtra("signature:");
        String address = intent.getStringExtra("address:");
        String city = intent.getStringExtra("city:");
        String postalCode = intent.getStringExtra("postalCode:");
        String unitNo = intent.getStringExtra("unitNo:");
        
        if(signature.equals("<No Signature>"))//punctuation is important!
            signature = "";
        else 
            signature += ",";
        
        String mailto = formatRecipientEmails(recipientEmails);
        
        EditText recipientsText = (EditText)findViewById(ca.democracylink.android.democracylink.R.id.email_recipients_text);
        EditText headerText = (EditText)findViewById(ca.democracylink.android.democracylink.R.id.message_header);
        EditText signatureText = (EditText)findViewById(ca.democracylink.android.democracylink.R.id.message_signature);
        
        recipientsText.setText(mailto);
        
        headerText.setText(greeting + " " + formattedRecipientNames);
        
        String formattedSignature = 
                signature + "\n"
                + (senderName)
                + (degree.equals("") ? "" : ("\n" + degree))
                + (employment.equals("") ? "" :  ("\n" + employment))
                + (employer.equals("") ? "" :  ("\n" + employer))
                + (address.equals("") ? "" :  ("\n" + address + " " + (unitNo.equals("") ? "" :  unitNo)))
                + (city.equals("") ? "" :  ("\n" + city))
                + (postalCode.equals("") ? "" :  ("\n" + postalCode));
        signatureText.setText(formattedSignature);
        
        TextView subjectText = (TextView)findViewById(ca.democracylink.android.democracylink.R.id.subject_text);
        subjectText.requestFocus();
    }
    
    private String formatRecipientNames(ArrayList<CharSequence> listOfNames)
    {
        String formattedRecipientNames = "";
        for(int i = 0; i < listOfNames.size(); ++i)
        {
            String name = listOfNames.get(i).toString();
            if(i+2 < listOfNames.size())
                name += ", ";
            if(i+2 == listOfNames.size())
                name += ", and ";
            formattedRecipientNames += name;
        }
        
        return formattedRecipientNames;
    }
    
    private String formatRecipientEmails(ArrayList<CharSequence> listOfEmails)
    {
        String mailto = "";//list of email addresses
        for(CharSequence recipientAddress: listOfEmails)
        {
            mailto += recipientAddress + "; ";
        }
        return mailto;
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(ca.democracylink.android.democracylink.R.menu.menu_create_email, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        
        int id = item.getItemId();
        
        if (id == ca.democracylink.android.democracylink.R.id.done_email_button) {
            
            EditText recipientsText = (EditText) findViewById(ca.democracylink.android.democracylink.R.id.email_recipients_text);
            String recipientAddresses = recipientsText.getText().toString();
            String[] recipients = recipientAddresses.split("^[,;]+$");//This regex will split on commas or semi-colons
            
            EditText subjectText = (EditText) findViewById(ca.democracylink.android.democracylink.R.id.subject_text);
            String subject = subjectText.getText().toString();
            
            EditText headerText = (EditText)findViewById(ca.democracylink.android.democracylink.R.id.message_header);
            EditText bodyText = (EditText) findViewById(ca.democracylink.android.democracylink.R.id.message_body_text);
            EditText signatureText = (EditText)findViewById(ca.democracylink.android.democracylink.R.id.message_signature);
            String body = headerText.getText().toString() + "\n\n" + bodyText.getText().toString() + "\n\n\n" + signatureText.getText().toString();
            
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"));
            emailIntent.putExtra(Intent.EXTRA_EMAIL, recipients);
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            emailIntent.putExtra(Intent.EXTRA_TEXT, body);
            startActivity(emailIntent);
            
            SharedPreferences sharedPref = getSharedPreferences(getString(ca.democracylink.android.democracylink.R.string.preference_file_key), MODE_PRIVATE);
            Utils.incrementNumEmailsSent(sharedPref);
            
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    public void showTips(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String title = getString(ca.democracylink.android.democracylink.R.string.friendly_note_title);
        String text = getString(ca.democracylink.android.democracylink.R.string.friendly_note_1) + "\n" + getString(ca.democracylink.android.democracylink.R.string.friendly_note_2);
        builder.setTitle(title);
        builder.setMessage(text);
        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create();
        builder.show();
    }

    /**
     * This is called when returning from selecting ministers.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(data == null)
            return;

        checkedMinisters = data.getParcelableArrayListExtra("ministers:");
        
        ArrayList<CharSequence> newRecipientNames = new ArrayList<>();
        ArrayList<CharSequence> newRecipientEmails = new ArrayList<>();
        //should add all current names to new list of names
        newRecipientNames.addAll(recipientNames);
        newRecipientEmails.addAll(recipientEmails);
        for(Minister minister: checkedMinisters)
        {
            newRecipientNames.add(minister.getName());
            newRecipientEmails.add(minister.getEmail());
        }
        String formattedNames = formatRecipientNames(newRecipientNames);
        EditText headerText = (EditText)findViewById(ca.democracylink.android.democracylink.R.id.message_header);
        headerText.setText(greeting + " " + formattedNames);
        
        
        //add recipient email addresses to recipient field
        
        String mailto = formatRecipientEmails(newRecipientEmails);
        EditText recipientsText = (EditText)findViewById(ca.democracylink.android.democracylink.R.id.email_recipients_text);
        recipientsText.setText(mailto);
    }

    /**
     * Called when user presses the Add Ministers button in the Create Email activity. Starts the process
     * of letting the user add ministers to the message.
     * @param view
     */
    public void addMinisters(View view)
    {
        if(!Utils.isNetworkAvailable(this)){
            Toast.makeText(this, "Please connect to the internet and try again.", Toast.LENGTH_SHORT).show();
            return;
        }
            
        
        
        final String queryString = mProvince.equals("Canada") ? "Federal_Ministers" : Utils.convertProvinceToShortProvince(mProvince)+"_Ministers";
        
        ParseQuery query = ParseQuery.getQuery(queryString);
//        query.fromLocalDatastore();
//        query.fromPin(mProvince);
        
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(ca.democracylink.android.democracylink.R.string.loading));
        progressDialog.setMessage(getString(ca.democracylink.android.democracylink.R.string.fetching_data));
        Utils.displayWaitingDialogWithTimeout(CreateEmailActivity.this, progressDialog);
        
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e != null)
                    return;
                final String title = getString(ca.democracylink.android.democracylink.R.string.ministers);
                if(list.size() == 0)
                {
                    Toast.makeText(CreateEmailActivity.this, "No Ministers found.", Toast.LENGTH_SHORT).show();
                }
                
                progressDialog.dismiss();
                CreateEmailActivity.this.gotMinisters(list, title);
            }
        });
    }

    /**
     * Once ministers are retrieved this will save them locally under the name of the province they
     * correspond to, or "Canada" if federal.
     * @param ministers
     */
    private void saveMinistersLocally(final List<ParseObject> ministers) {
        ParseObject.unpinAllInBackground(mProvince, ministers, new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                ParseObject.pinAllInBackground(mProvince, ministers);
            }
        });
        
    }

    /**
     * Ministers were fetched form either local datastore or from Parse. Now start the MinisterListActivity
     * so user can add ministers to the message.
     * @param list
     * @param title
     */
    private void gotMinisters(List<ParseObject> list, String title)
    {
        Intent intent = new Intent(this, MinisterListActivity.class);
        intent.putParcelableArrayListExtra("ministers:", convertParseObjectToMinisters(list));
        intent.putExtra("title:", title);
        intent.putParcelableArrayListExtra("checkedMinisters:", checkedMinisters);
        startActivityForResult(intent, Utils.MINI);
    }
    
    private ArrayList<Minister> convertParseObjectToMinisters(List<ParseObject> list)
    {
        ArrayList<Minister> ministers = new ArrayList<Minister>();
        Minister m;
        String name = "";
        String email = "";
        String position = "";
        boolean federal = false;
        for(ParseObject member: list)
        {
            name = member.getString("Name");
            position = member.getString("Minister_Position");
            email = member.getString("Email");

            m = new Minister(name, position, email);
            ministers.add(m);
        }

        return ministers;
    }
}
