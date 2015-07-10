package com.brahminno.tweetloc;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.brahminno.tweetloc.backend.tweetApi.TweetApi;
import com.brahminno.tweetloc.backend.tweetApi.model.AcceptanceStatusBean;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import org.json.JSONArray;

import java.util.ArrayList;

//this AsyncClass is for update user acceptance status when user click on Accept button.....
class AcceptAsyncTask extends AsyncTask<Void, Void, String> {
    private static TweetApi myTweetApi = null;
    private Context context;
    private String groupName;
    private String groupMemberMobileNumber;
    private String acceptanceStatus;
    SQLiteDatabase mydb;
    AcceptanceStatusBean acceptanceStatusBean;

    public AcceptAsyncTask(Context context, String groupName, String groupMemberMobileNumber, String acceptanceStatus) {
        this.context = context;
        this.groupName = groupName;
        this.groupMemberMobileNumber = groupMemberMobileNumber;
        this.acceptanceStatus = acceptanceStatus;
    }

    @Override
    protected String doInBackground(Void... params) {
        if (myTweetApi == null) {
            TweetApi.Builder builder = new TweetApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl("https://brahminno.appspot.com/_ah/api/");

            myTweetApi = builder.build();
        }
        try {
            AcceptanceStatusBean statusBean = new AcceptanceStatusBean();
            statusBean.setGroupName(groupName);
            statusBean.setMobileNumberMember(groupMemberMobileNumber);
            statusBean.setIsAccepted(acceptanceStatus);

            acceptanceStatusBean = myTweetApi.memberAcceptanceStatus(statusBean).execute();

            Log.i("status response...", "" + acceptanceStatusBean.getGroupName());
            Log.i("status response...", "" + acceptanceStatusBean.getMobileNumberMember());
            Log.i("status response...", "" + acceptanceStatusBean.getIsAccepted());

            //after getting response from server, update this response to app local database........
            mydb = new SQLiteDatabase(context);
            mydb.updateStatusOfGroupMemberIntoGroupTable(acceptanceStatusBean.getIsAccepted(), acceptanceStatusBean.getGroupName(), acceptanceStatusBean.getMobileNumberMember());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}

class RejectAsyncTask extends AsyncTask<Void, Void, String> {
    private static TweetApi myTweetApi = null;
    private Context context;
    private String groupName;
    private String groupMemberMobileNumber;
    private String acceptanceStatus;
    SQLiteDatabase mydb;

    AcceptanceStatusBean statusBean;

    public RejectAsyncTask(Context context, String groupName, String groupMemberMobileNumber, String acceptanceStatus) {
        this.context = context;
        this.groupName = groupName;
        this.groupMemberMobileNumber = groupMemberMobileNumber;
        this.acceptanceStatus = acceptanceStatus;
    }

    @Override
    protected String doInBackground(Void... params) {
        if (myTweetApi == null) {
            TweetApi.Builder builder = new TweetApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl("https://brahminno.appspot.com/_ah/api/");

            myTweetApi = builder.build();
        }
        try {
            AcceptanceStatusBean rejectStatusBean = new AcceptanceStatusBean();
            rejectStatusBean.setGroupName(groupName);
            rejectStatusBean.setMobileNumberMember(groupMemberMobileNumber);
            rejectStatusBean.setIsAccepted(acceptanceStatus);
            statusBean = myTweetApi.forgetEntityFromGroup(rejectStatusBean).execute();
            Log.i("status.....", "" + statusBean.getIsAccepted());
            Log.i("status.....", "" + statusBean.getGroupName());
            Log.i("status.....", "" + statusBean.getMobileNumberMember());
            //call sqlite database method to delete group when user click reject button.......
            mydb = new SQLiteDatabase(context);
            mydb.deleteGroupFromGroupTable(statusBean.getGroupName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}

//this class is called when user click on an group from grouplist........
//Shushmit Yadav....
//Brahmastra Innovations........
public class GroupCheckStatusActivity extends ActionBarActivity {

    SQLiteDatabase mydb;
    ArrayList<ContactNameWithNumber> contactNameWithNumberArrayList;
    String userOwnGroupAcceptanceStatus;
    GroupsAdapter adapter;
    String userMobileNumber;
    String groupName;
    String adminMobileNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("GroupCheckStatus", " Activity is clicked");
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        groupName = bundle.getString("Group Name");
        //set group name as title to activity.............
        getSupportActionBar().setTitle(groupName);
        //get User registered mobile_number from shared preference.......
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userMobileNumber = prefs.getString("Mobile Number", null);
        Log.i("Registered Number....", userMobileNumber);
        mydb = new SQLiteDatabase(this);
        //call method to get admin mobile number using group name  from local app db.....
        adminMobileNumber = mydb.getAdminMobileNumberUsingGroupName(groupName);
        contactNameWithNumberArrayList = new ArrayList<>();
        contactNameWithNumberArrayList = mydb.getAllMembersUsingGroupNames(groupName);
        Log.i("size of list...", " " + contactNameWithNumberArrayList.size());
        for (int i = 0; i < contactNameWithNumberArrayList.size(); i++) {
            Log.i("Contact Name....", " " + contactNameWithNumberArrayList.get(i).getContact_name());
            Log.i("Contact Number....", " " + contactNameWithNumberArrayList.get(i).getContact_number());
            Log.i("Contact Status....", " " + contactNameWithNumberArrayList.get(i).getMemberAcceptanceStatus());
            if (userMobileNumber.equals(contactNameWithNumberArrayList.get(i).getContact_number())) {
                Log.i("Inside if....", "" + userMobileNumber.equals(contactNameWithNumberArrayList.get(i).getContact_number()));
                userOwnGroupAcceptanceStatus = contactNameWithNumberArrayList.get(i).getMemberAcceptanceStatus();
            }
        }
        //if userGroupAcceptanceStatus is false or unknown then if condition otherwise else condition..........
        if (userOwnGroupAcceptanceStatus.equals("false") || userOwnGroupAcceptanceStatus.equals("unknown")) {
            setContentView(R.layout.activity_group_chat);
            Button btnRejectGroup, btnAcceptGroup;
            ListView listViewGroupMembers;
            btnAcceptGroup = (Button) findViewById(R.id.btnAccept);
            btnRejectGroup = (Button) findViewById(R.id.btnReject);
            listViewGroupMembers = (ListView) findViewById(R.id.listViewGroupmembers);
            adapter = new GroupsAdapter(getApplicationContext(), contactNameWithNumberArrayList);
            listViewGroupMembers.setAdapter(adapter);
            //on Accept button click event......
            btnAcceptGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String acceptanceStatus = "true";
                    //call async class to update user acceptance status as true.......
                    new AcceptAsyncTask(getApplicationContext(), groupName, userMobileNumber, acceptanceStatus).execute();
                }
            });
            //on Reject button click event........
            btnRejectGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String acceptanceStatus = "false";
                    //call async class to update user acceptance status as false.......
                    new RejectAsyncTask(getApplicationContext(), groupName, userMobileNumber, acceptanceStatus).execute();
                }
            });
        } else {
            //if user already accepted group, then this screen will appear to user for chat......
            setContentView(R.layout.already_group_accepted);
            JSONArray gruopMemberMobileNumber = new JSONArray();
            for(int i = 0; i < contactNameWithNumberArrayList.size(); i++){
                gruopMemberMobileNumber.put(contactNameWithNumberArrayList.get(i).getContact_number());
            }
            if (savedInstanceState == null) {
                FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
                Chat_Main_Fragment chat_main_fragment = new Chat_Main_Fragment(gruopMemberMobileNumber,adminMobileNumber,groupName);
                trans.add(R.id.fragment, chat_main_fragment);
                trans.commit();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_group_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if(id == R.id.action_addNewMember){
            Intent intent = new Intent(getApplicationContext(),AddNewMemberActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("Group Name", groupName);
            startActivity(intent.putExtras(bundle));
        }

        return super.onOptionsItemSelected(item);
    }
}
