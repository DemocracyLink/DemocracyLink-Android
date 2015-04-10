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

package ca.democracylink.android.democracylink;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


public class About extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.about, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        
        if(id == R.id.statistics_button)
        {
            SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
            
            
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            String title = (String)getString(R.string.statistics);
            String text = ((String)getString(R.string.stats_phone)) + sharedPref.getInt("numberPhoneCalls", 0);
            text += "\n" + ((String)getString(R.string.stats_email)) + sharedPref.getInt("numberEmails", 0);
            builder.setTitle(title);
            builder.setMessage(text);
            builder.setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.create();
            builder.show();
            
        }

        return super.onOptionsItemSelected(item);
    }

    public void showEmpoweredCitizensDialog(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String title = (String)getString(R.string.empowered_citizens);
        String text = ((String)getString(R.string.empowered_citizens_text));
        builder.setTitle(title);
        builder.setMessage(text);
        builder.setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create();
        builder.show();
    }

    public void showRepresentationDialog(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String title = (String)getString(R.string.representation);
        String text = ((String)getString(R.string.representation_text));
        builder.setTitle(title);
        builder.setMessage(text);
        builder.setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create();
        builder.show();
    }

    public void showDiplomaticNonPartisanDialog(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String title = (String)getString(R.string.non_partisan_communication);
        String text = ((String)getString(R.string.non_partisan_communication_text));
        builder.setTitle(title);
        builder.setMessage(text);
        builder.setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create();
        builder.show();
    }

    public void showCanadianDemocracyDialog(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String title = (String)getString(R.string.canadian_democracy);
        String text = ((String)getString(R.string.canadian_democracy_text));
        builder.setTitle(title);
        builder.setMessage(text);
        builder.setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        
        builder.setNeutralButton(getString(R.string.more_info), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String url = "http://www.parl.gc.ca/about/parliament/publications/democracyinaction/democracy-e.asp";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });
        builder.create();
        builder.show();
    }

    public void showSpecialSupportersDialog(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String title = (String)getString(R.string.special_supporters);
        String text = ((String)getString(R.string.special_supporters_text));
        builder.setTitle(title);
        builder.setMessage(text);
        builder.setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create();
        builder.show();
    }

    public void sendFeedbackButtonPressed(View view)
    {
        //email to democracylinkca@gmail.com I expect
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"democracylinkca@gmail.com"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_subject));
        emailIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.feedback_body));
        startActivity(emailIntent);
    }

    public void donateButtonPressed(View view)
    {
        //load the webpage
        String paypal = "https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=UJ8X2MXGWL9DG&lc=CA&item_name=Democracy%20Link&currency_code=CAD&bn=PP%2dDonationsBF%3abtn_donateCC_LG%2egif%3aNonHosted";
        String site = "http://www.democracylink.ca";
        try {
            Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(site));
            startActivity(myIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No application can handle this request."
                    + " Please install a webbrowser", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
