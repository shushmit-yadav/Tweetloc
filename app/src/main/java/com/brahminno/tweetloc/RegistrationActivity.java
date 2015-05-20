package com.brahminno.tweetloc;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Pattern;

//This class is for user registration.....
//By Shushmit on 20-05-2015.
//Brahmastra Innovations.
public class RegistrationActivity extends ActionBarActivity {

    TextView tvDeviceId,tvEmail,tvMobileNumber;
    Button btnRegister;

    GPSTracker gps;

    private SQLiteDatabase mydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        tvDeviceId = (TextView) findViewById(R.id.tvDeviceId);
        tvEmail = (TextView) findViewById(R.id.tvEmail);
        tvMobileNumber = (TextView) findViewById(R.id.tvMobileNumber);
        btnRegister = (Button) findViewById(R.id.btnRegister);

        mydb = new SQLiteDatabase(this);
        //On Button Click Event....
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //examinJSONFile();
                //saveData();
            }
        });
        //Check Internet if yes then goto else
        if (!isNetworkAvailable()){
            Toast.makeText(this,"No Internet Connection",Toast.LENGTH_SHORT).show();
        }
        else{
            //check if gps is enabled...
            gps = new GPSTracker(this);
            if (gps.isGPSTeackingEnabled()) {

                Toast.makeText(getApplicationContext(),String.valueOf(gps.latitude),Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(),String.valueOf(gps.longitude),Toast.LENGTH_SHORT).show();

                //Retrieve Device Id...
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                tvDeviceId.setText(getDeviceID(telephonyManager));

                //call method to get email....
                getEmail();

                //Retrieve User Mobile Number.
                String number = getMobileNumber();
                tvMobileNumber.setText(number);
            }
            else{
                gps.showSettingsAlert();
            }
        }
    }

    //Check Internet
    private boolean isNetworkAvailable(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()){
            return true;
        }
        else{
            return false;
        }
    }

    //To get the Unique Device Id...
    String getDeviceID(TelephonyManager manager){
        String id = manager.getDeviceId();
        if(id == null){
            id = "Not Available";
        }
        int phoneType = manager.getPhoneType();
        switch(phoneType){
            case TelephonyManager.PHONE_TYPE_NONE:
                return  "None" + id;

            case TelephonyManager.PHONE_TYPE_GSM:
                return "GSM: IMEI" + id;

            case TelephonyManager.PHONE_TYPE_CDMA:
                return "CDMA: MEID/ESN" + id;

            default:
                return "UNKNOWN: ID" +id;

        }
    }

    //To get Email Id....
    public void getEmail(){
        String primaryEmailID;
        Pattern emailPattern = Patterns.EMAIL_ADDRESS;
        Account[] accounts = AccountManager.get(getApplicationContext()).getAccountsByType("com.google");
        for (Account account : accounts){
            if(emailPattern.matcher(account.name).matches()){
                primaryEmailID = account.name;
                tvEmail.setText(primaryEmailID);
            }
        }
    }

    //This method is for obtaining mobile number...
    public String getMobileNumber(){
        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String strMobileNumber = manager.getLine1Number();
        return strMobileNumber;
    }

    //Generate data into JSON format....
    /*
    void examinJSONFile(){
        JSONObject json = null;
        try{
            json = new JSONObject();
            json.put("Device ID",tvDeviceId.getText().toString());
            json.put("Email ID",tvEmail.getText().toString());
            Log.i("json...",json.toString());
        }
        catch(JSONException je){

        }
    }
    */
    /*
    void saveData(){
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            int Value = extras.getInt("id");
            if(mydb.insertInfo(tvMobileNumber.getText().toString(),tvEmail.getText().toString(),tvDeviceId.getText().toString())){
                Toast.makeText(getApplicationContext(),"Done",Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getApplicationContext(),"Not Done",Toast.LENGTH_SHORT).show();
            }

        }
    }

    */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_registration, menu);
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
