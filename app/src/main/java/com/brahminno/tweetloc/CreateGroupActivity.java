package com.brahminno.tweetloc;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.brahminno.tweetloc.backend.tweetApi.TweetApi;
import com.brahminno.tweetloc.backend.tweetApi.model.GroupBean;
import com.brahminno.tweetloc.backend.tweetApi.model.RegistrationBean;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

//AsyncTask class to push group details on server.....
class GroupAsyncTask extends AsyncTask<Void,Void,String>{

    private static TweetApi myTweetApi = null;
    private Context context;
    private String Device_Id;
    private String Group_Name;
    private String Mobile_Number;
    private String Group_Member;

    public GroupAsyncTask(Context context,String Device_Id,String Group_Name,String Group_Member,String Mobile_Number){
        context = null;
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
    String Group_Member;
    String deviceId,Mobile_Number;
    RegistrationActivity registrationDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_hdr_add_grp);
        setContentView(R.layout.activity_create_group);
        etGroupName = (EditText) findViewById(R.id.etGroupName);
        btnCreate = (Button) findViewById(R.id.btnCreate);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Group_Name = etGroupName.getText().toString();

                //Call AsyncTask to upload data on server.....
                new GroupAsyncTask(getApplicationContext(),Group_Name,"Group_Member","deviceId","Mobile_Number").execute();
                //Call Intent to go another activity....
                Intent createIntent = new Intent(getApplicationContext(),AddMemberActivity.class);
                startActivity(createIntent);
            }
        });
        /*
        registrationDetail = new RegistrationActivity();
        //Call method to get Mobile Number And device id.......
        Mobile_Number = registrationDetail.getMobileNumber();
        //call method to get device id using telephony manager.....
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        deviceId = registrationDetail.getDeviceID(telephonyManager);
        */
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

        return super.onOptionsItemSelected(item);
    }
}
