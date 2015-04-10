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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import ca.democracylink.android.democracylink.R;
import ca.democracylink.android.democracylink.municipal.Contact;
import ca.democracylink.android.democracylink.municipal.MunicipalContactAdapter;
import ca.democracylink.android.democracylink.util.Utils;

import java.util.ArrayList;

public class MunicipalContactPhoneSelector extends Activity {
    
    MunicipalContactAdapter adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_municipal_contact_phone_selector);
        
        Intent intent = getIntent();
        final ArrayList<Contact> contacts = intent.getParcelableArrayListExtra("contacts");
        
        adapter = new MunicipalContactAdapter(this, contacts, false);
        ListView contactListView = (ListView) findViewById(R.id.contact_phone_listview);
        contactListView.setAdapter(adapter);
        
        contactListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Contact contact = contacts.get(position);
                
                String phoneNumber = contact.getPhone();
                
                if(!PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber))
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MunicipalContactPhoneSelector.this);
                    String title = getString(R.string.bad_phone_number_title);
                    String text = getString(R.string.bad_municipal_phone_text);
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
                else
                {
                    SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
                    Utils.incrementNumPhoneCalls(sharedPref);
                    
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + phoneNumber));
                    startActivity(callIntent);
                }
            }
        });
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_municipal_contact_phone_selector, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        

        return super.onOptionsItemSelected(item);
    }
}
