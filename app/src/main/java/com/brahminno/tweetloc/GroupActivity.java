package com.brahminno.tweetloc;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;

import com.brahminno.tweetloc.backend.tweetApi.TweetApi;
import com.brahminno.tweetloc.backend.tweetApi.model.AcceptanceStatusBean;
import com.brahminno.tweetloc.backend.tweetApi.model.GroupMemberSyncBean;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.util.ArrayList;
import java.util.List;

//This Async class is used for getting all information of groups based on Mobile num.......
class GroupDetailsAsyncTask extends AsyncTask<Void, Void, String> {
    private static TweetApi myTweetApi = null;
    private Context context;
    private String Mobile_Number;
    private String Group_Name;
    private ArrayList<String> Group_Members;
    List<GroupMemberSyncBean> groupBeanDetails;
    SQLiteDatabase mydb;

    public GroupDetailsAsyncTask(Context context, String Mobile_Number) {
        this.context = context;
        this.Mobile_Number = Mobile_Number;
    }

    @Override
    protected String doInBackground(Void... params) {
        Log.i("doInBackground...", "method starts");
        if (myTweetApi == null) {
            TweetApi.Builder builder = new TweetApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl("https://brahminno.appspot.com/_ah/api/");

            myTweetApi = builder.build();
        }
        try {
            Log.i("doInBackground...", "try block...");
            //call group Details Api.....
            groupBeanDetails = myTweetApi.groupMemberSync(Mobile_Number).execute().getItems();
            mydb = new SQLiteDatabase(context);
            //delete GROUP_TABLE Items....
            mydb.deleteGroupTableItems();
            for (GroupMemberSyncBean result : groupBeanDetails) {
                Log.i("Inside for loop...", "values");
                for (int i = 0; i < result.getGroupMember().size(); i++) {
                    String isAccepted = "unknown";
                    if (result.getGroupAdminNumber().equals(result.getGroupMember().get(i))) {
                        isAccepted = "true";
                    }
                    if (Mobile_Number.equals(result.getGroupMember().get(i))) {
                        isAccepted = result.getIsAccepted();
                    }
                    //store data to local app db.........
                    mydb.insertGroups(result.getGroupName(), result.getGroupAdminNumber(), isAccepted, result.getGroupMember().get(i));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }
}

//this Async class is used for Sync all member's status in a group........
class AcceptStatusAsyncTask extends AsyncTask<Void,Void,String>{

    GetGroupData getGroupData;
    private Context context;
    private static TweetApi myTweetApi = null;
    List<AcceptanceStatusBean> acceptanceStatusBean;
    SQLiteDatabase mydb;

    public AcceptStatusAsyncTask(Context context,GetGroupData getGroupData){
        this.getGroupData = getGroupData;
        this.context = context;
    }

    @Override
    protected String doInBackground(Void... params) {

        if (myTweetApi == null) {
            TweetApi.Builder builder = new TweetApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl("https://brahminno.appspot.com/_ah/api/");

            myTweetApi = builder.build();
        }
        try{
            AcceptanceStatusBean acceptanceStatus = new AcceptanceStatusBean();
            acceptanceStatus.setGroupNameList(getGroupData.getGroupNameList());
            acceptanceStatus.setGroupMemberNumberList(getGroupData.getGroupMemberNumberList());
            acceptanceStatusBean = myTweetApi.processAcceptanceStatusRequest(acceptanceStatus).execute().getItems();
            Log.i("Size of ...","acceptanceStatusBean..."+acceptanceStatusBean.size());
            mydb = new SQLiteDatabase(context);
            for(int i = 0; i < acceptanceStatusBean.size(); i++){
                Log.i("isAccepted.."," "+acceptanceStatusBean.get(i).getIsAccepted());
                Log.i("Group Name.."," "+acceptanceStatusBean.get(i).getGroupName());
                Log.i("GroupMember Number.."," "+acceptanceStatusBean.get(i).getMobileNumberMember());
                mydb.updateGroupTableWithSyncInfo(acceptanceStatusBean.get(i).getIsAccepted(),acceptanceStatusBean.get(i).getGroupName(),acceptanceStatusBean.get(i).getMobileNumberMember());
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        return null;
    }
}


public class GroupActivity extends ActionBarActivity {

    Button btnCreateGroup;
    ExpandableListView groupListView;
    GroupsAdapter groupsAdapter;
    String deviceId;
    String Mobile_Number;
    SQLiteDatabase mydb;
    ArrayList<GroupDetails> groupDetailsArrayList;
    ArrayList<ContactNameWithNumber> groupMembersList;
    ArrayList<String> groupNames;
    GetGroupData getGroupData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_hdr_grp_128);
        setContentView(R.layout.activity_group);
        groupListView = (ExpandableListView) findViewById(R.id.groupNameListView);
        btnCreateGroup = (Button) findViewById(R.id.btnCreateGroup);
        //get device id from shared preference.....
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        deviceId = prefs.getString("Device Id", null);
        Mobile_Number = prefs.getString("Mobile Number", null);
        Log.i("device id is....", deviceId);
        Log.i("Mobile number is....", Mobile_Number);
        btnCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CreateGroupActivity.class);
                startActivity(intent);
            }
        });
        Log.i("AsyncTask method....", " starts");
        //call AsyncMethod to get Groups from server......
        new GroupDetailsAsyncTask(getApplicationContext(), Mobile_Number).execute();

