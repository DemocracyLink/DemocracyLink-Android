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

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Used to populate the listview with members for selection.
 * By default members are sorted by constituency.
 * Created by ben on 13/12/14.
 */
public class MemberAdapter extends BaseAdapter implements Filterable {

    private ArrayList<Member> members;
    private ArrayList<Member> allMembers;
    private Context context;
    private boolean sortedByName;
    private String currentFilterText;//text currently written in the search field after being converted to lowercase
    
    public MemberAdapter(Context context, ArrayList<Member> members)
    {
        this.members = members;
        this.allMembers = members;
        this.context = context;
        this.sortedByName = true;
        currentFilterText = "";
        changeSorting();
    }
    
    /**
     * Sorts members lists by either name or constituency (whichever it is not currently sorted by).
     */
    public void changeSorting()
    {
        if(sortedByName)//then we want to sort by constiuency
        {
            Collections.sort(allMembers, new MemberConstituencyComparator());
            Collections.sort(members, new MemberConstituencyComparator());
            this.getFilter().filter(currentFilterText);
        }
        else 
        {
            Collections.sort(allMembers, new MemberNameComparator());
            Collections.sort(members, new MemberNameComparator());
            this.getFilter().filter(currentFilterText);
        }
        sortedByName = !sortedByName;
        notifyDataSetChanged();
    }
    
    public boolean isSortedByName()
    {
        return sortedByName;
    }
    
    /**
     * Returns member with the given name and constituency. If that's not a unique enough key or
     * there's no such member then we're screwed but still give some attempt to fail gracefully by
     * returning a member with no data.
     * @param constituency
     * @return
     */
    public Member getMemberFromConstituencyAndName(String constituency, String name)
    {
        for(Member member: members)
        {
            if(member.getConstituency().equals(constituency) && member.getName().equals(name))
                return member;
        }
        
        return new Member("", "","", "", "", false);
    }
    
    @Override
    public int getCount() {
        return members.size();
    }
    
    @Override
    public Member getItem(int position) {
        return members.get(position);
    }
    
    @Override
    public long getItemId(int position) {
        return position;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(ca.democracylink.android.democracylink.R.layout.member_list_item, parent, false);
        }
        
        TextView member_name = (TextView)convertView.findViewById(ca.democracylink.android.democracylink.R.id.member_name);
        TextView constituency = (TextView)convertView.findViewById(ca.democracylink.android.democracylink.R.id.member_constituency);
        
        member_name.setText(members.get(position).getName());
        constituency.setText((members.get(position)).getConstituency());
        
        return convertView;
    }
    
    @Override
    public Filter getFilter() {
        
        return filter;
    }
    
    private class MemberNameComparator implements Comparator<Member>
    {
        @Override
        public int compare(Member lhs, Member rhs) {
        
            return lhs.getName().compareTo(rhs.getName());
        }
    }
    
    private class MemberConstituencyComparator implements Comparator<Member>
    {
        @Override
        public int compare(Member lhs, Member rhs) {
            
            return lhs.getConstituency().compareTo(rhs.getConstituency());
        }
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence filterText) {
            FilterResults results = new FilterResults();
            ArrayList<Member> filteredMembers = (ArrayList)allMembers.clone();

            String constraint = filterText.toString().toLowerCase();
            currentFilterText = constraint;
            constraint = constraint.trim();
            if(constraint.equals(""))
                filteredMembers.addAll(allMembers);
            else
                for(int i = 0; i < allMembers.size(); ++i)
                {
                    Member member = allMembers.get(i);
                    //search each word of each members name/constituency
                    String[] names = member.getName().split("[-\\s]");
                    String[] constituencies = member.getConstituency().split("[-\\s]");
                    if(sortedByName)
                    {
                        boolean good = false;
                        for(String name: names) {
                            if (name.toLowerCase().startsWith(constraint)) {
                                good = true;
                                break;
                            }
                        }
                        if(!good){
                            for(String constituency: constituencies){
                                if(constituency.toLowerCase().startsWith(constraint)) {
                                    good = true;
                                    break;
                                }
                            }
                        }
                        if(!good)
                            filteredMembers.remove(member);
                    }
                    else {
                        boolean good = false;
                        for(String constituency: constituencies){
                            if(constituency.toLowerCase().startsWith(constraint)) {
                                good = true;
                                break;
                            }
                        }
                        if(!good){
                            for(String name: names) {
                                if (name.toLowerCase().startsWith(constraint)) {
                                    good = true;
                                    break;
                                }
                            }
                        }
                        if(!good)
                            filteredMembers.remove(member);
                    }
                }
            results.count = filteredMembers.size();
            results.values = filteredMembers;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            members = (ArrayList<Member>)results.values;
            notifyDataSetChanged();
        }
    };
}
