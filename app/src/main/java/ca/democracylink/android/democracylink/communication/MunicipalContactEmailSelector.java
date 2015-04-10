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
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import ca.democracylink.android.democracylink.R;
import ca.democracylink.android.democracylink.municipal.Contact;
import ca.democracylink.android.democracylink.municipal.MunicipalContactAdapter;

import java.util.ArrayList;

public class MunicipalContactEmailSelector extends Activity {

    private MunicipalContactAdapter adapter;
    private ArrayList<Contact> contacts;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ca.democracylink.android.democracylink.R.layout.activity_municipal_contact_email_selector);
        
        Intent intent = getIntent();
        contacts = intent.getParcelableArrayListExtra("contacts");
        ListView contactListView = (ListView)findViewById(ca.democracylink.android.democracylink.R.id.muni_email_selector_list);

        adapter = new MunicipalContactAdapter(this, contacts, true);
        contactListView.setAdapter(adapter);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(ca.democracylink.android.democracylink.R.menu.menu_municipal_contact_email_selector, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        
        SharedPreferences sharedPref = getSharedPreferences(getString(ca.democracylink.android.democracylink.R.string.preference_file_key), MODE_PRIVATE);
        
        int id = item.getItemId();
        
        if(id == ca.democracylink.android.democracylink.R.id.done_selecting_button)
        {
            //send email to selected contacts
            Intent intent = new Intent(this, CreateEmailActivity.class);
            ArrayList<CharSequence> emailDestinations = new ArrayList<>();
            ArrayList<CharSequence> recipientNames = new ArrayList<>();
            for(Contact contact: contacts)
            {
                if(contact.isSelected()) {
                    emailDestinations.add(contact.getEmail());
                    recipientNames.add(contact.getName());
                }
            }
            intent.putCharSequenceArrayListExtra("sendto:", emailDestinations);
            intent.putCharSequenceArrayListExtra("recipientNames:", recipientNames);
            
            intent.putExtra("greeting:", getString(ca.democracylink.android.democracylink.R.string.dear));
            intent.putExtra("senderName:", sharedPref.getString(getString(ca.democracylink.android.democracylink.R.string.first_name), "") + " " + sharedPref.getString(getString(ca.democracylink.android.democracylink.R.string.last_name), ""));
            intent.putExtra("degree:", sharedPref.getString(getString(ca.democracylink.android.democracylink.R.string.degree), ""));
            intent.putExtra("employment:", sharedPref.getString(getString(ca.democracylink.android.democracylink.R.string.employment), ""));
            intent.putExtra("employer:", sharedPref.getString(getString(ca.democracylink.android.democracylink.R.string.employer), ""));
            intent.putExtra("signature:", sharedPref.getString(getString(ca.democracylink.android.democracylink.R.string.email_signature), "Sincerely"));
            intent.putExtra("address:", sharedPref.getString(getString(ca.democracylink.android.democracylink.R.string.street_address), ""));
            intent.putExtra("city:", sharedPref.getString(getString(ca.democracylink.android.democracylink.R.string.city), ""));
            intent.putExtra("postalCode:", sharedPref.getString(getString(ca.democracylink.android.democracylink.R.string.postal_code), ""));
            intent.putExtra("unitNo:", sharedPref.getString(getString(ca.democracylink.android.democracylink.R.string.unit_no), ""));

            intent.putExtra("province:", sharedPref.getString(getString(R.string.province), "British Columbia"));
            
            intent.putExtra("municipal:", true);
            
            startActivity(intent);
        }
        
        return super.onOptionsItemSelected(item);
    }
}