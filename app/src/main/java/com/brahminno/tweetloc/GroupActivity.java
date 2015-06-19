package com.brahminno.tweetloc;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.util.ArrayList;

//This Async class is used for getting all information of groups based on device id.......
class GroupDetailsAsyncTask extends AsyncTask<Void,Void,String>{
    private static TweetApi myTweetApi = null;
    private Context context;
    private String Mobile_Number;
    private String Device_Id;
    private String Group_Name;
    private ArrayList<String> Group_Members;
    GroupBean groupBeanDetails;

    public GroupDetailsAsyncTask(Context context,String Device_Id){
        this.context = context;
        this.Device_Id = Device_Id;
    }

    @Override
    protected String doInBackground(Void... params) {
        if (myTweetApi == null) {
            TweetApi.Builder builder = new TweetApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl("https://brahminno.appspot.com/_ah/api/");

            myTweetApi = builder.build();
        }
        try{
            //call group Details Api.....
            groupBeanDetails = myTweetApi.getGRoupDetailUsingKey(Device_Id).execute();
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        return "";
    }
}


public class GroupActivity extends ActionBarActivity {

    Button btnCreateGroup;
    ExpandableListView groupNameListView;
    String group_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_hdr_grp_128);
        setContentView(R.layout.activity_group);
        groupNameListView = (ExpandableListView) findViewById(R.id.groupNameListView);
        btnCreateGroup = (Button) findViewById(R.id.btnCreateGroup);
        btnCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CreateGroupActivity.class);
                startActivity(intent);
            }
        });

        Toast.makeText(getApplicationContext(),group_name,Toast.LENGTH_SHORT).show();
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
        return super.onOptionsItemSelected(item);
    }
}
