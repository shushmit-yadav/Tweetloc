package com.brahminno.tweetloc;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

//AsyncTask class to push group details on server.....
class GroupAsyncTask extends AsyncTask<Void,Void,String>{
    private Context context;
    private String Device_Id;
    private String Group_Name;
    private JSONArray Group_Member ;
    private String adminMobileNumber;
    private String responseResult;

    private SQLiteDatabase mydb;

    public GroupAsyncTask(Context context,String Device_Id,String Group_Name,JSONArray Group_Member,String adminMobileNumber){
        this.context = context;
        this.Device_Id = Device_Id;
        this.Group_Name = Group_Name;
        this.Group_Member = Group_Member;
        this.adminMobileNumber = adminMobileNumber;
    }

    @Override
    protected String doInBackground(Void... params) {
        Log.i("inside GroupAsyncTask..", "succussfully.....");
        try {
            //create HttpClient.....
            HttpClient httpClient = new DefaultHttpClient();
            //Http POST request to given url....
            HttpPost httpPost = new HttpPost("http://104.236.27.79:8080/creategroup");
            String json = null;
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("groupAdminMobNo", adminMobileNumber);
                jsonObject.put("groupName",Group_Name);
                jsonObject.put("deviceID", Device_Id);
                jsonObject.put("groupmembers", Group_Member);
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
                //get responseResult from httpResponse......
                responseResult = EntityUtils.toString(httpResponse.getEntity());

                Log.i("responseResult...: ", responseResult);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseResult;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        try{
            Toast.makeText(context,responseResult,Toast.LENGTH_SHORT).show();
            mydb = new SQLiteDatabase(context);
            String isAccepted = "false";
            for(int i = 0; i < Group_Member.length(); i++){
                try {
                    if(adminMobileNumber.equals(Group_Member.getString(i))){
                        isAccepted = "true";
                    }
                    mydb.insertGroups(Group_Name,adminMobileNumber,Group_Member.getString(i),isAccepted);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Intent intent = new Intent(context,GroupActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.getApplicationContext().startActivity(intent);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }
}

//*********************************************************************

//Create Group Activity class starts from here........
public class CreateGroupActivity extends ActionBarActivity {

    EditText etGroupName;
    Button btnCreate;
    String Group_Name;
    String deviceId,Mobile_Number;
    SQLiteDatabase mydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_hdr_add_grp);
        setContentView(R.layout.activity_create_group);
        etGroupName = (EditText) findViewById(R.id.etGroupName);
        btnCreate = (Button) findViewById(R.id.btnCreate);

        mydb = new SQLiteDatabase(this);

        //Get data from shared preference.....
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        deviceId = prefs.getString("Device Id", null);
        Mobile_Number = prefs.getString("Mobile Number",null);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Group_Name = etGroupName.getText().toString();
                boolean result = mydb.checkGroupNameDuplicacy(Group_Name);
                Log.i("result from database..", " " + result);
                Log.i("result from database..", " " + Group_Name);
                if (Group_Name.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Group Name should not null!!!!", Toast.LENGTH_SHORT).show();
                } else if (result == true) {
                    //Call Intent to go another activity....
                    Intent groupIntent = new Intent(getApplicationContext(), AddMemberActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("Group Name", Group_Name);
                    groupIntent.putExtras(bundle);
                    startActivity(groupIntent);
                } else {
                    Toast.makeText(getApplicationContext(), "You are already member of this group!!!!", Toast.LENGTH_SHORT).show();
                }
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
            if(isNetworkAvailable()){
                inviteContacts();
            }
            else{
                Toast.makeText(getApplicationContext(),"Please connect to Internet!!!",Toast.LENGTH_SHORT).show();
            }
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

    //Check Internet
    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }
}