        //Object of mydb....
        mydb = new SQLiteDatabase(this);
        groupNames = new ArrayList<>();
        groupDetailsArrayList = new ArrayList<>();
        groupMembersList = new ArrayList<>();
        groupNames = mydb.getUniqueGroupNamesFromGroupTable();
        for (int i = 0; i < groupNames.size(); i++) {
            //create object of GroupDetails class.....
            GroupDetails details = new GroupDetails();
            details.setGroupName(groupNames.get(i));

            Log.i("Group Name...", groupNames.get(i));
            //groupMembersList = new ArrayList<>();
            groupMembersList = mydb.getAllMembersUsingGroupNames(groupNames.get(i));
            details.setGroupMembers(groupMembersList);
            //for loop to check group members .........
            for (int j = 0; j < groupMembersList.size(); j++) {
                Log.i("Group Name...", "with member " + groupNames.get(i) + "-->" + groupMembersList.get(j));
            }
            groupDetailsArrayList.add(details);
        }
        //set data to adapter.......
        groupsAdapter = new GroupsAdapter(getApplicationContext(),groupDetailsArrayList);
        //set adapter to expandable listview....
        groupListView.setAdapter(groupsAdapter);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_group, menu);
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
        if (id == R.id.action_invte) {
            //call inviteContacts() method to invite the contacts through installed app in device...
            inviteContacts();
            return true;
        }
        if(id == R.id.action_syncAcceptanceStatus){
            syncAcceptanceGroupMember();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //this method is used for inviting contacts through social media android app....
    public void inviteContacts() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "here is the link to download TweetLoc");
        startActivity(intent);
    }

    //methos to sync group member status from server.....
    public void syncAcceptanceGroupMember(){
        mydb = new SQLiteDatabase(this);
        getGroupData = new GetGroupData();
        getGroupData = mydb.getAllMobileNumberForSyncAccpt();
        ArrayList<String> groupNameList = new ArrayList<>();
        ArrayList<String> groupMemberNumberList = new ArrayList<>();
        Log.i("Size of groupName.."," "+getGroupData.getGroupNameList().size());
        Log.i("Size of groupMember..", " " + getGroupData.getGroupMemberNumberList().size());
        //call AsyncMethod to Sync status from server in background.....
        if(getGroupData.getGroupNameList().size() > 0 && getGroupData.getGroupMemberNumberList().size() > 0){
            new AcceptStatusAsyncTask(getApplicationContext(),getGroupData).execute();
        }

    }
}
