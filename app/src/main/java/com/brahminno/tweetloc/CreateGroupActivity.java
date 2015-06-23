package com.brahminno.tweetloc;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.brahminno.tweetloc.backend.tweetApi.TweetApi;
import com.brahminno.tweetloc.backend.tweetApi.model.GroupBean;
import com.brahminno.tweetloc.backend.tweetApi.model.RegistrationBean;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.util.ArrayList;

//AsyncTask class to push group details on server.....
class GroupAsyncTask extends AsyncTask<Void,Void,String>{

    private static TweetApi myTweetApi = null;
    private Context context;
    private String Device_Id;
    private String Group_Name;
    private ArrayList<String> Group_Member ;
    private String Mobile_Number;

    public GroupAsyncTask(Context context,String Device_Id,String Group_Name,ArrayList<String> Group_Member,String Mobile_Number){
        this.context = context;
        this.Device_Id = Device_Id;
        this.Group_Name = Group_Name;
        this.Group_Member = Group_Member;
        this.Mobile_Number = Mobile_Number;
    }

    @Override
    protected String doInBackground(Void... params) {

        if (myTweetApi == null) {
            TweetApi.Builder builder = new TweetApi.Builder(AndroidHttp.newCompatibleTransport(),new AndroidJsonFactory(),null)
                    .setRootUrl("https://brahminno.appspot.com/_ah/api/");

            myTweetApi = builder.build();
        }
        try{
            GroupBean groupBean = new GroupBean();
            groupBean.setGroupName(Group_Name);
            groupBean.setGroupMember(Group_Member);
            groupBean.setDeviceId(Device_Id);
            groupBean.setMobileNumber(Mobile_Number);

            myTweetApi.storeGroup(groupBean).execute();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return "";
    }
}

//*********************************************************************

//Create Group Activity class starts from here........
public class CreateGroupActivity extends ActionBarActivity {

    EditText etGroupName;
    Button btnCreate;
    String Group_Name;
    ArrayList<String> Group_Member;
    String deviceId,Mobile_Number;

    //SQLiteDatabase mydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_hdr_add_grp);
        setContentView(R.layout.activity_create_group);
        etGroupName = (EditText) findViewById(R.id.etGroupName);
        btnCreate = (Button) findViewById(R.id.btnCreate);

        //Get data from shared preference.....
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        deviceId = prefs.getString("Device Id", null);
        Mobile_Number = prefs.getString("Mobile Number",null);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Group_Name = etGroupName.getText().toString();
                //Call AsyncTask to upload data on server.....
                //new GroupAsyncTask(getApplicationContext(),deviceId,Group_Name,"Group_Member",Mobile_Number).execute();

                //save group name to the local app database....
                //mydb.insertGroups(new GroupDetails(Group_Name));
                //display Toast
                Toast.makeText(getApplicationContext(), "values inserted successfully.", Toast.LENGTH_LONG).show();

                //Call Intent to go another activity....
                Intent groupIntent = new Intent(getApplicationContext(),AddMemberActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("Group Name",Group_Name);
                groupIntent.putExtras(bundle);
                startActivity(groupIntent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_group, menu);
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
        if(id == R.id.action_invte){
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
