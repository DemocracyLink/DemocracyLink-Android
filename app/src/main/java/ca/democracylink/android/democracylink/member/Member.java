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

package ca.democracylink.android.democracylink.member;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class used to store Member data to pass into the list view to select your local representatives.
 * Created by ben on 13/12/14.
 */
public class Member implements Parcelable {

    private String name;
    private String constituency;
    private String email;
    private String phone;
    private String province;
    private boolean federal;

    public Member(String name, String constituency, String email, String phone, String province, boolean federal)
    {
        this.name = name;
        this.constituency = constituency;
        this.email = email;
        this.phone = phone;
        this.province = province;
        this.federal = federal;
    }

    public Member(Parcel in)
    {
        String[] data = new String[5];
        in.readStringArray(data);
        boolean[] bool_data = new boolean[1];
        in.readBooleanArray(bool_data);

        this.name           =   data[0];
        this.constituency   =   data[1];
        this.email          =   data[2];
        this.phone          =   data[3];
        this.province       =   data[4];
        this.federal        =   bool_data[0];
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeStringArray(new String[]{
                this.name,
                this.constituency,
                this.email,
                this.phone,
                this.province
        });
        dest.writeBooleanArray(new boolean[]{
                this.federal
        });

    }

    public static final Parcelable.Creator<Member> CREATOR = new Parcelable.Creator<Member>(){
        public Member createFromParcel(Parcel in)
        {
            return new Member(in);
        }

        @Override
        public Member[] newArray(int size) {
            return new Member[size];
        }
    };


    //Only Getters and Setters below here
    public String getName() {
        return (name == null ? "": name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getConstituency() {
        return (constituency == null ? "": constituency);
    }

    public void setConstituency(String constituency) {
        this.constituency = constituency;
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

    public String getProvince() {
        return (province == null ? "": province);
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public boolean isFederal() {
        return federal;
    }

    public void setFederal(boolean federal) {
        this.federal = federal;
    }
}
