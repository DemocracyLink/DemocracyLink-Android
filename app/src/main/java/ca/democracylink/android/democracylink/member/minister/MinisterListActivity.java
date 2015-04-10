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

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import ca.democracylink.android.democracylink.communication.CreateEmailActivity;

import java.util.ArrayList;

public class MinisterListActivity extends Activity {
    
    ArrayList<Minister> ministerList;
    MinisterAdapter adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ca.democracylink.android.democracylink.R.layout.activity_minister_list);
        
        Intent intent = getIntent();
        
        String title = intent.getStringExtra("title:");
        setTitle(title);
        
        ArrayList<Minister> ministers = intent.getParcelableArrayListExtra("ministers:");
        ministerList = ministers;
        adapter = new MinisterAdapter(this, ministerList);
        ArrayList<Minister> checkedMinisters = intent.getParcelableArrayListExtra("checkedMinisters:");
        adapter.setCheckedMinisters(checkedMinisters);
        
        final ListView ministerListView = (ListView)findViewById(ca.democracylink.android.democracylink.R.id.ministerList);
        ministerListView.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(ca.democracylink.android.democracylink.R.menu.menu_minister, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        
        if(item.getItemId() == ca.democracylink.android.democracylink.R.id.done_adding_ministers_button)
        {
            Intent intent = new Intent(this, CreateEmailActivity.class);
            intent.putParcelableArrayListExtra("ministers:", adapter.getCheckedMinisters());
            setResult(RESULT_OK, intent);
            finish();
        }
        
        
        return super.onOptionsItemSelected(item);
    }
    
    public void ministerChanged(View view)
    {
        //email to democracylinkca@gmail.com I expect
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"democracylinkca@gmail.com"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(ca.democracylink.android.democracylink.R.string.feedback_subject));
        emailIntent.putExtra(Intent.EXTRA_TEXT, getString(ca.democracylink.android.democracylink.R.string.feedback_body));
        startActivity(emailIntent);
        
    }
}