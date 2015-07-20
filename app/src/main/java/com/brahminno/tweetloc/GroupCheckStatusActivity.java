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

//this AsyncClass is for update user acceptance status when user click on Accept button.....
class AcceptAsyncTask extends AsyncTask<Void, Void, String> {
    private Context context;
    private String groupName;
    private String groupMemberMobNo;
    private String groupAdminMobNo;
    private boolean isAccepted;
    SQLiteDatabase mydb;

    public AcceptAsyncTask(Context context, String groupName, String groupMemberMobNo, String groupAdminMobNo, boolean isAccepted) {
        this.context = context;
        this.groupName = groupName;
        this.groupMemberMobNo = groupMemberMobNo;
        this.groupAdminMobNo = groupAdminMobNo;
        this.isAccepted = isAccepted;
    }

    @Override
    protected String doInBackground(Void... params) {
        Log.i("inside AcceptAsyncTask:", " .....");
        try {
            //create HttpClient.....
            HttpClient httpClient = new DefaultHttpClient();
            //Http POST request to given url....
            HttpPost httpPost = new HttpPost("http://104.236.27.79:8080/acceptance");
            String json = null;
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("groupMemberMobNo", groupMemberMobNo);
                jsonObject.put("groupName", groupName);
                jsonObject.put("groupAdminMobNo", groupAdminMobNo);
                jsonObject.put("isAccepted", isAccepted);
                //convert jsonObject to json string....
                json = jsonObject.toString();
                //set json to StringEntity....
                Log.i("json......", json);
                StringEntity stringEntity = new StringEntity(json);
                //set httpPost Entity.....
                httpPost.setEntity(stringEntity);
                //set headers to inform server about type of content.....
                httpPost.setHeader("Content-type", "application/json");
                //execute POST request to the given server.....
                HttpResponse httpResponse = httpClient.execute(httpPost);

                String responseResult = EntityUtils.toString(httpResponse.getEntity());
                //convert responseResult to jsonObject......
                JSONObject responseJsonObject = new JSONObject(responseResult);
                groupName = responseJsonObject.getString("groupName");
                Log.i("into AcceptAsyncTask...: ", "response" + responseJsonObject);
                mydb = new SQLiteDatabase(context);
                mydb.updateStatusOfGroupMemberIntoGroupTable(Boolean.toString(responseJsonObject.getBoolean("isAccepted")), responseJsonObject.getString("groupName"), responseJsonObject.getString("groupMemberMobNo"));

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}

class RejectAsyncTask extends AsyncTask<Void, Void, String> {
    private Context context;
    private String groupName;
    private String groupMemberMobNo;
    private boolean isAccepted;
    private String groupAdminMobNo;
    SQLiteDatabase mydb;

    public RejectAsyncTask(Context context, String groupName, String groupMemberMobNo, String groupAdminMobNo, boolean isAccepted) {
        this.context = context;
        this.groupName = groupName;
        this.groupMemberMobNo = groupMemberMobNo;
        this.groupAdminMobNo = groupAdminMobNo;
        this.isAccepted = isAccepted;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            //create HttpClient.....
            HttpClient httpClient = new DefaultHttpClient();
            //Http POST request to given url....
            HttpPost httpPost = new HttpPost("http://104.236.27.79:8080/rejection");
            String json = null;
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("groupMemberMobNo", groupMemberMobNo);
                jsonObject.put("groupName", groupName);
                jsonObject.put("groupAdminMobNo", groupAdminMobNo);
                jsonObject.put("isAccepted", isAccepted);
                //convert jsonObject to json string....
                json = jsonObject.toString();
                //set json to StringEntity....
                Log.i("json......", json);
                StringEntity stringEntity = new StringEntity(json);
                //set httpPost Entity.....
                httpPost.setEntity(stringEntity);
                //set headers to inform server about type of content.....
                httpPost.setHeader("Content-type", "application/json");
                //execute POST request to the given server.....
                HttpResponse httpResponse = httpClient.execute(httpPost);

                String responseResult = EntityUtils.toString(httpResponse.getEntity());
                //convert responseResult to jsonObject......
                //JSONObject responseJsonObject = new JSONObject(responseResult);
                Log.i("responseResult...: ", "into RejectAsyncTask..." + responseResult);

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
           /* //call sqlite database method to delete group when user click reject button.......
            mydb = new SQLiteDatabase(context);
            mydb.deleteGroupFromGroupTable(statusBean.getGroupName());*/
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        //super.onPostExecute(s);

    }
}

//this class is called when user click on an group from grouplist........
//Shushmit Yadav....
//Brahmastra Innovations........
public class GroupCheckStatusActivity extends ActionBarActivity {

    SQLiteDatabase mydb;
    ArrayList<ContactNameWithNumber> contactNameWithNumberArrayList;
    GroupsAdapter adapter;
    String userMobileNumber;
    String groupName;
    String adminMobileNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        Button btnRejectGroup, btnAcceptGroup;
        ListView listViewGroupMembers;
        btnAcceptGroup = (Button) findViewById(R.id.btnAccept);
        btnRejectGroup = (Button) findViewById(R.id.btnReject);
        Log.i("GroupCheckStatus", " Activity is clicked");
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        groupName = bundle.getString("Group Name");
        adminMobileNumber = bundle.getString("adminMobileNumber");
        //set group name as title to activity.............
        getSupportActionBar().setTitle(groupName);
        //get User registered mobile_number from shared preference.......
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userMobileNumber = prefs.getString("Mobile Number", null);
        Log.i("Registered Number....", userMobileNumber);
        mydb = new SQLiteDatabase(this);
        contactNameWithNumberArrayList = new ArrayList<>();
        contactNameWithNumberArrayList = mydb.getAllMembersUsingGroupNames(groupName);
        Log.i("size of list...", " " + contactNameWithNumberArrayList.size());

        listViewGroupMembers = (ListView) findViewById(R.id.listViewGroupmembers);
        adapter = new GroupsAdapter(getApplicationContext(), contactNameWithNumberArrayList);
        listViewGroupMembers.setAdapter(adapter);
        //on Accept button click event......
        btnAcceptGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isAccepted = true;
                //call async class to update user acceptance status as true.......
                new AcceptAsyncTask(getApplicationContext(), groupName, userMobileNumber, adminMobileNumber, isAccepted).execute();
                //intent to go next activity when user accept group.....
                Intent groupAcceptedIntent = new Intent(getApplicationContext(), GroupAccepted.class);
                Bundle groupAcceptedBundle = new Bundle();
                groupAcceptedBundle.putString("Group Name", groupName);
                groupAcceptedBundle.putString("GroupAdminMobNo", adminMobileNumber);
                startActivity(groupAcceptedIntent.putExtras(groupAcceptedBundle));
            }
        });
        //on Reject button click event........
        btnRejectGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isAccepted = false;
                //call async class to update user acceptance status as false.......
                new RejectAsyncTask(getApplicationContext(), groupName, userMobileNumber, adminMobileNumber, isAccepted).execute();
                mydb = new SQLiteDatabase(getApplicationContext());
                mydb.deleteGroupFromGroupTable(groupName, adminMobileNumber);
                //Intent to go previous activity......
                Intent intent = new Intent(getApplicationContext(), GroupActivity.class);
                startActivity(intent);
            }
        });
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
        if (id == R.id.action_addNewMember) {
            Intent intent = new Intent(getApplicationContext(), AddNewMemberActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("Group Name", groupName);
            startActivity(intent.putExtras(bundle));
        }

        return super.onOptionsItemSelected(item);
    }
}
