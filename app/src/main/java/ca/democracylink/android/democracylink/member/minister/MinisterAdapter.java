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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by ben on 11/01/15.
 */
public class MinisterAdapter extends BaseAdapter {
    private final Context context;
    private ArrayList<Minister> ministers;
    private ArrayList<Minister> checkedMinisters;

    public MinisterAdapter(Context context, ArrayList<Minister> ministers) {
        this.ministers = ministers;
        Collections.sort(this.ministers, new MinisterNameComparator());
        this.context = context;
        this.checkedMinisters = new ArrayList<>();
    }

    public ArrayList<Minister> getCheckedMinisters() {
        
        this.checkedMinisters = new ArrayList<>();
        for(Minister minister: ministers)
        {
            if(minister.isSelected())
                checkedMinisters.add(minister);
        }
        
        
        return this.checkedMinisters;
    }

    @Override
    public int getCount() {
        return ministers.size();
    }

    @Override
    public Object getItem(int position) {
        return ministers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(ca.democracylink.android.democracylink.R.layout.minister_list_item, parent, false);
        }
        
        final Minister current = ministers.get(position);
        
        TextView minister_name = (TextView) convertView.findViewById(ca.democracylink.android.democracylink.R.id.minister_name);
        TextView minister_position = (TextView) convertView.findViewById(ca.democracylink.android.democracylink.R.id.minister_position);
        
        minister_name.setText(current.getName());
        minister_position.setText(current.getPosition());
        
        final CheckBox checkbox = (CheckBox) convertView.findViewById(ca.democracylink.android.democracylink.R.id.minister_checkBox);
        
        if (checkedMinisters.contains(current)) {
            current.setSelected(true);
            checkbox.setChecked(true);
        } else//if checked ministers doesn't contain current minister
        {
            current.setSelected(false);
            checkbox.setChecked(false);
            //if not checked we don't need to do anything
        }

        checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current.setSelected(!current.isSelected());
                if(current.isSelected())
                    checkedMinisters.add(current);
                else
                    checkedMinisters.remove(current);
            }
        });

        return convertView;
    }

    public void setCheckedMinisters(ArrayList<Minister> checkedMinisters) {
        this.checkedMinisters = checkedMinisters;
    }

    private class MinisterNameComparator implements Comparator<Minister> {

        @Override
        public int compare(Minister lhs, Minister rhs) {
            return lhs.getName().compareTo(rhs.getName());
        }
    }
}
