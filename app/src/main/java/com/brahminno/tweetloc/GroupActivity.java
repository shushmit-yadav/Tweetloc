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

import com.brahminno.tweetloc.backend.tweetApi.TweetApi;
import com.brahminno.tweetloc.backend.tweetApi.model.AcceptanceStatusBean;
import com.brahminno.tweetloc.backend.tweetApi.model.GroupMemberSyncBean;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

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
    private String Group_Name;
    private String groupAdminMobNo;
    private boolean isAccepted;
    private String groupMemberMobNo;
    List<GroupMemberSyncBean> groupBeanDetails;
    SQLiteDatabase mydb;

    public GroupDetailsAsyncTask(Context context, String userMobileNumber) {
        this.context = context;
        this.userMobileNumber = userMobileNumber;
    }

    @Override
    protected String doInBackground(Void... params) {
        Log.i("Inside....","GroupDetailsAsyncTask");
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
            JSONObject responseJsonResult = new JSONObject(responseResult);
            JSONArray jsonArray = responseJsonResult.getJSONArray("groupinfo");
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObjectInJsonArray = jsonArray.getJSONObject(i);
                Group_Name = jsonObjectInJsonArray.getString("groupName");
                groupAdminMobNo = jsonObjectInJsonArray.getString("groupAdminMobNo");
                groupMemberMobNo = jsonObjectInJsonArray.getString("groupMemberMobNo");
                isAccepted = (jsonObjectInJsonArray.getBoolean("isAccepted"));
                //new SyncAllGroupMembers(context,Group_Name,groupAdminMobNo,groupMemberMobNo).execute();
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

//this class is used for sync all group members from server using groupName,groupMemberMobNo, and adminMobNo......
class SyncAllGroupMembers extends AsyncTask<Void,Void,String>{
    private String groupName;
    private String groupAdminMobNo;
    private String groupMemberMobNo;
    private boolean isAccepted;
    private Context context;
    SQLiteDatabase mydb;
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
            //delete GROUP_TABLE Items....
            mydb.deleteGroupTableItems();
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObjectInJsonArray = jsonArray.getJSONObject(i);
                groupName = jsonObjectInJsonArray.getString("groupName");
                groupAdminMobNo = jsonObjectInJsonArray.getString("groupAdminMobNo");
                groupMemberMobNo = jsonObjectInJsonArray.getString("groupMemberMobNo");
                isAccepted = jsonObjectInJsonArray.getBoolean("isAccepted");
                Log.i("group members...","details...:"+groupName+" "+ groupMemberMobNo+" "+groupAdminMobNo+" "+ isAccepted);
                mydb.insertGroups(groupName,groupAdminMobNo,groupMemberMobNo,Boolean.toString(isAccepted));
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
                mydb.updateGroupTableWithSyncInfo(acceptanceStatusBean.get(i).getIsAccepted(), acceptanceStatusBean.get(i).getGroupName(), acceptanceStatusBean.get(i).getMobileNumberMember());
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
    ListView groupNameListView;
    String deviceId;
    String Mobile_Number;
    SQLiteDatabase mydb;
    //ArrayList<GroupDetails> groupDetailsArrayList;
    //ArrayList<ContactNameWithNumber> groupMembersList;
    ArrayList<String> groupNames;
    GetGroupData getGroupData;

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
        //call AsyncMethod to get Groups from server......
        //new GroupDetailsAsyncTask(getApplicationContext(), Mobile_Number).execute();

        //Object of mydb....
        mydb = new SQLiteDatabase(this);
        groupNames = new ArrayList<>();
        //groupDetailsArrayList = new ArrayList<>();
        //groupMembersList = new ArrayList<>();
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
                Toast.makeText(getApplicationContext(),groupName+" clicked",Toast.LENGTH_SHORT).show();
                //Log to debug the application......
                Log.i("Group Name...",groupName+" is clicked");
                Intent intent = new Intent(getApplicationContext(),GroupCheckStatusActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("Group Name", groupName);
                startActivity(intent.putExtras(bundle));
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
