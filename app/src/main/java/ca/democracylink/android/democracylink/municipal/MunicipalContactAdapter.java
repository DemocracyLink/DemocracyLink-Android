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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import ca.democracylink.android.democracylink.R;

import java.util.ArrayList;

/**
 * Used in the MunicipalContactAdder activity to display existing municipal contacts and also in the
 * municipalContactEmailSelector to select contacts.
 * Created by ben on 19/12/14.
 */
public class MunicipalContactAdapter extends BaseAdapter {
    
    private ArrayList<Contact> contacts;
    private Context mContext;
    private boolean selectable;

    /**
     * 
     * @param context
     * @param contacts
     * @param selectable This is true if the list has a checkbox beside each contact indicating which
     *                   contacts we wish to send an email to, and false otherwise.
     */
    public MunicipalContactAdapter(Context context, ArrayList<Contact> contacts, boolean selectable)
    {
        this.contacts = contacts;
        this.mContext = context;
        this.selectable = selectable;
    }
    
    public void add(Contact contact)
    {
        contacts.add(contact);
        this.notifyDataSetChanged();
    }
    
    @Override
    public int getCount() {
        return contacts.size();
    }
    
    @Override
    public Object getItem(int position) {
        return contacts.get(position);
    }
    
    @Override
    public long getItemId(int position) {
        return position;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        
        if(convertView == null) {
            
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if(!selectable)
                convertView = inflater.inflate(R.layout.municipal_list_item, parent, false);
            else
                convertView = inflater.inflate(R.layout.municipal_multiselector_list_item, parent, false);
        }
        final Contact current = contacts.get(position);
        
        TextView name_text = (TextView) convertView.findViewById(R.id.contact_name);
        TextView position_text = (TextView) convertView.findViewById(R.id.contact_position);
        TextView phone_text = (TextView) convertView.findViewById(R.id.contact_phone);
        TextView email_text = (TextView) convertView.findViewById(R.id.contact_email);
        
        name_text.setText(current.getName());
        position_text.setText(current.getPosition());
        phone_text.setText(current.getPhone());
        email_text.setText(current.getEmail());
        
        //if item is selectable, check if it should be selected and set up the click listener
        if(selectable) {
            CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.contact_selected_checkbox);
            checkBox.setChecked(current.isSelected());
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    
                    CheckBox theBox = (CheckBox)v;
                    current.setSelected(theBox.isChecked());
                }
            });
        }
        
        return convertView;
    }

    public void remove(Contact selectedContact)
    {
        contacts.remove(selectedContact);
        notifyDataSetChanged();
    }
}
