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

package ca.democracylink.android.democracylink.member.minister;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ben on 11/01/15.
 */
public class Minister implements Parcelable
{
    private String name;
    private String position;
    private String email;
    private String phone;
    private String constituency;
    private boolean selected = false;//for use in adding ministers, not when fetching them

    public Minister(String name, String position, String email)
    {
        this.name = name;
        this.position = position;
        this.email = email;
        this.phone = "";
        this.constituency = "";
    }
    
    public Minister(Parcel in)
    {
        String[] data = new String[5];
        in.readStringArray(data);
    
        this.name           =   data[0];
        this.position       =   data[1];
        this.email          =   data[2];
        this.phone          =   data[3];
        this.constituency   =   data[4];
        
        boolean[] moreData = new boolean[1];
        in.readBooleanArray(moreData);
        this.selected = moreData[0];
    }

    public String getName() {
        return (name == null ? "": name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return (position == null ? "": position);
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getEmail() {
        return (email == null ? "": email);
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return (phone == null ? "": phone);
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getConstituency() {
        return (constituency == null ? "": constituency);
    }

    public void setConstituency(String constituency) {
        this.constituency = constituency;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
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
                this.email,
                this.phone,
                this.constituency
        });
        dest.writeBooleanArray(new boolean[]{
                this.selected
        });
        
    }

    public static final Parcelable.Creator<Minister> CREATOR = new Parcelable.Creator<Minister>(){
        public Minister createFromParcel(Parcel in)
        {
            return new Minister(in);
        }
        
        @Override
        public Minister[] newArray(int size) {
            return new Minister[size];
        }
    };
    
    @Override
    public boolean equals(Object o)
    {
        if(!(o instanceof Minister))
            return false;
        
        Minister other = (Minister) o;
        
        return other.getPosition().equals(this.getPosition()) && 
                other.getConstituency().equals(this.getConstituency()) &&
                other.getPhone().equals(this.getPhone()) &&
                other.getName().equals(this.getName());
    }
    
    @Override
    public String toString()
    {
        return this.getName() + " - " + this.getPosition();
    }
}
