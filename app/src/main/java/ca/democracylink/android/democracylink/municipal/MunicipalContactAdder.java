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

package ca.democracylink.android.democracylink.municipal;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import ca.democracylink.android.democracylink.R;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MunicipalContactAdder extends Activity {
    
    private MunicipalContactAdapter adapter;
    private ArrayList<Contact> contacts;
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_municipal_contact_adder);
        
        sharedPref = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
        String contactString = sharedPref.getString("saved_muni_contacts", "");
        if(!contactString.isEmpty()){
            contacts = Contact.convertStringToContacts(contactString);
        } else{
            contacts = new ArrayList<>();
        }
        
        adapter = new MunicipalContactAdapter(this, contacts, false);
        
        ListView listView = (ListView)findViewById(R.id.municipal_listview);
        listView.setAdapter(adapter);
        registerForContextMenu(listView);
    }
    
    private Contact selectedContact;

    /**
     * Called when we create the menu that appears when holding down on a contact for a while.
     * @param menu
     * @param v
     * @param menuInfo
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        if(v.getId() == R.id.municipal_listview) {
            
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            
            selectedContact = (Contact)adapter.getItem(info.position);
            
            super.onCreateContextMenu(menu, v, menuInfo);
            menu.setHeaderTitle(getString(R.string.options));
            menu.add(0, 0, 0, getString(R.string.delete_contact));
            menu.add(0, 1, 1, getString(R.string.edit_contact));
        }
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        
        int itemId = item.getItemId();
        switch (itemId)
        {
            
            case 0:     //delete contact
                adapter.remove(selectedContact);

                break;
            case 1:     //edit contact
                EditText positionText = (EditText)findViewById(R.id.muni_contact_position);
                EditText nameText = (EditText)findViewById(R.id.muni_contact_name);
                EditText emailText = (EditText)findViewById(R.id.muni_contact_email);
                EditText phoneText = (EditText)findViewById(R.id.muni_contact_phone);
                
                positionText.setText(selectedContact.getPosition());
                nameText.setText(selectedContact.getName());
                emailText.setText(selectedContact.getEmail());
                phoneText.setText(selectedContact.getPhone());
                
                Button saveContactButton = (Button)findViewById(R.id.saveContactButton);
                saveContactButton.setText(getString(R.string.edit_contact));
                break;
        }
        
        return true;
    }
    
    public void saveContactButtonPressed(View view)
    {
        EditText positionText = (EditText)findViewById(R.id.muni_contact_position);
        EditText nameText = (EditText)findViewById(R.id.muni_contact_name);
        EditText emailText = (EditText)findViewById(R.id.muni_contact_email);
        EditText phoneText = (EditText)findViewById(R.id.muni_contact_phone);
        
        String name = nameText.getText().toString();
        String position = positionText.getText().toString();
        String phone = phoneText.getText().toString();
        String email = emailText.getText().toString();
        
        //don't do anything if fields are empty
        if(name.equals("") || position.equals(""))
        {
            Toast.makeText(this, "Contacts must have a name and position.", Toast.LENGTH_LONG).show();
            return;
        }
        
        positionText.setText("");
        nameText.setText("");
        emailText.setText("");
        phoneText.setText("");
        
        //if we are editing a contact, modify the selected contact. Otherwise, add a new contact.
        Button saveContactButton = (Button)findViewById(R.id.saveContactButton);
        if(saveContactButton.getText().toString().equals("Edit Contact") && selectedContact != null)
        {
            
            selectedContact.setName(name);
            selectedContact.setPosition(position);
            selectedContact.setPhone(phone);
            selectedContact.setEmail(email);
            adapter.notifyDataSetChanged();
            
            saveContactButton.setText("Save Contact");
        }
        else if(saveContactButton.getText().toString().equals("Save Contact")) {

            //save locally for later use and add to list adapter for now
            Contact contact = new Contact(name, position, phone, email, false);
            adapter.add(contact);
            adapter.notifyDataSetChanged();
            
            //record how many municipal contacts there are now
            final SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            int numContacts = sharedPref.getInt("numMunicipalContacts", adapter.getCount()-1);
            editor.putInt("numMunicipalContacts", numContacts+1);
            
            editor.apply();
        }
        saveContacts();
    }
    
    private void saveContacts(){
        final SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        
        String toSave = Contact.convertContactsToString(contacts);
        editor.putString("saved_muni_contacts", toSave);
        editor.apply();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_municipal_member_adder, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onBackPressed(){
        
        //get a list of all contacts currently saved
        final ArrayList<Contact> contacts = new ArrayList<>();
        for(int i = 0; i < adapter.getCount(); ++i){
            contacts.add((Contact)adapter.getItem(i));
        }
        
        
        SharedPreferences.Editor editor = sharedPref.edit();
        String toSave = Contact.convertContactsToString(contacts);
        editor.putString("saved_muni_contacts", toSave);
        editor.apply();

        Intent intent = new Intent();
        intent.putExtra("newNumberOfMembers", adapter.getCount());
        intent.putParcelableArrayListExtra("muni_contacts", contacts);
        MunicipalContactAdder.this.setResult(RESULT_CANCELED, intent);
        MunicipalContactAdder.this.finish();

    }
}
