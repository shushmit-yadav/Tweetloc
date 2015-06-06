package com.brahminno.tweetloc;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.brahminno.tweetloc.backend.tweetApi.TweetApi;
import com.brahminno.tweetloc.backend.tweetApi.model.RegistrationBean;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.regex.Pattern;

//AsynTask class to push registration details on server...
class RegistrationAsyncTask extends AsyncTask<Void,Void,String>{
    private static TweetApi myTweetApi = null;
    private Context context;
    private String Mobile_Number;
    private String Email_ID;
    private String Device_Id;
    public RegistrationAsyncTask(Context context,String Mobile_Number,String Email_ID,String Device_Id){
        this.Mobile_Number = Mobile_Number;
        this.Email_ID = Email_ID;
        this.Device_Id = Device_Id;
        context = null;
    }

    @Override
    protected String doInBackground(Void... params) {
        if (myTweetApi == null) {
            TweetApi.Builder builder = new TweetApi.Builder(AndroidHttp.newCompatibleTransport(),new AndroidJsonFactory(),null)
                    .setRootUrl("https://brahminno.appspot.com/_ah/api/");

            myTweetApi = builder.build();
        }
        try{
            RegistrationBean registrationBean = new RegistrationBean();
            registrationBean.setMobileNumber(Mobile_Number);
            registrationBean.setEmailId(Email_ID);
            registrationBean.setDeviceId(Device_Id);

            myTweetApi.storeRegistration(registrationBean).execute();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        try{
            RegistrationBean registerUserValidation = myTweetApi.getRegistrationDetailUsingKey(Device_Id).execute();

            //Store details in shared preferince.....
            SharedPreferences.Editor editor = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).edit();
            editor.putBoolean("isFirstTime",true);
            editor.putString("Mobile Number",registerUserValidation.getMobileNumber());
            editor.putString("Email Id", registerUserValidation.getEmailId());
            editor.putString("Device Id",registerUserValidation.getDeviceId());
            editor.commit();

        }
        catch (Exception e){
            e.printStackTrace();
        }

        return "";
    }

    @Override
    protected void onPostExecute(String s) {
        //super.onPostExecute(s);
    }
}
//*******************************************************
//This class is for user registration.....
//By Shushmit on 20-05-2015.
//Brahmastra Innovations.
public class RegistrationActivity extends ActionBarActivity {
    TextView tvTC;
    Button btnRegister;
    CheckBox ckBoxTC;
    String number;
    String primaryEmail;
    String primaryEmailID;
    String strNumber;
    String deviceID;
    SQLiteDatabase mydb;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //To hide header...
        getSupportActionBar().hide();
        setContentView(R.layout.activity_registration);
        tvTC = (TextView) findViewById(R.id.tvTC);
        ckBoxTC = (CheckBox) findViewById(R.id.ckBoxTC);
        btnRegister = (Button) findViewById(R.id.btnRegister);



        mydb = new SQLiteDatabase(this);
        //On Button Click Event....
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ckBoxTC.isChecked()){
                    if(number == null){
                        strNumber = showInputDialog();
                        number = strNumber;
                    }
                    else{
                        /*
                        //Store Details in SharedPreference.....
                        SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).edit();
                        editor.putBoolean("isFirstTime",true);
                        editor.putString("Mobile NUmber",number);
                        editor.putString("Email Id", primaryEmail);
                        editor.putString("Device Id",deviceID);
                        editor.commit();
                        */

                        //Save details to SQLite Database.....
                        mydb.insertInfo(new RegistrationInfo(deviceID,number,primaryEmail));

                        //call method to store registration details on server
                        new RegistrationAsyncTask(getApplicationContext(),number,primaryEmail,deviceID).execute();

                        //call intent to open MyTrail Activity....
                        Intent intent = new Intent(getBaseContext(),MyTrail.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("Device_Id",deviceID);
                        intent.putExtras(bundle);
                        startActivity(intent);

                        finish();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(),"Accept Terms and Conditions",Toast.LENGTH_SHORT).show();
                }
            }
        });
        //Check Internet if yes then goto else
        if (!isNetworkAvailable()){
            Toast.makeText(this,"No Internet Connection",Toast.LENGTH_SHORT).show();
        }
        else{
            //Retrieve Device Id...
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            deviceID = getDeviceID(telephonyManager);
            Toast.makeText(getApplicationContext(),deviceID,Toast.LENGTH_SHORT).show();

            //call method to get email....
            primaryEmail = getEmail();
            Toast.makeText(getApplicationContext(),primaryEmail,Toast.LENGTH_SHORT).show();

            //Retrieve User Mobile Number.
            number = getMobileNumber();
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
    private String getDeviceID(TelephonyManager manager){
        String id = manager.getDeviceId();
        if(id == null){
            id = "Not Available";
        }
        int phoneType = manager.getPhoneType();
        switch(phoneType){
            case TelephonyManager.PHONE_TYPE_NONE:
                return  "" + id;

            case TelephonyManager.PHONE_TYPE_GSM:
                return "" + id;

            case TelephonyManager.PHONE_TYPE_CDMA:
                return "" + id;

            default:
                return "" +id;
        }
    }

    //To get Email Id....
    public String getEmail(){

        Pattern emailPattern = Patterns.EMAIL_ADDRESS;
        Account[] accounts = AccountManager.get(getApplicationContext()).getAccountsByType("com.google");
        for (Account account : accounts){
            if(emailPattern.matcher(account.name).matches()){
                primaryEmailID = account.name;
            }
        }
        return primaryEmailID;
    }

    //This method is for obtaining mobile number...
    public String getMobileNumber(){
        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String strMobileNumber = manager.getLine1Number();
        Toast.makeText(getApplicationContext(),strMobileNumber,Toast.LENGTH_SHORT).show();
        return strMobileNumber;
    }

    //If Mobile Number does not fetch automatically.....
    public String showInputDialog(){
        //get prompt.xml view...
        LayoutInflater inflater = LayoutInflater.from(RegistrationActivity.this);
        View promptView = inflater.inflate(R.layout.input_number, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegistrationActivity.this);
        alertDialogBuilder.setView(promptView);

        final EditText etNumber = (EditText) promptView.findViewById(R.id.etNumber);

        //set dialog window...
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                strNumber = etNumber.getText().toString();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialogBuilder.show();
        return strNumber;
    }
}
