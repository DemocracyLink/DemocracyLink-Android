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
import android.os.Parcel;
import android.os.Parcelable;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ben on 19/12/14.
 */
public class Contact implements Parcelable {
    
    private String name;
    private String position;
    private String phone;
    private String email;
    private boolean selected;
    
    public Contact(String name, String position, String phone, String email, boolean selected)
    {
        this.name = name;
        this.position = position;
        this.phone = phone;
        this.email = email;
        this.selected = selected;
    }
    
    public Contact(Parcel in)
    {
        String[] data = new String[4];
        in.readStringArray(data);
        boolean[] boolData = new boolean[1];
        in.readBooleanArray(boolData);

        this.name            =   data[0];
        this.position        =   data[1];
        this.phone           =   data[2];
        this.email           =   data[3];
        this.selected        =   boolData[0];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{
                this.name,
                this.position,
                this.phone,
                this.email,
        });
        dest.writeBooleanArray(new boolean[]{
                this.selected
        });
    }

    public static final Parcelable.Creator<Contact> CREATOR = new Parcelable.Creator<Contact>(){
        public Contact createFromParcel(Parcel in)
        {
            return new Contact(in);
        }
        
        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };

    public void setName(String name) {
        this.name = name;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public boolean isSelected() {
        return selected;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Contact contact = (Contact) o;

        if (email != null ? !email.equals(contact.email) : contact.email != null) return false;
        if (!name.equals(contact.name)) return false;
        if (phone != null ? !phone.equals(contact.phone) : contact.phone != null) return false;
        if (!position.equals(contact.position)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + position.hashCode();
        result = 31 * result + (phone != null ? phone.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        return result;
    }

    /**
     * This converts the contact to a formatted string. This string is not used for display but 
     * rather to reconstruct the municipal contact as we will save this in SharedPreferences.
     * Give a string with attribute values surround by double quotation marks and a space between
     * each attribute.
     * @return
     */
    @Override
    public String toString()
    {
        String result = "";
        result += "[" + getName()  + "]";
        result += "[" + getPosition()  + "]";
        result += "[" + (getPhone().isEmpty()? "": getPhone())  + "]";
        result += "[" + (getEmail().isEmpty()? "": getEmail())  + "]";
        return result;
    }
    
    public static ArrayList<Contact> convertStringToContacts(String people){
        
        String[] split = people.split("\\*");
        ArrayList<Contact> contacts = new ArrayList<>();
        Scanner scan = new Scanner(people);
        String nextLine;
        Contact newContact;
        for(String person: split){
//            nextLine = scan.nextLine();
//            if(nextLine.isEmpty())
//                break;
            String[] attributes = {"","","",""};
            
            //this regexp should find all text inside square brackets
            Pattern pattern = Pattern.compile("\\[(.*?)\\]");
            Matcher matcher = pattern.matcher(person);
            int count = -1;
            while(matcher.find() && ++count < 4){
                String attr = matcher.group(1);
                attributes[count] = attr;
            }
            
            newContact = new Contact(attributes[0], attributes[1], attributes[2], attributes[3], false);
            contacts.add(newContact);
        }
        return contacts;
    }
    
    public static String convertContactsToString(ArrayList<Contact> toSave){
        ArrayList<Contact> contacts = new ArrayList<>(toSave);
        
        
        if(contacts.isEmpty())
            return "";
        String people = contacts.remove(0).toString();
        for(Contact contact: contacts){
            people += "*" + contact;
        }
        return people;
    }
}
