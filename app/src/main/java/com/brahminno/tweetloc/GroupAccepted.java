package com.brahminno.tweetloc;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Shushmit Yadav on 18-07-2015.
 * Brahmastra Innovations Pvt. Ltd.
 * This activity class will call when user click on Group Accept button or already accepted or admin of that group......
 */
public class GroupAccepted extends ActionBarActivity {
    SQLiteDatabase mydb;
    private String groupName;
    private String userMobileNumber;
    private String groupAdminMobileNumber;
    ArrayList<ContactNameWithNumber> contactNameWithNumberArrayList;
    AlarmManager alarmManager;
    PendingIntent pendingIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(groupName);
        setContentView(R.layout.already_group_accepted);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        groupName = bundle.getString("Group Name");
        groupAdminMobileNumber = bundle.getString("GroupAdminMobNo");
        //get User registered mobile_number from shared preference.......
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userMobileNumber = prefs.getString("Mobile Number", null);
        JSONArray gruopMemberMobileNumber = new JSONArray();
        mydb = new SQLiteDatabase(getApplicationContext());
        contactNameWithNumberArrayList = new ArrayList<>();
        contactNameWithNumberArrayList = mydb.getAllMembersUsingGroupNames(groupName);
        final JSONArray groupMemberMobNoJsonArray = new JSONArray();
        for(int i = 0; i < contactNameWithNumberArrayList.size(); i++){
            gruopMemberMobileNumber.put(contactNameWithNumberArrayList.get(i).getContact_number());
            if(!userMobileNumber.equals(contactNameWithNumberArrayList.get(i).getContact_number())){
                groupMemberMobNoJsonArray.put(contactNameWithNumberArrayList.get(i).getContact_number());
            }
        }
        Log.i("groupMemberMobNoJsonArray", " " + groupMemberMobNoJsonArray);
        JSONObject jsonObjectGroupMember = new JSONObject();
        try {
            jsonObjectGroupMember.put("userNumbers", groupMemberMobNoJsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
       /* //set AlarmManager class to getGroupMember's location contitnuously....
        Log.i("inside GroupAccepted..."," "+jsonObject);
        Intent intentAlarm = new Intent(getApplicationContext(), MyAlarmManager.class);
        Bundle intentAlarmBundle = new Bundle();
        intentAlarmBundle.putString("groupMemberMobNo", jsonObject.toString());
        intentAlarm.putExtras(intentAlarmBundle);
        // create the object
        alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        //set the alarm for particular time
        alarmManager.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),1000 * 60, pendingIntent);
*/

        if (savedInstanceState == null) {
            FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
            Chat_Main_Fragment chat_main_fragment = new Chat_Main_Fragment(jsonObjectGroupMember,groupAdminMobileNumber,groupName);
            trans.add(R.id.fragment, chat_main_fragment);
            trans.commit();
        }
    }

    /*public static void getGroupMemberLocationResponse(JSONArray jsonArray){
        Log.i("inside....","Chat_Main_Fragment......"+ jsonArray);
        for(int i = 0; i < jsonArray.length(); i++)
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String groupUserMobNo = jsonObject.getString("userNumber");
                double latitude = jsonObject.getDouble("latitude");
                double longitude = jsonObject.getDouble("longitude");
                Log.i("Latlog--->"," "+ latitude+" "+longitude);
                //marker = map.addMarker(new MarkerOptions().title(groupUserMobNo).position(new LatLng(latitude,longitude)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
    }*/
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }
}

