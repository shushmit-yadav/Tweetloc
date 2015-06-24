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
import android.view.Window;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.brahminno.tweetloc.backend.tweetApi.TweetApi;
import com.brahminno.tweetloc.backend.tweetApi.model.GroupBean;
import com.brahminno.tweetloc.backend.tweetApi.model.GroupMemberSyncBean;
import com.brahminno.tweetloc.backend.tweetApi.model.GroupMemberSyncBeanCollection;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.util.ArrayList;
import java.util.Iterator;
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
            for (GroupMemberSyncBean result : groupBeanDetails){
                Log.i("Inside for loop...","values");
                //store data to local app db.........
                mydb.insertGroups(result.getGroupName(),result.getGroupAdminNumber(),result.getIsAccepted(),result.getGroupMember());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }
}


public class GroupActivity extends ActionBarActivity {

    Button btnCreateGroup;
    ExpandableListView groupNameListView;
    String deviceId;
    String Mobile_Number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_hdr_grp_128);
        setContentView(R.layout.activity_group);
        groupNameListView = (ExpandableListView) findViewById(R.id.groupNameListView);
        btnCreateGroup = (Button) findViewById(R.id.btnCreateGroup);
        //get device id from shared preference.....
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        deviceId = prefs.getString("Device Id", null);
        Mobile_Number = prefs.getString("Mobile Number",null);
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
        return super.onOptionsItemSelected(item);
    }

    //this method is used for inviting contacts through social media android app....
    public void inviteContacts() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "here is the link to download TweetLoc");
        startActivity(intent);
    }
}
