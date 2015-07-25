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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
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
import java.util.List;

//This Async class is used for getting all information of groups based on Mobile num.......
class GroupDetailsAsyncTask extends AsyncTask<Void, Void, String> {
    private Context context;
    private String userMobileNumber;
    SQLiteDatabase mydb;

    public GroupDetailsAsyncTask(Context context, String userMobileNumber) {
        this.context = context;
        this.userMobileNumber = userMobileNumber;
    }

    @Override
    protected String doInBackground(Void... params) {
        mydb = new SQLiteDatabase(context);
        //delete GROUP_TABLE Items....
        mydb.deleteGroupTableItems();
        Log.i("GroupDetailsAsyncTask....", "GroupDetailsAsyncTask");
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("http://104.236.27.79:8080/syncgroup");
        String json = null;
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("userMobileNum", userMobileNumber);
            json = jsonObject.toString();
            StringEntity stringEntity = new StringEntity(json);
            httpPost.setEntity(stringEntity);
            httpPost.setHeader("Content-type", "application/json");
            HttpResponse httpResponse = httpClient.execute(httpPost);
            String responseResult = EntityUtils.toString(httpResponse.getEntity());
            Log.i("GroupDetailsAsyncTask..","...:"+responseResult);
            JSONObject responseJsonObj  = new JSONObject(responseResult);
            if(responseJsonObj.getBoolean("status")){
                JSONArray responseJsonArray = responseJsonObj.getJSONArray("members");
                if(responseJsonArray.length() > 0){
                    for(int i = 0; i < responseJsonArray.length(); i++){
                        JSONArray groupArray = responseJsonArray.getJSONArray(i);
                        for(int j = 0; j < groupArray.length(); j++){
                            JSONObject groupJsonObject = groupArray.getJSONObject(j);
                            Log.i("insert groups", " into groupTable..." + groupJsonObject.getString("groupName")+" "+ groupJsonObject.getString("groupAdminMobNo")+" "+ groupJsonObject.getString("groupMemberMobNo")+" "+ Boolean.toString(groupJsonObject.getBoolean("isAccepted")));
                                    mydb.insertGroups(groupJsonObject.getString("groupName"), groupJsonObject.getString("groupAdminMobNo"), groupJsonObject.getString("groupMemberMobNo"), Boolean.toString(groupJsonObject.getBoolean("isAccepted")));
                        }
                    }
                }
                else{
                    Log.i("No Group Sync..."," available");
                }
            }
            else{
                Log.i("No Group Sync..."," available");
            }
        }
        catch (JSONException ex){
            ex.printStackTrace();
        }
        catch (UnsupportedEncodingException ex){
            ex.printStackTrace();
        }
        catch (ClientProtocolException ex){
            ex.printStackTrace();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return "";
    }
}

/*
//this class is used for sync all group members from server using groupName,groupMemberMobNo, and adminMobNo......
class SyncAllGroupMembers extends AsyncTask<Void,Void,String>{
    private String groupName;
    private String groupAdminMobNo;
    private String groupMemberMobNo;
    private Context context;
    SQLiteDatabase mydb;
    GroupActivity activity;
    public SyncAllGroupMembers(Context context,String groupName,String groupAdminMobNo,String groupMemberMobNo){
        this.context = context;
        this.groupAdminMobNo = groupAdminMobNo;
        this.groupMemberMobNo = groupMemberMobNo;
        this.groupName = groupName;
    }
    @Override
    protected String doInBackground(Void... params) {
        Log.i("Inside....","SyncAllGroupMembers");
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("http://104.236.27.79:8080/syncallgroupmember");
        String json = null;
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("userMobileNum", groupMemberMobNo);
            jsonObject.put("groupAdminMobNo",groupAdminMobNo);
            jsonObject.put("groupName",groupName);
            json = jsonObject.toString();
            StringEntity stringEntity = new StringEntity(json);
            httpPost.setEntity(stringEntity);
            httpPost.setHeader("Content-type", "application/json");
            HttpResponse httpResponse = httpClient.execute(httpPost);
            String responseResult = EntityUtils.toString(httpResponse.getEntity());
            Log.i("SyncAllGroupMembers..","...:"+responseResult);
            JSONObject responseJsonResult = new JSONObject(responseResult);
            Log.i("SyncAllGroupMembers..","....:"+responseJsonResult);
            JSONArray jsonArray = responseJsonResult.getJSONArray("allGroupMembers");
            mydb = new SQLiteDatabase(context);
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObjectInJsonArray = jsonArray.getJSONObject(i);
                mydb.insertGroups(jsonObjectInJsonArray.getString("groupName"), jsonObjectInJsonArray.getString("groupAdminMobNo"), jsonObjectInJsonArray.getString("groupMemberMobNo"), Boolean.toString(jsonObjectInJsonArray.getBoolean("isAccepted")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        return null;
    }
}
*/


public class GroupActivity extends ActionBarActivity {

