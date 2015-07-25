package com.brahminno.tweetloc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

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
import java.util.Collections;
import java.util.Comparator;

//this Async class is used to add new members in an existing group......
class AddNewMembersAsyncTask extends AsyncTask<Void, Void, Boolean> {
    private Context context;
    private String groupName, groupAdminMobNo;
    private ArrayList<String> groupmembers;
    private boolean responseResultBoolean;
    SQLiteDatabase mydb;
    AddNewMemberActivity addNewMemberActivity;

    public AddNewMembersAsyncTask(Context context, String groupName, String groupAdminMobNo, ArrayList<String> groupmembers) {
        this.context = context;
        this.groupName = groupName;
        this.groupAdminMobNo = groupAdminMobNo;
        this.groupmembers = groupmembers;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            Log.i("inside...", "AddNewMemberAsyncTask....");
            //create HttpClient.....
            HttpClient httpClient = new DefaultHttpClient();
            //Http POST request to given url....
            HttpPost httpPost = new HttpPost("http://104.236.27.79:8080/addnewmember");
            String json = null;
            JSONObject jsonObject = new JSONObject();
            try {
                JSONArray groupMemberJsonArray = new JSONArray();
                for (int i = 0; i < groupmembers.size(); i++) {
                    groupMemberJsonArray.put(groupmembers.get(i));
                }
                jsonObject.put("groupAdminMobNo", groupAdminMobNo);
                jsonObject.put("groupName", groupName);
                jsonObject.put("groupMembers", groupMemberJsonArray);
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
                //get httpResponse into string.....
                String responseResult = EntityUtils.toString(httpResponse.getEntity());
                //convert responseResult to jsonObject......
                JSONObject responseJsonObject = new JSONObject(responseResult);
                Log.i("responseJsonObject...", "" + responseResult);
                responseResultBoolean = responseJsonObject.getBoolean("status");

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return responseResultBoolean;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        mydb = new SQLiteDatabase(context);
        boolean isAccepted = false;
        Log.i("onPostExecute...."," "+aBoolean);
        if (aBoolean) {
            for (int i = 0; i < groupmembers.size(); i++) {
                mydb.insertGroups(groupName, groupAdminMobNo, groupmembers.get(i), Boolean.toString(isAccepted));
            }
        }
        Intent intent = new Intent(context.getApplicationContext(),GroupAccepted.class);
        Bundle bundle = new Bundle();
        bundle.putString("Group Name",groupName);
        bundle.putString("GroupAdminMobNo", groupAdminMobNo);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.getApplicationContext().startActivity(intent.putExtras(bundle));
    }
}

//When group admin wants to add new members into his/her group, then this class is used......
public class AddNewMemberActivity extends ActionBarActivity {

    static String groupName;
    ArrayList<String> Group_Member;
    static String adminMobileNumber;
    SQLiteDatabase mydb;
    ListView listViewAddNewContactToGroup;
    NonExistingContactsAdapter nonExistingContactsAdapter;
    Button btnNewAddSelectedContact;
    ArrayList<ContactNameWithNumber> nonExistingContactList;
    static Context context;
    static Activity activity = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_member);
        activity = this;
        listViewAddNewContactToGroup = (ListView) findViewById(R.id.addNewMemberListView);
        btnNewAddSelectedContact = (Button) findViewById(R.id.btnNewAddSelectedContact);
        //get groupName from intent......
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        groupName = bundle.getString("Group Name", groupName);
        Group_Member = new ArrayList<>();
        try {
            mydb = new SQLiteDatabase(this);
            //get Admin Mobile Number from database using groupName....
            adminMobileNumber = mydb.getAdminMobileNumberUsingGroupName(groupName);
            nonExistingContactList = new ArrayList<>();
            nonExistingContactList = mydb.getNonExistingContacts(groupName, adminMobileNumber);
            for (int i = 0; i < nonExistingContactList.size(); i++) {
                Log.i("Contacts.....", "" + nonExistingContactList.get(i).getContact_name() + "-->" + nonExistingContactList.get(i).getContact_number());
            }
            Log.i("size of...", "non existing contacts" + nonExistingContactList.size());
            Collections.sort(nonExistingContactList, new Comparator<ContactNameWithNumber>() {
                public int compare(ContactNameWithNumber a, ContactNameWithNumber b) {
                    return a.getContact_name().compareTo(b.getContact_name());
                }
            });

            nonExistingContactsAdapter = new NonExistingContactsAdapter(getApplicationContext(), nonExistingContactList);
            listViewAddNewContactToGroup.setAdapter(nonExistingContactsAdapter);
            //on button click event......
            btnNewAddSelectedContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("onButtonClick.....",".... ");
                    Group_Member = nonExistingContactsAdapter.getArrayList();
                    new AddNewMembersAsyncTask(getApplicationContext(), groupName, adminMobileNumber, Group_Member).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            });


        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_new_member, menu);
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

        return super.onOptionsItemSelected(item);
    }

   /* public static void getResponseStatus(Boolean response){
        if(response){
            Log.i("getResponseStatus..."," "+response);
            Intent intent = new Intent(context.getApplicationContext(),GroupAccepted.class);
            Bundle bundle = new Bundle();
            bundle.putString("Group Name",groupName);
            bundle.putString("GroupAdminMobNo",adminMobileNumber);
            context.getApplicationContext().startActivity(intent.putExtras(bundle));
            activity.finish();
        }
    }*/
}
