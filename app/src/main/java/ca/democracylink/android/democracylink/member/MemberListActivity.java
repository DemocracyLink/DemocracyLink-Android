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

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import ca.democracylink.android.democracylink.R;
import ca.democracylink.android.democracylink.setup.Setup4;
import ca.democracylink.android.democracylink.util.Utils;

import java.util.ArrayList;


public class MemberListActivity extends Activity implements SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    private ArrayList<Member> memberList;
    private MemberAdapter adapter;
    private String province;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member__list_);
        Intent intent = getIntent();

        //TODO: generate the title in this activity, not outside for whatever reason...
        String title = intent.getStringExtra("title");
        setTitle(title);
        
        ArrayList<Member> members = intent.getParcelableArrayListExtra("members");
        memberList = members;
        
        province = intent.getStringExtra("province");
        
        //TODO: It would be nice to find a page for the Yukon so we didn't have to hide the link
        TextView findConstituency = (TextView)findViewById(R.id.findConstituencyLink);
        if(province.equals("Yukon"))
            findConstituency.setVisibility(View.GONE);
        
        searchView = (SearchView)findViewById(R.id.searchView);
        searchView.setIconifiedByDefault(false);
        searchView.setOnCloseListener(this);
        searchView.setOnQueryTextListener(this);
        
        
        adapter = new MemberAdapter(this, members);
        
        final ListView memberList = (ListView) findViewById(R.id.member_list_view);
        memberList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                
                TextView constituencyView = (TextView)view.findViewById(R.id.member_constituency);
                TextView nameView = (TextView) view.findViewById(R.id.member_name);
                
                String constituency = constituencyView.getText().toString();
                String name = nameView.getText().toString();
                
                MemberAdapter currentAdapter = (MemberAdapter) memberList.getAdapter();
                Intent intent = new Intent(view.getContext(), Setup4.class);
                Member member = currentAdapter.getMemberFromConstituencyAndName(constituency, name);
                
                intent.putExtra("name", member.getName());
                intent.putExtra("email", member.getEmail());
                intent.putExtra("phone", member.getPhone());
                intent.putExtra("constituency", member.getConstituency());
                setResult(0, intent);
                finish();
            }
        });
        memberList.setAdapter(adapter);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.member__list_, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (item.getItemId()){
            case R.id.sort_button:
                adapter.changeSorting();
                if(adapter.isSortedByName())
                    item.setTitle("Sort By Constituency");
                else
                    item.setTitle("Sort By Name");
                
                return true;
            case android.R.id.home: //if user presses the up/back button on the action bar
                onBackPressed();//just cheat and do this since it accomplishes what we really want
                return true;
        }
        
        return super.onOptionsItemSelected(item);
    }

    /**
     * Called when user presses on the "don't know your constituency" text. Should open
     * a link the the relevant url to help them find their constituency.
     * @param view
     */
    public void findConstituency(View view)
    {
        String url = Utils.getURLFromProvince(province);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    @Override
    public boolean onClose() {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        MemberListActivity.this.adapter.getFilter().filter(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        MemberListActivity.this.adapter.getFilter().filter(newText);
        return false;
    }
}
