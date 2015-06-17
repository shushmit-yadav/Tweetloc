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

import java.util.regex.Pattern;

//AsynTask class to push registration details on server...
class RegistrationAsyncTask extends AsyncTask<Void,Void,String>{
    private static TweetApi myTweetApi = null;
    private Context context;
    private String Mobile_Number;
    private String Email_ID;
    private String Device_Id;
    RegistrationBean registerUserValidation;
    public RegistrationAsyncTask(Context con,String Mobile_Number,String Email_ID,String Device_Id){
        this.Mobile_Number = Mobile_Number;
        this.Email_ID = Email_ID;
        this.Device_Id = Device_Id;
        this.context = con;
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


            //call registration Details Api.....
            registerUserValidation = myTweetApi.getRegistrationDetailUsingKey(Device_Id).execute();

            //Store details in shared preferince.....
            SharedPreferences.Editor editor = context.getSharedPreferences("MyPrefs", context.MODE_PRIVATE).edit();
            editor.putString("Mobile Number", registerUserValidation.getMobileNumber());
            editor.putString("Email Id", registerUserValidation.getEmailId());
            editor.putString("Device Id", registerUserValidation.getDeviceId());
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
       // Toast.makeText(context,registerUserValidation.getMobileNumber().toString(),Toast.LENGTH_SHORT).show();
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
    String mobNum;
    String primaryEmail;
    String primaryEmailID;
    String deviceID;
    //SQLiteDatabase mydb;
    String strNumber;
    boolean isValidNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //To hide header...
        getSupportActionBar().hide();
        setContentView(R.layout.activity_registration);
        tvTC = (TextView) findViewById(R.id.tvTC);
        ckBoxTC = (CheckBox) findViewById(R.id.ckBoxTC);
        btnRegister = (Button) findViewById(R.id.btnRegister);

        //mydb = new SQLiteDatabase(this);
        //On Button Click Event....
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ckBoxTC.isChecked()){
                    if(!isValidNum){
                        mobNum = showInputDialog();

                    }
                    else{
                        //Save details to SQLite Database.....
                        //mydb.insertInfo(new RegistrationInfo(deviceID,mobNum,primaryEmail));

                        //call method to store registration details on server
                        new RegistrationAsyncTask(getApplicationContext(), mobNum,primaryEmail,deviceID).execute();

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
            //Toast.makeText(getApplicationContext(),deviceID,Toast.LENGTH_SHORT).show();

            //call method to get email....
            primaryEmail = getEmail();
            //Toast.makeText(getApplicationContext(),primaryEmail,Toast.LENGTH_SHORT).show();

            //Retrieve User Mobile Number.
            mobNum = getMobileNumber();
            Log.i("Mobile No...", mobNum);
            isValidNum = validPhone(mobNum);
            Log.i("Valid Number..",""+ isValidNum);
            Toast.makeText(getApplicationContext(), mobNum,Toast.LENGTH_SHORT).show();
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

    //This method is for obtaining mobile mobNum...
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
                Log.i("From Dialog...",strNumber);
                Log.i("From Dialog is valid...",""+validPhone(strNumber));
                isValidNum = validPhone(strNumber);
                if(isValidNum){
                    mobNum = strNumber;
                }
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

    //Regular Expression to check valid mobile mobNum....
    private boolean validPhone(String phone) {
        Pattern pattern = Patterns.PHONE;
        return pattern.matcher(phone).matches();
    }
}
