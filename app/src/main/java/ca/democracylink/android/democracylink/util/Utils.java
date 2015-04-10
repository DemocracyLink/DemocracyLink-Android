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

package ca.democracylink.android.democracylink.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.widget.Spinner;
import android.widget.Toast;

import ca.democracylink.android.democracylink.municipal.Contact;
import com.parse.ParseObject;

import java.util.ArrayList;

/**
 * Created by ben on 07/12/14.
 */
public class Utils {

    /*
    These constants are used when selecting provincial and federal members in Setup4 and MemberListActivity
     */
    public static final int FED_MEMB = 0;
    public static final int PROV_MEMB = 1;
    public static final int MUNI_MEMB = 2;
    public static final int MINI = 3;
    /**
     * Finds the index of the given value in the given spinner, or 0 if value is not in spinner.
     * @param spinner
     * @param myString
     * @return
     */
    public static int getIndex(Spinner spinner, String myString)
    {
        int index = 0;

        for (int i=0;i<spinner.getCount();i++)
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                index = i;
                break;
            }
        return index;
    }

    /**
     * Takes a province name and returns the short abbreviation for that province. Used to construct
     * the name of the Parse objects for each province. Parse stores MPs as objects of the name
     * XX_Members where XX is the province abbreviation.
     * Optimized really poorly for French.. Oh well.
     * @param province
     * @return province abbreviation, BC if given string is not a correctly spelled province.
     */
    public static String convertProvinceToShortProvince(String province)
    {
        String short_province = "BC";

        if(province.equals("Alberta") || province.equals("Alberta"))
        {
            short_province = "AB";
        }
        else if(province.equals("British Columbia") || province.equals("Colombie-Britannique"))
        {
            short_province = "BC";
        }
        else if(province.equals("Manitoba") || province.equals("Manitoba"))
        {
            short_province = "MB";
        }
        else if(province.equals("New Brunswick") || province.equals("Nouveau-Brunswick"))
        {
            short_province = "NB";
        }
        else if(province.equals("Newfoundland and Labrador") || province.equals("Terre-Neuve-et-Labrador"))
        {
            short_province = "NL";
        }
        else if(province.equals("Northwest Territories") || province.equals("Territoires du Nord-Ouest"))
        {
            short_province = "NT";
        }
        else if(province.equals("Nova Scotia") || province.equals("Nouvelle-Écosse"))
        {
            short_province = "NS";
        }
        else if(province.equals("Nunavut") || province.equals("Nunavut"))
        {
            short_province = "NU";
        }
        else if(province.equals("Ontario") || province.equals("Ontario"))
        {
            short_province = "ON";
        }
        else if(province.equals("Prince Edward Island") || province.equals("Île-du-Prince-Édouard"))
        {
            short_province = "PE";
        }
        else if(province.equals("Quebec") || province.equals("Québec"))
        {
            short_province = "QC";
        }
        else if(province.equals("Saskatchewan") || province.equals("Saskatchewan"))
        {
            short_province = "SK";
        }
        else if(province.equals("Yukon") || province.equals("Yukon"))
        {
            short_province = "YK";
        }
        else if(province.equals("Canada") || province.equals("Canada"))
        {
            short_province = "CA";
        }

        return short_province;
    }

    /**
     * Gives a url to a site where a user can figure out what constituency they're in.
     * @param province
     * @return
     */
    public static String getURLFromProvince(String province)
    {
        if(province.equals("Alberta"))
        {
            return "http://streetkey.elections.ab.ca/";
        }
        else if(province.equals("British Columbia"))
        {
            return "https://www.leg.bc.ca/mla/3-1-1.htm";
        }
        else if(province.equals("Manitoba"))
        {
            return "http://www.electionsmanitoba.ca/en/voting/MLA";
        }
        else if(province.equals("New Brunswick"))
        {
            return "http://www.electionsnb.ca/content/enb/en/maps/municipal-regions.html";
        }
        else if(province.equals("Newfoundland and Labrador"))
        {
            return "http://www.elections.gov.nl.ca/elections/Voters/index.html";
        }
        else if(province.equals("Northwest Territories"))
        {
            return "http://www.assembly.gov.nt.ca/visitors/maps/constituency-map";
        }
        else if(province.equals("Nova Scotia"))
        {
            return "http://enstools.gov.ns.ca/edinfo2012/";
        }
        else if(province.equals("Nunavut"))
        {
            return "ttp://www.elections.nu.ca/apps/lookup/ConstituencySearch.aspx";
        }
        else if(province.equals("Ontario"))
        {
            return "http://fyed.elections.on.ca/fyed/en/form_page_en.jsp";
        }
        else if(province.equals("Prince Edward Island"))
        {
            return "http://www.electionspei.ca/provincial/districts/find/index.php";
        }
        else if(province.equals("Quebec"))
        {
            return "http://www.electionsquebec.qc.ca/francais/provincial/carte-electorale/trouvez-votre-circonscription-2011.php";
        }
        else if(province.equals("Saskatchewan"))
        {
            return "http://www.elections.sk.ca/voters/find-my-constituency/";
        }
        else if(province.equals("Yukon"))
        {
            //TODO: We don't have a Yukon page yet so for now just give the federal one and hide the link
            return "http://www.elections.ca/scripts/vis/FindED?L=e&PAGEID=20";
        }
        else if(province.equals("Canada"))
        {
            return "http://www.elections.ca/scripts/vis/FindED?L=e&PAGEID=20";
        }
        
        //If nothing found just return the default federal page
        return "http://www.elections.ca/scripts/vis/FindED?L=e&PAGEID=20";
    }

    public static void incrementNumPhoneCalls(SharedPreferences sharedPref) {
        
        SharedPreferences.Editor editor = sharedPref.edit();
        int numCalls = sharedPref.getInt("numberPhoneCalls", 0);
        numCalls++;
        editor.putInt("numberPhoneCalls", numCalls);
        editor.commit();
    }
    public static void incrementNumEmailsSent(SharedPreferences sharedPref)
    {
        SharedPreferences.Editor editor = sharedPref.edit();
        int numEmails = sharedPref.getInt("numberEmails", 0);
        numEmails++;
        editor.putInt("numberEmails", numEmails);
        editor.commit();
    }

    /**
     * Determines if the device is connected to a network. Technically does not check for an internet
     * connection, only the network.
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context){
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Starts a ProgressDialog that will end after 15 seconds if it has not yet been dismissed,
     * Dialog in question will be dismissed outside of here if either the user cancels (taps away)
     * or 
     */
    public static void displayWaitingDialogWithTimeout(final Context context, final ProgressDialog dialog)
    {
        dialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(dialog.isShowing()) {
                    dialog.dismiss();
                    Toast.makeText(context, "Request Timed out. Check Network Connection.", Toast.LENGTH_SHORT).show();
                }
            }
        }, 15000);
    }
    
}
