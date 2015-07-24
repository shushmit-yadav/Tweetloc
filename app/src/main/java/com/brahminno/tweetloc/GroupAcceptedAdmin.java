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

//this asyncTask class will class when groupAdmin press DeleteGroup Button from options menu....
class DeleteGroupAsyncClass extends AsyncTask<Void,Void,Boolean>{
    private Context context;
    private String groupName;
    private String groupAdminNum;
    private JSONArray groupMemberMobNo;
    private boolean responseBoolean;
    SQLiteDatabase mydb;
    public DeleteGroupAsyncClass(Context context,String groupName,String groupAdminNo,JSONArray groupMemberMobNo){
        this.context = context;
        this.groupName = groupName;
        this.groupAdminNum = groupAdminNo;
        this.groupMemberMobNo = groupMemberMobNo;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try{
            //create HttpClient.....
            HttpClient httpClient = new DefaultHttpClient();
            //Http POST request to given url....
            HttpPost httpPost = new HttpPost("http://104.236.27.79:8080/deletegroup");
            String json = null;
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("groupName",groupName);
                jsonObject.put("groupAdminMobNo",groupAdminNum);
                jsonObject.put("groupMembers",groupMemberMobNo);
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
                //receive response from server as inputStream............
                String responseResult = EntityUtils.toString(httpResponse.getEntity());
                Log.i("responseResult...: ", responseResult);
                JSONObject obj = new JSONObject(responseResult);
                responseBoolean = obj.getBoolean("status");
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return responseBoolean;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        Log.i("inside onPostExecute.."," method "+ aBoolean);
        mydb = new SQLiteDatabase(context);
        if(aBoolean){
            mydb.deleteGroupFromGroupTable(groupName,groupAdminNum);
            Intent intent = new Intent(context.getApplicationContext(),GroupActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.getApplicationContext().startActivity(intent);
        }
    }
}

//this class will appear when user id groupAdmin of that group.....
public class GroupAcceptedAdmin extends ActionBarActivity {
    SQLiteDatabase mydb;
    private String groupName;
    private String userMobileNumber;
    private String groupAdminMobileNumber;
    JSONArray gruopMemberMobileNumber;
    ArrayList<ContactNameWithNumber> contactNameWithNumberArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_accepted_admin);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        groupName = bundle.getString("Group Name");
        groupAdminMobileNumber = bundle.getString("GroupAdminMobNo");
        getSupportActionBar().setTitle(groupName);
        //get User registered mobile_number from shared preference.......
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userMobileNumber = prefs.getString("Mobile Number", null);
        gruopMemberMobileNumber = new JSONArray();
        mydb = new SQLiteDatabase(getApplicationContext());
        contactNameWithNumberArrayList = new ArrayList<>();
        contactNameWithNumberArrayList = mydb.getAllMembersUsingGroupNames(groupName);
        final JSONArray groupMemberMobNoJsonArray = new JSONArray();
        for (int i = 0; i < contactNameWithNumberArrayList.size(); i++) {
            gruopMemberMobileNumber.put(contactNameWithNumberArrayList.get(i).getContact_number());
            if (!userMobileNumber.equals(contactNameWithNumberArrayList.get(i).getContact_number())) {
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
            Chat_Main_Fragment chat_main_fragment = new Chat_Main_Fragment(jsonObjectGroupMember, groupAdminMobileNumber, groupName);
            trans.add(R.id.fragment, chat_main_fragment);
            trans.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_group_accepted_admin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(getApplicationContext(), User_ProfileActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        if(id == R.id.action_addNewMember){
            Intent intent = new Intent(getApplicationContext(),AddNewMemberActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("Group Name",groupName);
            startActivity(intent.putExtras(bundle));
            finish();
            return true;
        }
        if(id == R.id.action_deleteGroup){
            new DeleteGroupAsyncClass(getApplicationContext(),groupName,groupAdminMobileNumber,gruopMemberMobileNumber).execute();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
