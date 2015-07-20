package com.brahminno.tweetloc;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONArray;

import java.util.ArrayList;

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
        for(int i = 0; i < contactNameWithNumberArrayList.size(); i++){
            gruopMemberMobileNumber.put(contactNameWithNumberArrayList.get(i).getContact_number());
        }
        if (savedInstanceState == null) {
            FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
            Chat_Main_Fragment chat_main_fragment = new Chat_Main_Fragment(gruopMemberMobileNumber,groupAdminMobileNumber,groupName);
            trans.add(R.id.fragment, chat_main_fragment);
            trans.commit();
        }
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
