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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.already_group_accepted);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        groupName = bundle.getString("Group Name");
        groupAdminMobileNumber = bundle.getString("GroupAdminMobNo");
        getSupportActionBar().setTitle(groupName);
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

        if (savedInstanceState == null) {
            FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
            Chat_Main_Fragment chat_main_fragment = new Chat_Main_Fragment(jsonObjectGroupMember,groupAdminMobileNumber,groupName);
            trans.add(R.id.fragment, chat_main_fragment);
            trans.commit();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishActivity(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_group_chat,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(getApplicationContext(), User_ProfileActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        if(id == R.id.action_removeMeFromGroup){
            boolean isAccepted = false;
            new RejectAsyncTask(getApplicationContext(), groupName, userMobileNumber, groupAdminMobileNumber, isAccepted).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}