    Button btnCreateGroup;
    ListView groupNameListView;
    String deviceId;
    String Mobile_Number;
    SQLiteDatabase mydb;
    ArrayList<String> groupNames;
    GetGroupData getGroupData;
    private String adminMobileNumber;
    private String userOwnGroupAcceptanceStatus;
    ArrayList<ContactNameWithNumber> contactNameWithNumberArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_hdr_grp_128);
        setContentView(R.layout.activity_group);
        groupNameListView = (ListView) findViewById(R.id.groupNameListView);
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
        //Object of mydb....
        mydb = new SQLiteDatabase(this);
        groupNames = new ArrayList<>();
        groupNames = mydb.getUniqueGroupNamesFromGroupTable();
        for(int i = 0; i < groupNames.size(); i++){
            Log.i("Group Names....",groupNames.get(i));
        }
        ArrayAdapter<String> groupNameAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,groupNames);
        groupNameListView.setAdapter(groupNameAdapter);
        groupNameListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //listview clicked item index.......
                int itemPosition = position;
                //listview clicked item value.......
                String groupName = (String) groupNameListView.getItemAtPosition(position);
                //toast to show clicked group name......
                Toast.makeText(getApplicationContext(), groupName + " clicked", Toast.LENGTH_SHORT).show();
                //Log to debug the application......
                Log.i("Group Name...", groupName + " is clicked");
                adminMobileNumber = mydb.getAdminMobileNumberUsingGroupName(groupName);
                Log.i("Admin Number...."," in GroupActivity is "+ adminMobileNumber);
                contactNameWithNumberArrayList = new ArrayList<>();
                contactNameWithNumberArrayList = mydb.getAllMembersUsingGroupNames(groupName);
                for (int i = 0; i < contactNameWithNumberArrayList.size(); i++) {
                    if (Mobile_Number.equals(contactNameWithNumberArrayList.get(i).getContact_number())) {
                        Log.i("Inside if....", "" + Mobile_Number.equals(contactNameWithNumberArrayList.get(i).getContact_number()));
                        userOwnGroupAcceptanceStatus = contactNameWithNumberArrayList.get(i).getMemberAcceptanceStatus();
                    }
                }

                if(userOwnGroupAcceptanceStatus.equals("false")){
                    Intent intent = new Intent(getApplicationContext(), GroupCheckStatusActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("Group Name", groupName);
                    bundle.putString("GroupAdminMobNo", adminMobileNumber);
                    startActivity(intent.putExtras(bundle));
                }
                else if(Mobile_Number.equals(adminMobileNumber)){
                    Intent groupAcceptedIntent = new Intent(getApplicationContext(),GroupAcceptedAdmin.class);
                    Bundle groupAcceptedBundle = new Bundle();
                    groupAcceptedBundle.putString("Group Name", groupName);
                    groupAcceptedBundle.putString("GroupAdminMobNo", adminMobileNumber);
                    startActivity(groupAcceptedIntent.putExtras(groupAcceptedBundle));
                }
                else{
                    Intent groupAcceptedIntent = new Intent(getApplicationContext(),GroupAccepted.class);
                    Bundle groupAcceptedBundle = new Bundle();
                    groupAcceptedBundle.putString("Group Name", groupName);
                    groupAcceptedBundle.putString("GroupAdminMobNo", adminMobileNumber);
                    startActivity(groupAcceptedIntent.putExtras(groupAcceptedBundle));
                }
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_group, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Object of mydb....
        mydb = new SQLiteDatabase(this);
        groupNames = new ArrayList<>();
        groupNames = mydb.getUniqueGroupNamesFromGroupTable();
        for(int i = 0; i < groupNames.size(); i++){
            Log.i("Group Names....",groupNames.get(i));
        }
        ArrayAdapter<String> groupNameAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,groupNames);
        groupNameListView.setAdapter(groupNameAdapter);
    }

    @Override
    public void onBackPressed() {
       /* Intent intent = new Intent(getApplicationContext(),MyTrail.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Bundle bundle = new Bundle();
        bundle.putString("Device_Id",deviceId);
        bundle.putString("User_Mob_No",Mobile_Number);
        intent.putExtras(bundle);
        startActivity(intent);*/
        super.onBackPressed();
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
        if(id == R.id.action_groupSync){
            //call AsyncMethod to get Groups from server......
            new GroupDetailsAsyncTask(getApplicationContext(), Mobile_Number).execute();
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
        Log.i("Size of groupName..", " " + getGroupData.getGroupNameList().size());
        Log.i("Size of groupMember..", " " + getGroupData.getGroupMemberNumberList().size());
        //call AsyncMethod to Sync status from server in background.....
        if(getGroupData.getGroupNameList().size() > 0 && getGroupData.getGroupMemberNumberList().size() > 0){
            //new AcceptStatusAsyncTask(getApplicationContext(),getGroupData).execute();
        }

    }
}
